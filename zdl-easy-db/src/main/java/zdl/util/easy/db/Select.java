package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static zdl.util.easy.db.FilterConstant.*;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 16:11
 */
@Getter
@Setter
public class Select {

    /**
     * 筛选字段，WHERE后筛选语句
     */
    private Filters filters;

    /**
     * SELECT字段列表
     */
    private List<String> fields;

    public String sqlBuild(DatabaseSource source) {
        String tableName = source.getLongTableName();
        String columns = ASTERISK;
        if (!CollectionUtils.isEmpty(fields)) {
            columns = String.join(",", fields);
        }

        String where = IDENTITY_CONDITION;
        if (filters != null) {
            where = SqlBuild.sqlBuild(filters);
        }

        return String.format(SELECT_FORMAT, columns, tableName, where);
    }
}
