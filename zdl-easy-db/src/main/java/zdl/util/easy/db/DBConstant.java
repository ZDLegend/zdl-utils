package zdl.util.easy.db;

import zdl.util.easy.db.druid.DruidDatabase;
import zdl.util.easy.db.hikari.HikariDatabase;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/25/ 16:51
 */
public class DBConstant {

    public static final Map<String, String[]> DB_TABLE = new HashMap<>();
    public static final Map<String, Class<? extends DatabaseInterface>> POOL_TABLE = new HashMap<>();

    public static final String HIKARI = "hikari";
    public static final String DRUID = "druid";

    static {
        DB_TABLE.put("oracle", new String[]{"oracle.jdbc.driver.OracleDriver", "jdbc:%s:thin:@%s:%d:orcl", "/* ping */ select 1 from dual"});
        DB_TABLE.put("mysql", new String[]{"com.mysql.jdbc.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});
        DB_TABLE.put("sqlserver", new String[]{"com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:%s://%s:%d;DatabaseName=%s", "/* ping */ select 1"});
        DB_TABLE.put("postgresql", new String[]{"org.postgresql.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});
        DB_TABLE.put("greenplum", new String[]{"org.postgresql.Driver", "jdbc:%s://%s:%d/%s", "/* ping */ select 1"});

        POOL_TABLE.put(HIKARI, HikariDatabase.class);
        POOL_TABLE.put(DRUID, DruidDatabase.class);
    }

    public static DatabaseInterface initDBI(DatabaseConfig databaseConfig) {
        Class<? extends DatabaseInterface> clazz = POOL_TABLE.get(databaseConfig.getUsedPool());
        try {
            return clazz.getDeclaredConstructor(DatabaseConfig.class).newInstance(databaseConfig);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
