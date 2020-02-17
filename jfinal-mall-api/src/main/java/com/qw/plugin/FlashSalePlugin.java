package com.qw.plugin;

import com.qw.conf.QuantityConf;
import com.qw.conf.module.FlashConf;
import com.qw.service.frontend.prom.FlashSaleService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.redis.Redis;
import org.apache.commons.lang3.exception.ExceptionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀插件
 *
 * @author qw
 */
public class FlashSalePlugin implements IPlugin {
    public final static String LISTENER_PATTERN = "__keyevent@*__:expired";
    private Log log = Log.getLog(getClass());
    private ExecutorService pool = null;
    private Jedis jedis = null;

    public FlashSalePlugin() {
        this.pool = new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("redis-flash-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public boolean start() {
        jedis = Redis.use(QuantityConf.FLASH_SALE).getJedis();
        pool.execute(() -> jedis.psubscribe(new JedisPubSub() {
            @Override
            public void onPSubscribe(String pattern, int subscribedChannels) {
                log.info("秒杀过期相关的存储健监听成功，onPSubscribe " + pattern + " " + subscribedChannels);
            }

            /**
             * 过期后的监听
             * @param pattern  __keyevent@*__:expired
             * @param channel __keyevent@5__:expired - 渠道
             * @param message 过期的健
             */
            @Override
            public void onPMessage(String pattern, String channel, String message) {
                // 这里监听 QuantityConf.FLASH_SALE 这个redis数据库里面的过期事件，删除没有，只有过期会通知
                log.info("有对应的过期：" + "pattern：" + pattern + "；channel：" + channel + "；message：" + message);
                if (message.startsWith(FlashConf.FLASH_ORDER_EXPIRE_KEY_PREFIX)) {
                    String flashGoodIdOrderSn = message.replaceAll(FlashConf.FLASH_ORDER_EXPIRE_KEY_PREFIX, "");
                    log.error("秒杀订单过期（用户ID + 秒杀商品ID + 秒杀订单主编号）: " + flashGoodIdOrderSn);
                    try {
                        String[] split = flashGoodIdOrderSn.split("-");
                        int userId = Integer.parseInt(split[0]);
                        int flashGoodId = Integer.parseInt(split[1]);
                        String masterOrderSn = split[2];
                        // 具体逻辑
                        FlashSaleService.me().orderExpire(userId, flashGoodId, masterOrderSn);
                    } catch (Exception e) {
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }
        }, LISTENER_PATTERN));
        return true;
    }

    @Override
    public boolean stop() {
        if (null != pool) {
            pool.shutdown();
            pool = null;
        }
        jedis.close();
        return true;
    }
}