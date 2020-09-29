package zdl.util.easy.db.select;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/09/29/ 10:09
 */
@Getter
@Setter
public class Page {
    private int offset;
    private int limit;
}
