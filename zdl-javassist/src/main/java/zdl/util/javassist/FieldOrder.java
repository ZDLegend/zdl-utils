package zdl.util.javassist;

import java.lang.annotation.*;

/**
 * 声明入库字段在数据表中的顺序
 *
 * Created by ZDLegend on 2020/06/17 10:36
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldOrder {
    int order();
}
