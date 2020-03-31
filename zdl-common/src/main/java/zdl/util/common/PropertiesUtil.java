package zdl.util.common;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * Created by ZDLegend on 2020/3/26 16:55
 */
public class PropertiesUtil {

    public static final ResourceBundle RESOURCE = ResourceBundle.getBundle("application");

    public static final Properties PROP = new Properties();

    static {
        String files = getStringByKey("files");
        if(StringUtils.isNoneBlank()) {

            Stream.of(files.trim().split(","))
                    .map(name -> PropertiesUtil.class.getClassLoader().getResourceAsStream(name))
                    .filter(Objects::nonNull)
                    .map(InputStreamReader::new)
                    .forEach(reader -> {
                        try {
                            PROP.load(reader);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public static String getStringByKey(String key) {
        return RESOURCE.getString(key);
    }

    public static String getPropKey(String key) {
        return PROP.getProperty(key);
    }
}
