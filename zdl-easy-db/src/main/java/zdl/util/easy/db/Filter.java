package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;

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
     * 数据库逻辑操作，枚举参考{@link FilterConstant#FILTER_OPE}
     */
    private String operator;

    /**
     * 值
     */
    private String value;
}
