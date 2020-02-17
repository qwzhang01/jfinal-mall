package com.qw;

import cn.qw.entity.ApiResult;
import cn.qw.es.ESPlugin;
import cn.qw.json.ButlerJsonFactory;
import cn.qw.mongo.MongoPlugin;
import cn.qw.rabbitmq.RabbitMQPlugin;
import cn.qw.shiro.ShiroMethod;
import cn.qw.shiro.ShiroPlugin;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jfinal.config.*;
import com.jfinal.core.Const;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.JsonRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.cache.RedisAccessTokenCache;
import com.qw.conf.QuantityConf;
import com.qw.interceptor.CorsInterceptor;
import com.qw.model._MappingKit;
import com.qw.plugin.FastDfsPlugin;
import com.qw.plugin.FlashSalePlugin;
import com.qw.plugin.RedisPublishPlugin;
import com.qw.plugin.XxlJobPlugin;
import net.dreamlu.event.EventPlugin;
import redis.clients.jedis.Protocol;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QwConfig extends JFinalConfig {
    private Routes routes;

    @Override
    public void configConstant(Constants me) {
        PropKit.use("config.properties");
        me.setDevMode(PropKit.getBoolean("devMode", false));

        RenderManager.me().setRenderFactory(new RenderFactory() {
            @Override
            public Render getErrorRender(int errorCode) {
                String msg = "系统未知错误";
                if (500 == errorCode) {
                    msg = "系统出现500异常";
                } else if (404 == errorCode) {
                    msg = "地址不存在";
                } else if (403 == errorCode) {
                    msg = "不允许访问";
                }
                return new JsonRender(new ApiResult(null, errorCode, msg));
            }
        });
        me.setBaseUploadPath(PropKit.get("file_path"));
        me.setMaxPostSize(50 * Const.DEFAULT_MAX_POST_SIZE);
        me.setJsonFactory(ButlerJsonFactory.me());
        me.setJsonDatePattern("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public void configRoute(Routes me) {
        me.add(new QwRestRoutes());
        me.add(new QwWebRoutes());
        this.routes = me;
    }

    @Override
    public void configEngine(Engine me) {

    }

    @Override
    public void configPlugin(Plugins me) {
        // 配置数据库连接池插件
        DruidPlugin druid = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("username"),
                PropKit.get("password"), PropKit.get("driver"));
        druid.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setLogViolation(true);
        wall.setThrowException(false);
        druid.addFilter(wall);

        // 配置log插件
        Log4jFilter log = new Log4jFilter();
        log.setStatementLogEnabled(false);
        log.setStatementLogErrorEnabled(true);
        log.setStatementExecutableSqlLogEnable(true);
        log.setStatementParameterSetLogEnabled(true);
        druid.addFilter(log);
        me.add(druid);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druid);
        arp.setDialect(new MysqlDialect());
        _MappingKit.mapping(arp);
        arp.setContainerFactory(new CaseInsensitiveContainerFactory());
        me.add(arp);

        // EhCache 缓存插件
        me.add(new EhCachePlugin());
        // shiro权限配置
        me.add(new ShiroPlugin(routes));

        // Redis 配置
        String host = PropKit.get("redis_host");
        String password = PropKit.get("redis_pwd");
        RedisPlugin mobileCodeRedis = null;
        RedisPlugin loginErrorRedis = null;
        RedisPlugin tokenRedis = null;
        RedisPlugin paramRedis = null;
        RedisPlugin queue = null;
        RedisPlugin flashSale = null;
        // 验证码缓存
        mobileCodeRedis = new RedisPlugin(QuantityConf.MOBILE_CODE, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 0);
        mobileCodeRedis.setTestWhileIdle(true);
        mobileCodeRedis.getJedisPoolConfig().setTestOnBorrow(true);
        // 密码错误信息
        loginErrorRedis = new RedisPlugin(QuantityConf.LOGIN_ERROR, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 1);
        loginErrorRedis.setTestWhileIdle(true);
        loginErrorRedis.getJedisPoolConfig().setTestOnBorrow(true);
        // TOKEN 缓存
        tokenRedis = new RedisPlugin(QuantityConf.TOKEN_REDIS, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 2);
        tokenRedis.setTestWhileIdle(true);
        tokenRedis.getJedisPoolConfig().setTestOnBorrow(true);
        // 参数缓存
        paramRedis = new RedisPlugin(QuantityConf.PARAM_CATCHE, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 3);
        paramRedis.setTestWhileIdle(true);
        paramRedis.getJedisPoolConfig().setTestOnBorrow(true);
        // 消息队列
        queue = new RedisPlugin(QuantityConf.BUTLER_QUENE, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 4);
        queue.setTestWhileIdle(true);
        queue.getJedisPoolConfig().setTestOnBorrow(true);
        // 秒杀
        flashSale = new RedisPlugin(QuantityConf.FLASH_SALE, host, Protocol.DEFAULT_PORT,
                Protocol.DEFAULT_TIMEOUT, password, 5);
        flashSale.setTestWhileIdle(true);
        flashSale.getJedisPoolConfig().setTestOnBorrow(true);

        me.add(mobileCodeRedis);
        me.add(loginErrorRedis);
        me.add(tokenRedis);
        me.add(paramRedis);
        me.add(queue);
        me.add(flashSale);

        // 配置监听器
        ExecutorService pool = new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("event-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
        EventPlugin eventPlugin = new EventPlugin(false, "com.qw", true);
        eventPlugin.threadPool(pool);
        me.add(eventPlugin);

        // 消息队列
        me.add(new RedisPublishPlugin());
        // 秒杀插件
        me.add(new FlashSalePlugin());
        // 定时任务插件
        me.add(new XxlJobPlugin());
        // 加载文件系统插件
        me.add(new FastDfsPlugin());

        // ESPlugin - es 存放查看的商品的数据
        me.add(new ESPlugin(PropKit.get("es-ip"), Integer.parseInt(PropKit.get("es-port")), PropKit.get("es-cluster-name")));
        // MongoPlugin -  存放商品浏览记录，短信、通知等消息记录，APP显示类配置数据，cms信息
        me.add(new MongoPlugin().add(PropKit.get("mongo-host"), PropKit.getInt("mongo-port")).setDatabase(PropKit.get("mongo-database")));
        //
        me.add(new RabbitMQPlugin(PropKit.get("rabitmq-host"), PropKit.getInt("rabitmq-port"), PropKit.get("rabitmq-username"), PropKit.get("rabitmq-password"), PropKit.get("rabitmq-virtual-host")));
    }

    @Override
    public void configInterceptor(Interceptors me) {
        me.add(new CorsInterceptor());
    }

    @Override
    public void configHandler(Handlers me) {
        DruidStatViewHandler dvh = new DruidStatViewHandler("/druid",
                request -> ShiroMethod.hasRole("系统管理员"));
        me.add(dvh);
    }

    @Override
    public void onStart() {
        // 加载微信配置
        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关参数
        ac.setToken(PropKit.get("wxa_token"));
        ac.setAppId(PropKit.get("wxa_appId"));
        ac.setAppSecret(PropKit.get("wxa_appSecret"));
        ac.setEncryptMessage(PropKit.getBoolean("wxa_encryptMessage", false));
        ac.setEncodingAesKey(PropKit.get("wxa_encodingAesKey"));

        // 多个公众号时，重复调用ApiConfigKit.putApiConfig(ac)依次添加即可，第一个添加的是默认。
        ApiConfigKit.putApiConfig(ac);
        // redis存储access_token、js_ticket
        ApiConfigKit.setAccessTokenCache(new RedisAccessTokenCache(QuantityConf.TOKEN_REDIS));
    }
}