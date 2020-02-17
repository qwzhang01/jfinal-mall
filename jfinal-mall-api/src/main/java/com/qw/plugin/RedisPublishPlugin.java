package com.qw.plugin;

import com.qw.conf.QuantityConf;
import com.qw.event.RedisConsumer;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.redis.Redis;
import net.dreamlu.utils.ClassUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * redis 做消息队列的插件,redis消息监听插件
 *
 * @author qw
 */
public class RedisPublishPlugin implements IPlugin {

    private Log log = Log.getLog(getClass());
    private Jedis jedis;
    private ExecutorService pool = null;

    public RedisPublishPlugin() {
        this.pool = new ThreadPoolExecutor(2, 50, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("redismq-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public boolean start() {
        this.jedis = Redis.use(QuantityConf.BUTLER_QUENE).getJedis();
        Set<Class<?>> classSet = ClassUtil.scanPackageBySuper("com.qw.event", false, RedisConsumer.class);
        if (classSet != null && classSet.size() > 0) {
            String[] channels = classSet.stream().map(c -> c.getName()).toArray(String[]::new);
            pool.execute(() -> {
                jedis.subscribe(new JedisPubSub() {

                    @Override
                    public void onSubscribe(String channel, int subscribedChannels) {
                        log.info("消息队列订阅成功，onSubscribe " + channel + " " + subscribedChannels);
                    }

                    @Override
                    public void onMessage(String channel, String message) {
                        try {
                            Class<?> consumer = Class.forName(channel);
                            Object obj = consumer.newInstance();
                            consumer.getMethod("onMsg", String.class).invoke(obj, message);
                        } catch (ClassNotFoundException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } catch (IllegalAccessException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } catch (InstantiationException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } catch (NoSuchMethodException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        } catch (InvocationTargetException e) {
                            log.error(ExceptionUtils.getStackTrace(e));
                        }
                    }
                }, channels);
            });
        }
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