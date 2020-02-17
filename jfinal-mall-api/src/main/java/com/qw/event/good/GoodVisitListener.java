package com.qw.event.good;

import cn.qw.kit.JsonKit;
import com.qw.conf.QuantityConf;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.event.core.EventListener;
import redis.clients.jedis.Jedis;

/**
 * 商品浏览后的相关逻辑,具体业务转消息队里
 */
public class GoodVisitListener {
    private Log log = Log.getLog(getClass());
    private Cache cache = null;

    public GoodVisitListener() {
        this.cache = Redis.use(QuantityConf.BUTLER_QUENE);
    }

    @EventListener({GoodVisitEvent.class})
    public void onApplicationEvent(GoodVisitEvent event) {
        Jedis jedis = cache.getJedis();
        try {
            // 发布消息
            jedis.publish(GoodVisitMqSubscribe.class.getName(), JsonKit.toJson(event));
        } finally {
            cache.close(jedis);
        }
    }
}