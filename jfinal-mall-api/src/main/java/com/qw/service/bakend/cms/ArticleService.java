package com.qw.service.bakend.cms;

import cn.qw.base.BaseService;
import cn.qw.mongo.MongoKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.model.Article;
import com.qw.model.ArticleCat;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ArticleService extends BaseService {
    private static ArticleService service;

    private ArticleService() {
    }

    public static synchronized ArticleService me() {
        if (service == null) {
            service = new ArticleService();
        }
        return service;
    }

    /**
     * 将mysql中的数据全部转到mongodb
     */
    public void init(){
        String sql = "SELECT * FROM butler_article";
        List<Article> articles = Article.dao.find(sql);
        articles.stream().forEach(a->{
            Integer catId = a.getCatId();
            ArticleCat cat = ArticleCat.dao.findById(catId);
            Record record = a.toRecord();
            record.remove("cat_id");
            Record catRecord = new Record();
            catRecord.set("cat_id", cat.getCatId());
            catRecord.set("cat_name", cat.getCatName());
            record.set("cat", catRecord.getColumns());

            Document document = MongoKit.INSTANCE.toDocument(record.getColumns());
            MongoKit.INSTANCE.insert("content", document);
        });
    }

    public Page<Record> pageList(int pageNumber, int pageSize, String title) {
        String select = "SELECT c.cat_name catName, a.article_id id, a.title, a.author, a.thumb, a.click clickCount";
        select += ", FROM_UNIXTIME(a.add_time, '%Y-%m-%d %H:%i') addTime, FROM_UNIXTIME(a.publish_time, '%Y-%m-%d %H:%i') publishTime";
        String from = " FROM butler_article a ";
        from += " LEFT JOIN butler_article_cat c ON c.cat_id = a.cat_id";
        from += " WHERE 1=1";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(title)) {
            from += " AND a.title LIKE ?";
            paras.add("%" + title + "%");
        }
        from += " ORDER BY a.article_id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            String thumb = r.getStr("thumb");
            if (StrKit.notBlank(thumb)) {
                r.set("thumb", PropKit.get("fileHost") + thumb);
            }
        }
        return page;
    }
}
