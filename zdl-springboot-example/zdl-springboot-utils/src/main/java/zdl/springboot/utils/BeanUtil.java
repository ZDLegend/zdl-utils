package zdl.springboot.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

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

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl();
        PropertyDescriptor[] propertyDescriptors = src.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : propertyDescriptors) {
            Object p = src.getPropertyValue(pd.getName());
            if (p == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
