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

    private static String sqlBuild(Filters filters) {
        String filterSql = null;
        String filtersSql = null;

        if (!CollectionUtils.isEmpty(filters.getFilter())) {
            filterSql = filters.getFilter().stream()
                    .map(SqlBuild::sqlBuild)
                    .filter(StringUtils::isNotBlank)
                    .map(SqlBuild::addBrackets)
                    .collect(Collectors.joining(filters.getOperator()));
        }

        if (!CollectionUtils.isEmpty(filters.getFilters())) {
            filtersSql = filters.getFilters().stream()
                    .map(SqlBuild::sqlBuild)
                    .filter(StringUtils::isNotBlank)
                    .map(SqlBuild::addBrackets)
                    .collect(Collectors.joining(filters.getOperator()));
        }

        if (StringUtils.isNotBlank(filterSql) && StringUtils.isNotBlank(filtersSql)) {
            return addBrackets(filterSql) + filters.getOperator() + addBrackets(filtersSql);
        } else if (StringUtils.isNotBlank(filterSql)) {
            return addBrackets(filterSql);
        } else if (StringUtils.isNotBlank(filtersSql)) {
            return addBrackets(filtersSql);
        } else {
            return IDENTITY_CONDITION;
        }
    }

    private static String sqlBuild(Filter filter) {
        StringBuilder sb = new StringBuilder();
        if (operatorTable.containsKey(filter.getOperator())) {
            operatorTable.get(filter.getOperator()).accept(sb, filter);
        }
        return sb.toString();
    }

    private static void common(StringBuilder sb, String sqlOperator, Filter filter) {
        sb.append(filter.getField())
                .append(sqlOperator)
                .append("'")
                .append(filter.getValue())
                .append("'");
    }

    private static void is(StringBuilder sb, Filter filter) {
        if (IS_NULL.equals(filter.getValue())) {
            sb.append(filter.getField()).append("IS NULL");
        } else {
            sb.append(filter.getField()).append("IS NOT NULL");
        }
    }

    private static void contains(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(" CAST(")
                .append(filter.getField())
                .append(" AS VARCHAR)")
                .append(" like'%")
                .append(value)
                .append("%'");
    }

    private static void endWith(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(" CAST(")
                .append(filter.getField())
                .append(" AS VARCHAR)")
                .append(" like'%")
                .append(value)
                .append("'");
    }

    private static void startWith(StringBuilder sb, Filter filter) {
        String value = likeReplace(filter);
        sb.append(" CAST(")
                .append(filter.getField())
                .append(" AS VARCHAR)")
                .append(" like'")
                .append(value)
                .append("%'");
    }

    private static void in(StringBuilder sb, Filter filter) {
        String[] strings = filter.getValue().split(",");
        String in = Stream.of(strings)
                .map(SqlBuild::addSingleQuotes)
                .collect(Collectors.joining(","));
        sb.append(filter.getField()).append(" in(").append(in).append(")");
    }

    private static void between(StringBuilder sb, Filter filter) {
        String[] strings = filter.getValue().split(",");
        sb.append(filter.getField())
                .append(" between ")
                .append(addSingleQuotes(strings[0]))
                .append(" and ")
                .append(addSingleQuotes(strings[1]));
    }

    private static String likeReplace(Filter filter) {
        return filter.getValue()
                .replace("?", "_")
                .replace("*", "%");
    }

    private static String addSingleQuotes(String field) {
        return "'" + field + "'";
    }

    private static String addBrackets(String field) {
        return "(" + field + ")";
    }
}
