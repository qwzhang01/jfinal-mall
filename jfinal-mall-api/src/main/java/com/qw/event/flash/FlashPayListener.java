package com.qw.event.flash;

import com.qw.conf.QuantityConf;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.event.core.EventListener;
import redis.clients.jedis.Jedis;

/**
 * 秒杀支付以后监听器，直接发起队列
 */
public class FlashPayListener {
    private Log log = Log.getLog(getClass());
    private Cache cache = null;

    public FlashPayListener() {
        this.cache = Redis.use(QuantityConf.BUTLER_QUENE);
    }

    @EventListener({FlashPayEvent.class})
    public void onApplicationEvent(FlashPayEvent flashPayEvent) {
        Jedis jedis = cache.getJedis();
        try {
            jedis.publish(FlashPayMqSubscribe.class.getName(), flashPayEvent.getOrderDetailKey());
        } finally {
            cache.close(jedis);
        }
    }
}