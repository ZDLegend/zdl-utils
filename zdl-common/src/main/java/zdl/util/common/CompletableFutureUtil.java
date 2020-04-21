package zdl.util.common;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Created by ZDLegend on 2019/12/26 13:52
 */
public class CompletableFutureUtil {

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
