package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 13:55
 */
@Getter
@Setter
public class DatabaseSource {
    private String ip;
    private String port;
    private String url;
    private String userName;
    private String password;
    private String dataBaseName;
    private String schemaName;
    private String tableName;
}
