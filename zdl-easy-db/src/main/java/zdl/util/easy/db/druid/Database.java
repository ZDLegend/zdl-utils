package zdl.util.easy.db.druid;

import com.alibaba.druid.pool.DruidDataSource;
import zdl.util.easy.db.DatabaseSource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * description
 *
 * @author [zhang.zhiming@unisinsight.com]
 * @date 2019/10/22 15:48
 * @since 1.0
 */
public class Database extends DruidDataSource implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final Map<String, String[]> DB_TABLE = new HashMap<>();
    private static String dbType;

    static {
        DB_TABLE.put("oracle", new String[]{"oracle.jdbc.driver.OracleDriver", "jdbc:%s:thin:@%s:%d:orcl", "/* ping */ select 1 from dual"});
        DB_TABLE.put("mysql", new String[]{"com.mysql.jdbc.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});
        DB_TABLE.put("sqlserver", new String[]{"com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:%s://%s:%d;DatabaseName=%s", "/* ping */ select 1"});
        DB_TABLE.put("postgresql", new String[]{"org.postgresql.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});
        DB_TABLE.put("greenplum", new String[]{"org.postgresql.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});
    }

    public Database(final DatabaseSource databaseSource) {
        super();

        //设置连接参数
        dbType = databaseSource.getDbType();

        if (!DB_TABLE.containsKey(dbType)) {
            String message = "数据库类型非法，只能为下列值：" + DB_TABLE.keySet();
            throw new IllegalArgumentException(message);
        }

        String url = String.format(DB_TABLE.get(dbType)[1],
                dbType,
                databaseSource.getHost(),
                databaseSource.getPort(),
                databaseSource.getDataBaseName());
        setUrl(url);
        setDriverClassName(DB_TABLE.get(dbType)[0]);
        setUsername(databaseSource.getUserName());
        setPassword(databaseSource.getPassword());

        //配置初始化大小、最小、最大
        setInitialSize(1);
        setMinIdle(3);
        setMaxActive(databaseSource.getMaxConnection());

        //连接泄漏监测
        setRemoveAbandoned(true);
        setRemoveAbandonedTimeout(1200);

        //打开PSCache，并且指定每个连接上PSCache的大小
        setPoolPreparedStatements(true);
        setMaxPoolPreparedStatementPerConnectionSize(200);

        //配置获取连接等待超时的时间（单位：毫秒）
        //dataSource.setMaxWait(300000);

        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接（单位：毫秒）
        setTimeBetweenEvictionRunsMillis(600000);

        //防止过期
        setValidationQuery(DB_TABLE.get(dbType)[2]);
        setTestWhileIdle(true);
        setTestOnBorrow(true);
    }

    public Database(final String dbType, final String host, final int port, final String database,
                    final String user, final String password, final int maxConnection) {
        super();

        //设置连接参数
        Database.dbType = dbType;
        if (!DB_TABLE.containsKey(dbType)) {
            String message = "数据库类型非法，只能为下列值：" + DB_TABLE.keySet();
            throw new IllegalArgumentException(message);
        }
        String url = String.format(DB_TABLE.get(dbType)[1], dbType, host, port, database);
        setUrl(url);
        setDriverClassName(DB_TABLE.get(dbType)[0]);
        setUsername(user);
        setPassword(password);

        //配置初始化大小、最小、最大
        setInitialSize(1);
        setMinIdle(3);
        setMaxActive(maxConnection);

        //连接泄漏监测
        setRemoveAbandoned(true);
        setRemoveAbandonedTimeout(1200);

        //打开PSCache，并且指定每个连接上PSCache的大小
        setPoolPreparedStatements(true);
        setMaxPoolPreparedStatementPerConnectionSize(200);

        //配置获取连接等待超时的时间（单位：毫秒）
        //dataSource.setMaxWait(300000);

        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接（单位：毫秒）
        setTimeBetweenEvictionRunsMillis(600000);

        //防止过期
        setValidationQuery(DB_TABLE.get(dbType)[2]);
        setTestWhileIdle(true);
        setTestOnBorrow(true);
    }

    public Database(final Properties props) {
        this(props.getProperty("db.type"),
                props.getProperty("db.host"),
                Integer.parseInt(props.getProperty("db.port")),
                props.getProperty("db.database"),
                props.getProperty("db.user"),
                props.getProperty("db.password"),
                Integer.parseInt(props.getProperty("db.maxConnection")));
    }

    public boolean allowsOnDuplicateKey() {
        return true;
    }

    public String getDBType() {
        return dbType;
    }
}
