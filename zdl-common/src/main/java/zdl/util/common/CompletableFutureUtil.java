package zdl.util.common;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * CompletableFuture辅助类
 *
 * Created by ZDLegend on 2019/12/26 13:52
 */
public class CompletableFutureUtil {

    /**
     * 用于统一处理whenComplete()方法
     */
    public static BiConsumer<Object, ? super Throwable> whenCompleteHandle = (s, t) -> {
        if (null != s) {
            //todo: 返回结果在这里处理
            System.out.println(s);
        }

        if (null != t) {
            //todo: 异常结果在这里处理
            System.out.println(t);
        }
    };

    /**
     * 异步中的串行等待
     *
     * @param timeMillis 时间
     * @param t 上一个future的返回参数
     * @param <T> 上一个future的返回参数类型
     * @return 返回上一个future的返回值
     */
    public static <T> CompletableFuture<T> waite(long timeMillis, T t) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("waite " + timeMillis + " ms");
            try {
                Thread.sleep(timeMillis);
            } catch (InterruptedException e) {
                //todo: 中断异常处理
                e.printStackTrace();
            }
            System.out.println("waite end " + timeMillis + " ms");
            return t;
        });
    }
}
