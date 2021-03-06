package zdl.springboot.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by ZDLegend on 2020/1/13 9:49
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    public static ApplicationContext get() {
        if (null == context) {
            throw new UnsupportedOperationException("ApplicationContext not ready!");
        }
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        if (null == ApplicationContextHolder.context) {
            ApplicationContextHolder.context = applicationContext;
        }
    }
}
