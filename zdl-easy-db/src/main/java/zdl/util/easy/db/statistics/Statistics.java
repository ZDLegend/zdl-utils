package zdl.util.easy.db.statistics;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import zdl.util.easy.db.DatabaseSource;
import zdl.util.easy.db.Filters;
import zdl.util.easy.db.SqlBuild;

import java.util.List;
import java.util.stream.Collectors;

import static zdl.util.easy.db.FilterConstant.*;
import static zdl.util.easy.db.SqlBuild.*;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/28/ 16:38
 */
@Getter
@Setter
public class Statistics {

    private Filters filters;
    private List<Count> counts;
    private List<String> groupBy;

    public String sqlBuild(DatabaseSource source) {

        String head = "";

        String columns = String.join(",", groupBy);

        if (StringUtils.isNotBlank(columns)) {
            head = columns;
        }

        if (!CollectionUtils.isEmpty(counts)) {
            head = head + "," + counts.stream()
                    .map(count -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(count.getType())
                                .append(addBrackets(addSingleQuotes(count.getField())))
                                .append(addSpace(AS));
                        if (StringUtils.isNotBlank(count.getOutPutField())) {
                            sb.append(count.getOutPutField());
                        } else {
                            sb.append(count.getType()).append("_").append(count.getField());
                        }
                        return sb.toString();
                    })
                    .collect(Collectors.joining(","));

        }

        String where = SqlBuild.sqlBuild(filters);
        where = StringUtils.isNotBlank(where) ? where : IDENTITY_CONDITION;

        if (StringUtils.isNotBlank(columns)) {
            return String.format(COUNT_FORMAT, head, source.getLongTableName(), where, columns);
        } else {
            return String.format(SELECT_FORMAT, head, source.getLongTableName(), where);
        }
    }
}
