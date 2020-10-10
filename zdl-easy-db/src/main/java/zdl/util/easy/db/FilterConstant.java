package zdl.util.easy.db;

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
    public static final String IDENTITY_CONDITION = "1=1";
    public static final String SPACE = " ";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String ORDER_BY = "ORDER BY";
    public static final String OFFSET = "OFFSET";
    public static final String LIMIT = "LIMIT";

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

    //数据库统计类型
    public static final String COUNT = "count";
    public static final String SUM = "sum";
    public static final String AVG = "avg";
    public static final String MAX = "max";
    public static final String MIN = "min";
}
