package zdl.util.easy.db.select;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import zdl.util.easy.db.DatabaseSource;
import zdl.util.easy.db.Filters;
import zdl.util.easy.db.SqlBuild;

import java.util.List;

import static zdl.util.easy.db.FilterConstant.*;
import static zdl.util.easy.db.SqlBuild.addDoubleQuotes;
import static zdl.util.easy.db.SqlBuild.addSpace;

/**
 * SELECT数据库语句拼接类
 *
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

    /**
     * 排序条件
     */
    private Sort sort;

    /**
     * 分页条件
     */
    private Page page;

    public String sqlBuild(DatabaseSource source) {
        String tableName = source.getLongTableName();
        String columns = ASTERISK;
        if (!CollectionUtils.isEmpty(fields)) {
            columns = String.join(",", fields);
        }

        String where = TRUE_CONDITION;
        if (filters != null) {
            where = SqlBuild.sqlBuild(filters);
        }

        if (sort != null) {
            where = where + addSpace(ORDER_BY) + addDoubleQuotes(sort.getField()) + addSpace(sort.getDirection());
        }

        if (page != null) {
            where = where
                    + OFFSET + addSpace(String.valueOf(page.getOffset()))
                    + LIMIT + addSpace(String.valueOf(page.getLimit()));
        }

        return String.format(SELECT_FORMAT, columns, tableName, where);
    }
}
