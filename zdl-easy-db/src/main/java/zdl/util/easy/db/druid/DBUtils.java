package zdl.util.easy.db.druid;

import org.apache.commons.dbutils.DbUtils;
import zdl.util.easy.db.DatabaseSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * description
 *
 * @author [zhang.zhiming@unisinsight.com]
 * @date 2019/10/22 15:48
 * @since 1.0
 */
public class DBUtils {
    private static final long serialVersionUID = 1L;

    private static DBUtils instance;
    private static Database source;

    private static Connection conn = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static String lastSQL = "";

    public DBUtils(DatabaseSource config) {
        this.source = new Database(config);
        try {
            this.conn = this.source.getConnection();
        } catch (Exception e) {
            System.out.println("连接数据库报错：" + e.getMessage());
        }
    }

    /**
     * 返回类单实例
     *
     * @param config
     * @return
     */
    public static DBUtils getInstance(DatabaseSource config) {
        if (instance == null) {
            synchronized (DBUtils.class) {
                if (instance == null) {
                    instance = new DBUtils(config);
                }
            }
        }
        return instance;
    }

    /**
     * 执行更新SQL，同一时刻只有一个线程执行
     *
     * @param sql
     * @param parmas
     * @return
     */
    public int update(String sql, Object... parmas) {
        //互斥：同一时刻只有一个线程执行
        synchronized (DBUtils.class) {
            return this.update(sql, 3, parmas);  //失败重试3次
        }
    }

    public int update(String sql, int retry, Object... params) {
        int index = 0, affectRows = -1;
        try {
            if (!sql.equals(this.lastSQL)) {
                DbUtils.closeQuietly(this.ps);
                this.ps = this.conn.prepareStatement(sql);
                this.lastSQL = sql;
            }
            for (Object param : params) {
                ps.setObject(++index, param);
            }
            affectRows = ps.executeUpdate();
        } catch (Exception e) {
            if (retry > 0) {
                try {
                    this.conn = this.source.getConnection();  //重连数据库
                    DbUtils.closeQuietly(ps);
                    this.ps = this.conn.prepareStatement(sql);
                } catch (Exception re) {
                    System.out.println("重连接数据库报错：" + re.getMessage());
                }
                affectRows = this.update(sql, --retry, params);
            } else {
                System.out.println("执行语句报错：" + e.getMessage());
            }
        }/* finally {
            DbUtils.closeQuietly(ps);
        }*/
        return affectRows;
    }
}
