package zdl.springboot.utils;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ZDLegend on 2020/3/24 14:41
 */
public class BeanUtil extends org.springframework.beans.BeanUtils {

    private BeanUtil() {
    }

    public static <T> List<T> convertList(List<?> source, Class<T> clazz) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }
        return source.stream().map(model -> convert(model, clazz)).collect(Collectors.toList());
    }

    public static <T> T convert(Object src, Class<T> clazz) {
        T target = null;
        try {
            target = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert target != null;
        copyProperties(src, target);
        return target;
    }

    /**
     * copy scr中非null的属性至target中
     */
    public static void copyPropertiesIgnoreNull(Object src, Object target) {
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
