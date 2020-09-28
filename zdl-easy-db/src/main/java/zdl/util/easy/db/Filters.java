package zdl.util.easy.db;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 过滤条件组数据结构
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 09:29
 */
@Getter
@Setter
public class Filters {

    /**
     * 逻辑关系
     */
    private String operator;

    /**
     * 筛选条件列表
     */
    List<Filter> filter;

    /**
     * 筛选条件组列表
     */
    List<Filters> filters;

}
