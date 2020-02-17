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
        record.set("id", article.getArticleId());
        record.set("title", article.getTitle());
        record.set("author", article.getAuthor());
        record.set("keywords", article.getKeywords());
        record.set("thumb", article.getThumb());
        record.set("showThumb", PropKit.get("fileHost") + article.getThumb());
        String content = article.getContent();
        try {
            // TODO 临时解决老数据 新数据不兼容的问题
            Base64Kit.decodeToStr(content);
            record.set("content", content);
        } catch (Exception e) {
            record.set("content", Base64Kit.encode(StringEscapeUtils.unescapeHtml4(content)));
        }
        renderJson(record);
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

    @RequiresPermissions("资讯管理-查看")
    public void save() {
        Integer id = getParaToInt("id");
        String title = getPara("title");
        String author = getPara("author");
        String keywords = getPara("keywords");
        String thumb = getPara("thumb");
        String content = getPara("content");
        Article article = Article.dao.findById(id);
        if (article == null) {
            article = new Article();
        }
        article.setTitle(title);
        article.setThumb(thumb);
        article.setAuthor(author);
        article.setKeywords(keywords);
        article.setContent(content);
        article.setCatId(99);
        article.setAddTime(System.currentTimeMillis() / 1000);
        article.setPublishTime(article.getAddTime().intValue());
        article.saveOrUpdate(false);
        renderSuccess("保存成功");
    }
}