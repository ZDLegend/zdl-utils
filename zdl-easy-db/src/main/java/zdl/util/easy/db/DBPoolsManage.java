package zdl.util.easy.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * description
 *
 * @author ZDLegend
 * @date 2019/10/22 15:48
 * @since 1.0
 */
public class DBPoolsManage {
    private static final Logger logger = LoggerFactory.getLogger(DBPoolsManage.class);

    private static final ConcurrentMap<String, DatabaseInterface> POOLS_TABLE = new ConcurrentHashMap<>();

    private DBPoolsManage() {
    }

    public static DatabaseInterface getDBPool(DatabaseConfig config) {
        String key = config.getSingleton();
        if (!POOLS_TABLE.containsKey(key)) {
            DatabaseInterface database = DBConstant.initDBI(config);
            POOLS_TABLE.put(key, database);
        }
        return POOLS_TABLE.get(key);
    }

    public static Connection getConnection(DatabaseConfig config) throws SQLException {
        return getDBPool(config).getConnection();
    }

    public static boolean execute(String sql, DatabaseConfig config) throws SQLException {
        try (Connection conn = getConnection(config);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (null != ps) {
                return ps.execute();
            } else {
                return false;
            }
        }
    }
}
