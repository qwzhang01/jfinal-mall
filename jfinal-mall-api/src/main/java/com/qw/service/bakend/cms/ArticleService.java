package com.qw.service.bakend.cms;

import cn.qw.base.BaseService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

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

    public Page<Record> pageList(int pageNumber, int pageSize, String title) {
        StringBuilder select = new StringBuilder("SELECT c.cat_name catName,a.id, a.title, a.author, a.keyword, a.thumb, a.click, a.add_time addTime");
        StringBuilder from = new StringBuilder(" FROM cms_article a");
        from.append(" LEFT JOIN cms_article_cat c ON a.cat_id = c.id");
        from.append(" WHERE 1=1");

        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(title)) {
            from.append(" AND a.title LIKE ?");
            paras.add("%" + title + "%");
        }
        from.append(" ORDER BY a.id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        return page;
    }
}
