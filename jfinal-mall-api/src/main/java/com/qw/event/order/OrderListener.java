package com.qw.event.order;

import com.qw.conf.QuantityConf;
import com.jfinal.log.Log;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.event.core.EventListener;
import redis.clients.jedis.Jedis;

/**
 * 提交订单监听器，提交订单后，给队列发送消息
 */
public class OrderListener {
    private Log log = Log.getLog(getClass());
    private Cache cache = null;

    public OrderListener() {
        this.cache = Redis.use(QuantityConf.BUTLER_QUENE);
    }

    @EventListener({OrderEvent.class})
    public void onApplicationEvent(OrderEvent event) {
        int orderId = (int) event.getSource();
        Jedis jedis = cache.getJedis();
        try {
            jedis.publish(OrderMqSubscribe.class.getName(), String.valueOf(orderId));
        } finally {
            cache.close(jedis);
        }
    }
}