package zdl.util.easy.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 常用字符串
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 09:47
 */
public class FilterConstant {

    //基础sql拼接
    public static final String SELECT_FORMAT = "SELECT %s FROM %s WHERE %s";
    public static final String COUNT_FORMAT = "SELECT %s FROM %s WHERE %s GROUP BY %s";
    public static final String LIKE_FORMAT = " CAST(%s AS VARCHAR) LIKE '%s%s%s'";
    public static final String IN_FORMAT = "\"%s\" IN(%s)'";
    public static final String BETWEEN_FORMAT = "\"%s\" BETWEEN '%s' AND '%s'";
    public static final String IS_NULL_FORMAT = "\"%s\" IS NULL";
    public static final String IS_NOT_NULL_FORMAT = "\"%s\" IS NOT NULL";

    //sql关键字
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String SELECT = "SELECT";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String ASTERISK = "*";
    public static final String AS = "AS";
    public static final String TRUE_CONDITION = "1=1";
    public static final String FALSE_CONDITION = "1=0";
    public static final String SPACE = " ";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String ORDER_BY = "ORDER BY";
    public static final String OFFSET = "OFFSET";
    public static final String LIMIT = "LIMIT";
    public static final Set<String> LOGIC_OPE = Stream.of(AND, OR).collect(Collectors.toSet());
    public static final Set<String> SORT_OPE = Stream.of(ASC, DESC).collect(Collectors.toSet());
    public static final Map<String, String> CON_MAP = new HashMap<>() {{
        put(AND, TRUE_CONDITION);
        put(OR, FALSE_CONDITION);
    }};

    //数据库逻辑操作
    public static final String EQ = "eq";
    public static final String NE = "ne";
    public static final String LT = "lt";
    public static final String LTE = "lte";
    public static final String GT = "gt";
    public static final String GTE = "gte";
    public static final String CONTAINS = "contains";
    public static final String START_WITH = "startWith";
    public static final String END_WITH = "endWith";
    public static final String IN = "in";
    public static final String IS = "is";
    public static final String BETWEEN = "between";
    public static final String IS_NULL = "0";
    public static final Set<String> FILTER_OPE = Stream.of(EQ, NE, LT, LTE, GT, GTE, CONTAINS, START_WITH,
            END_WITH, IN, IS, BETWEEN, IS_NULL).collect(Collectors.toSet());

    //数据库统计类型
    public static final String COUNT = "count";
    public static final String SUM = "sum";
    public static final String AVG = "avg";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final Set<String> COUNT_OPE = Stream.of(COUNT, SUM, AVG, MAX, MIN).collect(Collectors.toSet());
}
