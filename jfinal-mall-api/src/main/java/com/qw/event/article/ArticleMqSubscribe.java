package com.qw.event.article;

import com.qw.event.RedisConsumer;
import com.qw.model.Article;
import com.jfinal.log.Log;

/**
 * 1. 记录浏览次数
 *
 * @author qw
 */
public class ArticleMqSubscribe implements RedisConsumer {
    private Log log = Log.getLog(getClass());

    @Override
    public void onMsg(String msg) {
        log.info("资讯查看消息队列：" + msg);
        Integer articleId = Integer.valueOf(msg);
        Article article = Article.dao.findById(articleId);
        Integer click = article.getClick();
        if (click == null) {
            click = 0;
        } else {
            click = click + 1;
        }
        article.setClick(click);
        article.update(false);
    }
}