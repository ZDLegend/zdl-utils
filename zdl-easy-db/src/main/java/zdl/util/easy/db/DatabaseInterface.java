package zdl.util.easy.db;


import javax.sql.DataSource;
import java.io.Closeable;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/25/ 16:47
 */
public interface DatabaseInterface extends DataSource, Closeable {

}
