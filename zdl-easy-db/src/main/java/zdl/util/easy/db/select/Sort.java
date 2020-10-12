package zdl.util.easy.db.select;

import lombok.Getter;
import lombok.Setter;
import zdl.util.easy.db.FilterConstant;

/**
 * 排序类
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/29/ 10:09
 */
@Getter
@Setter
public class Sort {

    /**
     * 字段名
     */
    private String field;

    /**
     * 排序字段，枚举：{@link FilterConstant#SORT_OPE}
     */
    private String direction;
}
