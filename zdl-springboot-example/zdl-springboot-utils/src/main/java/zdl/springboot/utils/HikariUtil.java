package zdl.springboot.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by ZDLegend on 2020/3/26 17:02
 */
@Configuration
@ConditionalOnClass(HikariDataSource.class)
public class HikariUtil {

    @Autowired
    private HikariDataSource hikariDataSource;

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    public void releaseConnection(Connection connection) throws SQLException {
        connection.close();
    }

    public void evictConnection(Connection connection) {
        hikariDataSource.evictConnection(connection);
    }
}
