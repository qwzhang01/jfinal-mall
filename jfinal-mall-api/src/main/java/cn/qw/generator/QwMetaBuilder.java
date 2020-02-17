package cn.qw.generator;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MetaBuilder
 */
public class QwMetaBuilder extends MetaBuilder {

    public QwMetaBuilder(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 不同数据库 dbMeta.getTables(...) 的 schemaPattern 参数意义不同 1：oracle 数据库这个参数代表
     * dbMeta.getUserName() 2：postgresql 数据库中需要在 jdbcUrl中配置 schemaPatter，例如：
     * jdbc:postgresql://localhost:15432/djpt?currentSchema=public,system,app
     * 最后的参数就是搜索schema的顺序，DruidPlugin 下测试成功 3：开发者若在其它库中发现工作不正常，可通过继承
     * MetaBuilder并覆盖此方法来实现功能
     */
    @Override
    protected ResultSet getTablesResultSet() throws SQLException {
        String schemaPattern = dialect instanceof OracleDialect ? dbMeta
                .getUserName() : null;
        return dbMeta.getTables(conn.getCatalog(), schemaPattern, null,
                new String[]{"TABLE"});
    }

    /**
     * 构造 modelName，mysql 的 tableName 建议使用小写字母，多单词表名使用下划线分隔，不建议使用驼峰命名 oracle 之下的
     * tableName 建议使用下划线分隔多单词名，无论 mysql还是 oralce，tableName 都不建议使用驼峰命名
     */
    @Override
    protected String buildModelName(String tableName) {
        // 移除表名前缀仅用于生成 modelName、baseModelName，而 tableMeta.name 表名自身不能受影响
        if (removedTableNamePrefixes != null) {
            for (String prefix : removedTableNamePrefixes) {
                if (tableName.contains(prefix)) {
                    tableName = tableName.replace(prefix, "");
                }
            }
        }

        // 将 oralce 大写的 tableName 转成小写，再生成 modelName
        if (dialect instanceof OracleDialect) {
            tableName = tableName.toLowerCase();
        }

        return StrKit.firstCharToUpperCase(StrKit.toCamelCase(tableName));
    }
}
