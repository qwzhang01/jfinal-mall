package com.qw.controller.web.cms;

import cn.qw.base.RestController;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qw.model.Article;
import com.qw.service.bakend.cms.ArticleService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.Date;

@RequiresAuthentication
@RequiresPermissions("资讯管理")
public class ArticleController extends RestController {
    @RequiresPermissions("资讯管理-查看")
    public void pageList() {
        String title = getPara("title");
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> pageList = ArticleService.me().pageList(pageNumber, pageSize, title);
        renderJson(pageList);
    }

    @RequiresPermissions("资讯管理-查看")
    public void detail() {
        int id = getParaToInt("id", 0);
        Article article = Article.dao.findById(id);
        Record record = new Record();
        record.set("id", article.getId());
        record.set("title", article.getTitle());
        record.set("author", article.getAuthor());
        record.set("keyword", article.getKeyword());
        record.set("thumb", article.getThumb());
        renderJson(record);
    }
    @RequiresPermissions("资讯管理-查看")
    public void save() {
        int id = getParaToInt("id", 0);
        String title = getPara("title");
        String author = getPara("author");
        String keywords = getPara("keyword");
        String thumb = getPara("thumb");
        String content = getPara("content");
        Article article = Article.dao.findById(id);
        if (article == null) {
            article = new Article();
        }
        article.setTitle(title);
        article.setThumb(thumb);
        article.setAuthor(author);
        article.setKeyword(keywords);
        article.setContent(content);
        article.setCatId(24);
        article.setAddTime(new Date());
        article.saveOrUpdate(false);
        renderSuccess("保存成功");
    }
    @RequiresPermissions("资讯管理-查看")
    public void delete() {
        int id = getParaToInt("id", 0);
        Article article = Article.dao.findById(id);
        if (article == null) {
            renderParamError("参数错误，资讯不存在");
            return;
        }
        article.delete();
        renderSuccess("删除成功");
    }
}