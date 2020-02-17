package com.qw.util;

import cn.qw.generator.QwBaseModelGenerator;
import cn.qw.generator.QwMetaBuilder;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.activerecord.generator.ModelGenerator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * GeneratorDemo
 */
public class QwGenerator {

    public static DataSource getDataSource() {
        Prop p = PropKit.use("config.properties");
        DruidPlugin druid = new DruidPlugin(p.get("jdbcUrl"),
                p.get("username"), p.get("password"), p.get("driver"));
        druid.start();
        return druid.getDataSource();
    }

    public static void main(String[] args) {
        // base model 所使用的包名
        String baseModelPackageName = "com.qw.model.base";
        // base model 文件保存路径
        String baseModelOutputDir = System.getProperty("user.dir")
                + "/src/main/java/com/qw/model/base";

        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.qw.model";
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        // 创建生成器
        Generator gernerator = new Generator(getDataSource(),
                new QwBaseModelGenerator(baseModelPackageName,
                        baseModelOutputDir), new ModelGenerator(
                modelPackageName, baseModelPackageName, modelOutputDir));
        gernerator.setMetaBuilder(new QwMetaBuilder(getDataSource()));
        gernerator.setDialect(new MysqlDialect());
        // 设置是否在 Model 中生成 dao 对象
        gernerator.setGenerateDaoInModel(true);
        // 设置生成备注
        gernerator.setGenerateRemarks(true);
        // 设置是否生成字典文件
        gernerator.setGenerateDataDictionary(true);
        // 设置需要被移除的表名前缀用于生成modelName。
        gernerator.setRemovedTableNamePrefixes("_tb", "app_", "oms_",
                "global_", "rbac_", "pub_", "mkt_", "im_", "butler_");
        // 生成
        gernerator.generate();
    }
}