package zdl.util.easy.db;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static zdl.util.easy.db.FilterConstant.*;

/**
 * 数据库语句拼接
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 10:25
 */
public class SqlBuild {

    private static final Map<String, BiConsumer<StringBuilder, Filter>> operatorTable = new HashMap<>();

    static {
        operatorTable.put(EQ, (sb, filter) -> common(sb, "=", filter));
        operatorTable.put(NE, (sb, filter) -> common(sb, "!=", filter));
        operatorTable.put(LT, (sb, filter) -> common(sb, "<", filter));
        operatorTable.put(LTE, (sb, filter) -> common(sb, "<=", filter));
        operatorTable.put(GT, (sb, filter) -> common(sb, ">", filter));
        operatorTable.put(GTE, (sb, filter) -> common(sb, ">=", filter));
        operatorTable.put(IS, SqlBuild::is);
        operatorTable.put(CONTAINS, SqlBuild::contains);
        operatorTable.put(START_WITH, SqlBuild::startWith);
        operatorTable.put(END_WITH, SqlBuild::endWith);
        operatorTable.put(IN, SqlBuild::in);
        operatorTable.put(BETWEEN, SqlBuild::between);
    }

    public static String sqlBuild(Filters filters) {
        String filterSql = null;
        String filtersSql = null;

        if (!CollectionUtils.isEmpty(filters.getFilter())) {
            filterSql = filters.getFilter().stream()
                    .map(SqlBuild::sqlBuild)
                    .filter(StringUtils::isNotBlank)
                    .map(SqlBuild::addBrackets)
                    .collect(Collectors.joining(filters.getOperator()));
        } else {
            filterSql = CON_MAP.get(filters.getOperator());
        }

        if (!CollectionUtils.isEmpty(filters.getFilters())) {
            filtersSql = filters.getFilters().stream()
                    .map(SqlBuild::sqlBuild)
                    .filter(StringUtils::isNotBlank)
                    .map(SqlBuild::addBrackets)
                    .collect(Collectors.joining(filters.getOperator()));
        } else {
            filterSql = CON_MAP.get(filters.getOperator());
        }

        return filterSql + addSpace(filters.getOperator()) + filtersSql;
    }

    private static String sqlBuild(Filter filter) {
        StringBuilder sb = new StringBuilder();
        if (operatorTable.containsKey(filter.getOperator())) {
            operatorTable.get(filter.getOperator()).accept(sb, filter);
        }
        return sb.toString();
    }

    private static void common(StringBuilder sb, String sqlOperator, Filter filter) {
        sb.append(addDoubleQuotes(filter.getField()))
                .append(sqlOperator)
                .append(addSingleQuotes(filter.getValue()));
    }

    private static void is(StringBuilder sb, Filter filter) {
        if (IS_NULL.equals(filter.getValue())) {
            sb.append(String.format(IS_NULL_FORMAT, filter.getField()));
        } else {
            sb.append(String.format(IS_NOT_NULL_FORMAT, filter.getField()));
        }
    }

    private static void contains(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(String.format(LIKE_FORMAT, filter.getField(), "%", value, "%"));
    }

    private static void endWith(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(String.format(LIKE_FORMAT, filter.getField(), "%", value, ""));
    }

    private static void startWith(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(String.format(LIKE_FORMAT, filter.getField(), "", value, "%"));
    }

    private static void in(StringBuilder sb, Filter filter) {
        String[] strings = filter.getValue().split(",");
        String in = Stream.of(strings)
                .map(SqlBuild::addSingleQuotes)
                .collect(Collectors.joining(","));
        sb.append(String.format(IN_FORMAT, filter.getField(), in));
    }

    private static void between(StringBuilder sb, Filter filter) {
        String[] strings = filter.getValue().split(",");
        sb.append(String.format(BETWEEN_FORMAT, filter.getField(), strings[0], strings[1]));
    }

    private static String likeReplace(Filter filter) {
        return filter.getValue()
                .replace("?", "_")
                .replace("*", "%");
    }

    public static String addSingleQuotes(String field) {
        return "'" + field + "'";
    }

    public static String addDoubleQuotes(String field) {
        return "\"" + field + "\"";
    }

    public static String addBrackets(String field) {
        return "(" + field + ")";
    }

    public static String addSpace(String field) {
        return " " + field + " ";
    }
}
