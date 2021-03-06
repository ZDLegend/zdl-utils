package zdl.springboot.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 * Created by ZDLegend on 2020/3/21 16:13
 */
@Component
public class ContextUtil implements ApplicationContextAware, EmbeddedValueResolverAware {

    private static ApplicationContext context;
    private static StringValueResolver resolver;
    private static final String KEY_FORMAT = "${%s}";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            context = applicationContext;
        }
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        if (stringValueResolver == null) {
            resolver = stringValueResolver;
        }
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(Class<T> tClass) {
        return context.getBean(tClass);
    }

    public static <T> T getBean(String name, Class<T> tClass) {
        return context.getBean(name, tClass);
    }

    public static String getProperty(String key) {
        return resolver.resolveStringValue(String.format(KEY_FORMAT, key));
    }

    /**
     * Spring 方式退出系统
     */
    public static void shutdown() {
        if (context instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) context).close();
        }
    }

    /**
     * Linux环境命令行方式强制关闭系统
     */
    public static void forceCloseSys() {
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        String exec = String.format("kill -9 %s", pid);
        try {
            Runtime.getRuntime().exec(exec);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
