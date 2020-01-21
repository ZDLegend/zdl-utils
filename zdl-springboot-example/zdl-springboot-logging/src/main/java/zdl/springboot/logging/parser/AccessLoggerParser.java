package zdl.springboot.logging.parser;

import zdl.springboot.logging.AopAccessLoggerSupport;
import zdl.springboot.logging.aop.MethodInterceptorHolder;

import java.lang.reflect.Method;

/**
 * AOP日志切面分析器
 * 继承此接口来决定哪些Spring bean的方法和类需要注入
 *
 * @see DefaultAccessLoggerParser
 */
public interface AccessLoggerParser {

    /**
     * 实现此方法来决定哪些Spring Bean中方法和类需要进行AOP注入
     *
     * @param method 方法对象
     * @param clazz  类对象
     * @return 是否需要注入
     * @see AopAccessLoggerSupport#matches(Method, Class)
     */
    boolean support(Class clazz, Method method);

    LoggerDefine parse(MethodInterceptorHolder holder);
}
