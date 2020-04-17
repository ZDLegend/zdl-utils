package zdl.util.common;

import java.util.Arrays;

import static zdl.util.common.StringUtils.compileRegex;

/**
 * Created by ZDLegend on 2020/4/14 10:17
 */
public class ObjectUtil {

    /**
     * 对象是否为无效值
     *
     * @param obj 要判断的对象
     * @return 是否为有效值（不为null 和 "" 字符串）
     */
    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || "".equals(obj.toString());
    }

    /**
     * 参数是否是有效数字 （整数或者小数）
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是数字
     */
    public static boolean isNumber(Object obj) {
        if (obj instanceof Number) return true;
        return isInt(obj) || isDouble(obj);
    }

    /**
     * 参数是否是有效整数
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是整数
     */
    public static boolean isInt(Object obj) {
        if (isNullOrEmpty(obj))
            return false;
        if (obj instanceof Integer)
            return true;
        return obj.toString().matches("[-+]?\\d+");
    }

    /**
     * 字符串参数是否是double
     *
     * @param obj 参数（对象将被调用string()转为字符串类型）
     * @return 是否是double
     */
    public static boolean isDouble(Object obj) {
        if (isNullOrEmpty(obj))
            return false;
        if (obj instanceof Double || obj instanceof Float)
            return true;
        return compileRegex("[-+]?\\d+\\.\\d+").matcher(obj.toString()).matches();
    }

    /**
     * 判断一个对象是否为boolean类型,包括字符串中的true和false
     *
     * @param obj 要判断的对象
     * @return 是否是一个boolean类型
     */
    public static boolean isBoolean(Object obj) {
        if (obj instanceof Boolean) return true;
        String strVal = String.valueOf(obj);
        return "true".equalsIgnoreCase(strVal) || "false".equalsIgnoreCase(strVal);
    }

    /**
     * 对象是否为true
     *
     * @param obj
     * @return
     */
    public static boolean isTrue(Object obj) {
        return "true".equals(String.valueOf(obj));
    }

    /**
     * 判断一个数组里是否包含指定对象
     *
     * @param arr 对象数组
     * @param obj 要判断的对象
     * @return 是否包含
     */
    public static boolean contains(Object[] arr, Object... obj) {
        if (arr == null || obj == null || arr.length == 0) return false;
        return Arrays.asList(arr).containsAll(Arrays.asList(obj));
    }

    /**
     * 将对象转为int值,如果对象无法进行转换,则使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static int toInt(Object object, int defaultValue) {
        if (object instanceof Number)
            return ((Number) object).intValue();
        if (isInt(object)) {
            return Integer.parseInt(object.toString());
        }
        if (isDouble(object)) {
            return (int) Double.parseDouble(object.toString());
        }
        return defaultValue;
    }

    /**
     * 将对象转为int值,如果对象不能转为,将返回0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static int toInt(Object object) {
        return toInt(object, 0);
    }

    /**
     * 将对象转为long类型,如果对象无法转换,将返回默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static long toLong(Object object, long defaultValue) {
        if (object instanceof Number)
            return ((Number) object).longValue();
        if (isInt(object)) {
            return Long.parseLong(object.toString());
        }
        if (isDouble(object)) {
            return (long) Double.parseDouble(object.toString());
        }
        return defaultValue;
    }

    /**
     * 将对象转为 long值,如果无法转换,则转为0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static long toLong(Object object) {
        return toLong(object, 0);
    }

    /**
     * 将对象转为Double,如果对象无法转换,将使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    public static double toDouble(Object object, double defaultValue) {
        if (object instanceof Number)
            return ((Number) object).doubleValue();
        if (isNumber(object)) {
            return Double.parseDouble(object.toString());
        }
        if (null == object) return defaultValue;
        return 0;
    }

    /**
     * 将对象转为Double,如果对象无法转换,将使用默认值0
     *
     * @param object 要转换的对象
     * @return 转换后的值
     */
    public static double toDouble(Object object) {
        return toDouble(object, 0);
    }

    /**
     * 将对象转为String后进行分割，如果为对象为空或者空字符,则返回null
     *
     * @param object 要分隔的对象
     * @param regex  分隔规则
     * @return 分隔后的对象
     */
    public static final String[] toStringAndSplit(Object object, String regex) {
        if (isNullOrEmpty(object)) return null;
        return String.valueOf(object).split(regex);
    }

    /**
     * 将对象转为字符串,如果对象为null,则返回null,而不是"null"
     *
     * @param object 要转换的对象
     * @return 转换后的对象
     */
    public static String toString(Object object) {
        return toString(object, null);
    }

    /**
     * 将对象转为字符串,如果对象为null,则使用默认值
     *
     * @param object       要转换的对象
     * @param defaultValue 默认值
     * @return 转换后的字符串
     */
    public static String toString(Object object, String defaultValue) {
        if (object == null) return defaultValue;
        return String.valueOf(object);
    }
}
