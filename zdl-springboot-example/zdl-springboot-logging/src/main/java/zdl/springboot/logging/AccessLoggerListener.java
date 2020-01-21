package zdl.springboot.logging;

/**
 * 访问日志监听器,实现此接口并注入到spring容器即可获取访问日志信息
 * <p>
 * Created by ZDLegend on 2020/1/13 10:05
 */
public interface AccessLoggerListener {

    /**
     * 当产生访问日志时,将调用此方法.注意,此方法内的操作应尽量设置为异步操作,否则可能影响请求性能
     * 被{@link AccessLogger}注解方法使用后调用
     *
     * @param loggerInfo 产生的日志信息
     */
    void onLogger(AccessLoggerInfo loggerInfo);

    /**
     * 被{@link AccessLogger}注解方法使用前调用
     *
     * @param loggerInfo 产生的日志信息
     */
    default void onLogBefore(AccessLoggerInfo loggerInfo) {
    }
}
