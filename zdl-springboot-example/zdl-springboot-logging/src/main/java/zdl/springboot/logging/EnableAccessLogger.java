package zdl.springboot.logging;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;

import java.lang.annotation.*;

/**
 * 启用访问日志
 *
 * @see AopAccessLoggerSupportAutoConfiguration
 * @see AccessLoggerListener
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ImportAutoConfiguration(AopAccessLoggerSupportAutoConfiguration.class)
public @interface EnableAccessLogger {
}
