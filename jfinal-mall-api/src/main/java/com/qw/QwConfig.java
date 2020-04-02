package com.qw;

import cn.qw.entity.ApiResult;
import cn.qw.json.ButlerJsonFactory;
import cn.qw.plugin.event.AsyncEventPlugin;
import cn.qw.shiro.ShiroMethod;
import cn.qw.shiro.ShiroPlugin;
import com.alibaba.druid.filter.logging.Log4jFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.*;
import com.jfinal.core.Const;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.JsonRender;
import com.jfinal.render.Render;
import com.jfinal.render.RenderFactory;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import com.qw.model._MappingKit;

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
        me.add(new AsyncEventPlugin());
    }

    @Override
    public void configInterceptor(Interceptors me) {
    }

    @Override
    public void configHandler(Handlers me) {
        DruidStatViewHandler dvh = new DruidStatViewHandler("/druid",
                request -> ShiroMethod.hasRole("系统管理员"));
        me.add(dvh);
    }
}