package zdl.util.common;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 异步封装基于CompletableFuture
 *
 * @author ZDLegend
 * @create 2017/12/12
 */

public class AsyncUtil {

    private final static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(8);
    private final static Timer timer = new Timer();

    /**
     * 将函数handle进行异步操作, 获取到的结果在resultHandle中执行
     */
    public static <V> CompletableFuture<V> handle(Supplier<V> handle, Consumer<V> success, Consumer<Throwable> exception) {
        return CompletableFuture.supplyAsync(handle, cachedThreadPool)
                .whenComplete((v, throwable) -> {
                    if (throwable == null) {
                        success.accept(v);
                    } else {
                        exception.accept(throwable);
                    }
                });
    }

    public static <T, V> CompletableFuture<V> handle(T obj, Function<T, V> handle, Consumer<V> resultHandle,
                                                     Consumer<Throwable> exception) {
        return CompletableFuture.supplyAsync(() -> handle.apply(obj), cachedThreadPool)
                .whenComplete((v, throwable) -> {
                    if (throwable == null) {
                        resultHandle.accept(v);
                    } else {
                        exception.accept(throwable);
                    }
                });
    }

    public static <V> CompletableFuture handle(V obj, Consumer<V> handle, Consumer<Throwable> exception) {
        return CompletableFuture.runAsync(() -> handle.accept(obj), cachedThreadPool)
                .whenComplete((v, throwable) -> {
                    if (throwable != null) {
                        exception.accept(throwable);
                    }
                });
    }

    public static CompletableFuture handle(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, cachedThreadPool);
    }

    /**
     * 添加一个定时器
     *
     * @param runnable 定时任务
     * @param delay    延迟
     * @param period   周期
     */
    public static void addTimerTask(Runnable runnable, Date delay, long period) {
        TimerTask task = new TimerTask() {
            public void run() {
                runnable.run();
            }
        };
        timer.schedule(task, delay, period);
    }

    public static void addTimerTask(TimerTask task, Date delay, long period) {
        timer.schedule(task, delay, period);
    }

    /**
     * 添加一个即刻定时器
     *
     * @param runnable 定时任务
     * @param period   周期
     */
    public static void addTimerTask(Runnable runnable, long period) {
        addTimerTask(runnable, new Date(), period);
    }

    private static void stop() {
        cachedThreadPool.shutdown();
    }
}
