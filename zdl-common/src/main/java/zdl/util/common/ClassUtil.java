package zdl.util.common;

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
        return null;
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
        return null;
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
        List<Type> arrays = new ArrayList<>();
        arrays.add(clazz.getGenericSuperclass());
        arrays.addAll(Arrays.asList(clazz.getGenericInterfaces()));
        return arrays.stream()
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

    public static boolean instanceOf(Class clazz, Class target) {
        if (clazz == null) return false;
        if (clazz == target) return true;
        if (target.isInterface()) {
            for (Class aClass : clazz.getInterfaces()) {
                if (aClass == target) return true;
            }
        }
        if (clazz.getSuperclass() == target) return true;
        else {
            if (clazz.isInterface()) {
                for (Class aClass : clazz.getInterfaces()) {
                    if (instanceOf(aClass, target)) return true;
                }
            }
            return instanceOf(clazz.getSuperclass(), target);
        }
    }

    /**
     * 将对象转为指定的类型
     * <br/>
     * 支持日期，数字，boolean类型转换
     *
     * @param value 需要转换的值
     * @param type  目标类型
     * @return 转换后的值
     */
    public static <T> T cast(Object value, Class<T> type) {
        if (value == null) return null;
        Object newVal = null;
        if (instanceOf(value.getClass(), type)) {
            newVal = value;
        } else if (type == Integer.class || type == int.class) {
            newVal = ObjectUtil.toInt(value);
        } else if (type == Double.class || type == double.class || type == Float.class || type == float.class) {
            newVal = ObjectUtil.toDouble(value);
        } else if (type == Long.class || type == long.class) {
            newVal = ObjectUtil.toLong(value);
        } else if (type == Boolean.class || type == boolean.class) {
            newVal = ObjectUtil.isTrue(value);
        } else if (type == String.class) {
            newVal = String.valueOf(value);
        }

        return (T) newVal;
    }
}
