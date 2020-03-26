package zdl.springboot.utils;

import java.util.ResourceBundle;

/**
 * Created by ZDLegend on 2020/3/26 16:55
 */
public class PropertiesUtil {

    public static final ResourceBundle RESOURCE = ResourceBundle.getBundle("application");

    public static String getStringByKey(String key) {
        return RESOURCE.getString(key);
    }
}
