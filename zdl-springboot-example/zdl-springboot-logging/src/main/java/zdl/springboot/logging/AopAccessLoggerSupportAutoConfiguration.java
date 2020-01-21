package zdl.springboot.logging;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zdl.springboot.logging.parser.DefaultAccessLoggerParser;

/**
 * AOP 访问日志记录自动配置
 *
 * @author Created by ZDLegend on 2020/1/13 13:56
 * @see AccessLogger
 * @see AopAccessLoggerSupport
 */
@ConditionalOnClass(AccessLoggerListener.class)
@Configuration
public class AopAccessLoggerSupportAutoConfiguration {

    @Bean
    public AopAccessLoggerSupport aopAccessLoggerSupport() {
        return new AopAccessLoggerSupport();
    }

    @Bean
    public DefaultAccessLoggerParser defaultAccessLoggerParser() {
        return new DefaultAccessLoggerParser();
    }
}
