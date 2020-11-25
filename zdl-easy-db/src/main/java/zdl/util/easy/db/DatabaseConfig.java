package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * 数据源类
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 13:55
 */
@Getter
@Setter
public class DatabaseConfig {
    private String host;
    private String port;
    private String url;
    private String userName;
    private String password;
    private String dataBaseName;
    private String schemaName;
    private String tableName;
    private String dbType;  //数据库类型
    private int maxConnection = 10;
    private long timeOutMs = 60000;

    public String getLongTableName() {
        if (StringUtils.isNotBlank(schemaName)) {
            return schemaName + "." + tableName;
        } else {
            return tableName;
        }
    }
}
