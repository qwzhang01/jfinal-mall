package com.qw.event.article;

import com.qw.conf.QuantityConf;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.event.core.EventListener;
import redis.clients.jedis.Jedis;

/**
 * 浏览资讯以后的监听器，具体业务转给
 */
public class ScanListener {
    private Log log = Log.getLog(getClass());
    private Cache cache = null;

    public ScanListener() {
        this.cache = Redis.use(QuantityConf.BUTLER_QUENE);
    }

    @EventListener({ScanEvent.class})
    public void onApplicationEvent(ScanEvent scanEvent) {
        int articleId = scanEvent.getArticleId();
        Jedis jedis = cache.getJedis();
        try {
            jedis.publish(ArticleMqSubscribe.class.getName(), String.valueOf(articleId));
        } finally {
            cache.close(jedis);
        }
    }
}