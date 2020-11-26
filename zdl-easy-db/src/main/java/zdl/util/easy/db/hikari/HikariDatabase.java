package zdl.util.easy.db.hikari;

import com.zaxxer.hikari.HikariDataSource;
import zdl.util.easy.db.DatabaseConfig;
import zdl.util.easy.db.DatabaseInterface;

import java.io.Serializable;

import static zdl.util.easy.db.DBConstant.DB_TABLE;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/25/ 16:56
 */
public class HikariDatabase extends HikariDataSource implements Serializable, DatabaseInterface {

    private static String dbType;

    public HikariDatabase(final DatabaseConfig databaseConfig) {
        super();

        dbType = databaseConfig.getDbType();

        if (!DB_TABLE.containsKey(dbType)) {
            String message = "数据库类型非法，只能为下列值：" + DB_TABLE.keySet();
            throw new IllegalArgumentException(message);
        }

        String url = databaseConfig.getDBUrl();
        setJdbcUrl(url);
        setDriverClassName(DB_TABLE.get(dbType)[0]);
        setUsername(databaseConfig.getUserName());
        setPassword(databaseConfig.getPassword());
        setMaximumPoolSize(databaseConfig.getMaxConnection());
        setConnectionTimeout(databaseConfig.getTimeOutMs());
    }
}
