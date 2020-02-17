package com.qw.service.frontend.cms;

import cn.qw.base.BaseService;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.commons.lang3.StringEscapeUtils;

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
     * 获取资讯分页数据
     */
    public Page<Record> page(int pageNumber, int pageSize) {
        String select = "SELECT article_id id, title, FROM_UNIXTIME(publish_time, '%Y-%m-%d') publishTime, click clickCount, CONCAT('" + PropKit.get("fileHost") + "', thumb) thumb";
        String from = " FROM butler_article WHERE cat_id = 99 ORDER BY article_id DESC";
        return Db.paginate(pageNumber, pageSize, select, from);
    }

    /**
     * 获取首页资讯，已弃用
     */
    @Deprecated
    public List<Record> findIndex() {
        int catId = 99;
        String field = "article_id, title, '1' jump_type";
        int count = 5;
        return Db.find("SELECT " + field + " FROM butler_article WHERE is_open = 1 AND publish_time < ? AND cat_id = ? ORDER BY publish_time desc, article_id DESC LIMIT  ?",
                System.currentTimeMillis() / 1000, catId, count);
    }

    /**
     * 新版获取首页资讯
     */
    public List<Record> findHome() {
        int catId = 99;
        int count = 5;
        return Db.find("SELECT article_id id, title FROM butler_article WHERE is_open = 1 AND publish_time < ? AND cat_id = ? ORDER BY publish_time desc, article_id DESC LIMIT  ?",
                System.currentTimeMillis() / 1000, catId, count);
    }

    /**
     * 获取资讯详情
     */
    public Record detail(Integer articleId) {
        String sql = "SELECT article_id articleId, title, content FROM butler_article WHERE article_id = ? LIMIT 1";
        Record detail = Db.findFirst(sql, articleId);
        if (detail == null) {
            return null;
        }
        String content = detail.getStr("content");
        try {
            String decode = Base64Kit.decodeToStr(content);
            detail.set("content", decode);
        } catch (IllegalArgumentException e) {
            String html4 = StringEscapeUtils.unescapeHtml4(content);
            detail.set("content", html4);
        }
        return detail;
    }

    public List<Record> search(String title) {
        List<Record> list = new ArrayList<>();
        for (int i = 1; list.size() < 10; i++) {
            list.addAll(search(title, i, 10 - list.size()));
            if (i == 5) {
                break;
            }
        }
        if (list.size() < 10) {
            list.addAll(search(title, 10 - list.size()));
        }
        return list;
    }

    private List<Record> search(String title, int num) {
        String sql = "SELECT article.article_id `key`, article.title `value` FROM butler_article article"
                + " WHERE article.cat_id = 99 AND article.title LIKE ? ORDER BY article.title ASC LIMIT " + num;
        List<Record> list = Db.find(sql, title + "%");
        return list;
    }

    private List<Record> search(String title, int startPos, int num) {
        String sql = "SELECT * FROM("
                + "SELECT article_id `key`, title `value`, cat_id,"
                + "substring(trim(title), "
                + startPos
                + ") AS subName FROM butler_article"
                + ") as good"
                + " WHERE good.cat_id = 99 AND good.subName LIKE ? ORDER BY good.subName ASC LIMIT " + num;
        List<Record> list = Db.find(sql, title + "%");
        return list;
    }
}