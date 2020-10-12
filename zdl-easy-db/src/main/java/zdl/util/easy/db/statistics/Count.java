package zdl.util.easy.db.statistics;

import lombok.Getter;
import lombok.Setter;
import zdl.util.easy.db.FilterConstant;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 16:39
 */
@Getter
@Setter
public class Count {

    /**
     * 字段名
     */
    private String field;

    /**
     * 统计类型，枚举参考{@link FilterConstant#COUNT_OPE}
     */
    private String type;

    /**
     * 不填为默认字段：{@link Count#type} + '_' + {@link Count#field}
     */
    private String outPutField;
}
