package zdl.springboot.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

/**
 * Created by ZDLegend on 2020/3/24 14:41
 */
public class BeanUtil extends org.springframework.beans.BeanUtils {

    /**
     * copy scr中非null的属性至target中
     */
    public static void copyPropertiesIgnoreNull (Object src, Object target) {
        copyProperties(src, target, getNullPropertyNames(src));
    }

    /**
     * 获取类中的空指针属性
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Stream.of(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .distinct()
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }
}
