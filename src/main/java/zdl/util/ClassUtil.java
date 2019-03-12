package zdl.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author ZDLegend
 * @create 2019/3/12
 */
public class ClassUtil {

    /**
     * 获取一个类的注解,如果未获取到则获取父类
     *
     * @param clazz      要获取的类
     * @param annotation 注解类型
     * @param <T>        注解类型泛型
     * @return 注解
     */
    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotation) {
        T ann = clazz.getAnnotation(annotation);
        if (ann != null) {
            return ann;
        } else {
            if (clazz.getSuperclass() != Object.class) {
                //尝试获取父类
                return getAnnotation(clazz.getSuperclass(), annotation);
            }
        }
        return ann;
    }

    /**
     * 获取一个方法的注解,如果未获取则获取父类方法
     *
     * @param method     要获取的方法
     * @param annotation 注解类型
     * @param <T>        注解类型泛型
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotation) {
        T ann = method.getAnnotation(annotation);
        if (ann != null) {
            return ann;
        } else {
            Class clazz = method.getDeclaringClass();
            Class superClass = clazz.getSuperclass();
            if (superClass != Object.class) {
                try {
                    //父类方法
                    Method suMethod = superClass.getMethod(method.getName(), method.getParameterTypes());
                    return getAnnotation(suMethod, annotation);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        }
        return ann;
    }

    public static Class<?> getGenericTypeByType(ParameterizedType genType, int index) {
        Type[] params = genType.getActualTypeArguments();
        if (index >= params.length || index < 0) {
            return null;
        }
        Object res = params[index];
        if (res instanceof Class) {
            return ((Class) res);
        }
        if (res instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) res).getRawType();
        }
        return null;
    }

    /**
     * 获取一个类的泛型类型,如果未获取到返回Object.class
     *
     * @param clazz 要获取的类
     * @param index 泛型索引
     * @return 泛型
     */
    public static Class<?> getGenericType(Class clazz, int index) {
        List<Type> arrys = new ArrayList<>();
        arrys.add(clazz.getGenericSuperclass());
        arrys.addAll(Arrays.asList(clazz.getGenericInterfaces()));
        return arrys.stream()
                .filter(Objects::nonNull)
                .map(type -> {
                    if (clazz != Object.class && !(type instanceof ParameterizedType)) {
                        return getGenericType(clazz.getSuperclass(), index);
                    }
                    return getGenericTypeByType(((ParameterizedType) type), index);
                })
                .filter(Objects::nonNull)
                .filter(res -> res != Object.class)
                .findFirst()
                .orElse((Class) Object.class);
    }

    /**
     * 获取一个类的第一个泛型的类型
     *
     * @param clazz 要获取的类
     * @return 泛型
     */
    public static Class<?> getGenericType(Class clazz) {
        return getGenericType(clazz, 0);
    }
}
