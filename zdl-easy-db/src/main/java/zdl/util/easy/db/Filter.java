package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;

import static zdl.util.easy.db.FilterConstant.AND;

/**
 * 过滤条件数据结构
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/25/ 17:53
 */
@Getter
@Setter
public class Filter {

    /**
     * 字段名
     */
    private String field;

    /**
     * 数据库逻辑操作
     */
    private String operator = AND;

    /**
     * 值
     */
    private String value;
}
