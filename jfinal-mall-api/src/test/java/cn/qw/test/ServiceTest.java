package cn.qw.test;

import com.qw.model._MappingKit;
import com.jfinal.config.Plugins;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.After;
import org.junit.Before;

public class ServiceTest {
    protected final Log log = Log.getLog(getClass());

    private Plugins plugins;

    @Before
    public void initConfig() {
        long beginTime = System.currentTimeMillis();
        log.error("启动配置数据");
        PropKit.use("config.properties");
        try {
            plugins = new Plugins();
            // 配置数据库连接池插件
            DruidPlugin druid = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("username"),
                    PropKit.get("password"), PropKit.get("driver"));
            plugins.add(druid);
            // 配置ActiveRecord插件
            ActiveRecordPlugin arp = new ActiveRecordPlugin(druid);
            arp.setDialect(new MysqlDialect());
            _MappingKit.mapping(arp);
            arp.setContainerFactory(new CaseInsensitiveContainerFactory());
            plugins.add(arp);
            for (IPlugin plug : plugins.getPluginList()) {
                plug.start();
            }
            log.error("插件启动完成，用时-------" + (System.currentTimeMillis() - beginTime) / 1000 + "秒");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void endConfig() {
        log.error("测试完成，销毁配置数据");
        if (plugins != null) {
            for (IPlugin plug : plugins.getPluginList()) {
                plug.stop();
            }
        }
    }
}
