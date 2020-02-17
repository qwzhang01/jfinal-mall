package com.qw.controller.app.cms;

import cn.qw.base.RestController;
import com.qw.event.article.ScanEvent;
import com.qw.interceptor.RestSecurityInterceptor;
import com.qw.service.frontend.cms.ArticleService;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import net.dreamlu.event.EventKit;

/**
 * 资讯管理
 */
public class ArticleController extends RestController {

    /**
     * @param pageNumber|页码|int|必填
     * @param pageSize|一页条数|int|必填
     * @title 获取资讯列表
     * @respParam id|资讯ID|int|必填
     * @respParam title|资讯标题|String|必填
     * @respParam publishTime|发表时间|String|必填
     * @respParam clickCount|阅读数|int|必填
     * @respParam thumb|缩略图|String|必填
     * @respParam totalRow|总记录数|int|必填
     * @respParam pageNumber|页码|int|必填
     * @respParam pageSize|一页个数|int|必填
     * @respParam lastPage|是否最后一页|boolean|必填
     * @respParam firstPage|是否第一页|boolean|必填
     * @respParam totalPage|总页数|int|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void list() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        Page<Record> page = ArticleService.me().page(pageNumber, pageSize);
        renderJson(page);
    }

    /**
     * @param articleId|资讯ID|String|必填
     * @title 获取资讯详情
     * @respParam articleId|资讯ID|int|必填
     * @respParam title|资讯标题|String|必填
     * @respParam content|资讯内容|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void detail() {
        Integer articleId = getParaToInt("articleId");
        if (articleId == null || articleId <= 0) {
            renderParamNull("参数错误，articleId不能为空");
        }
        EventKit.post(new ScanEvent(articleId));
        renderJson(ArticleService.me().detail(articleId));
    }
}