package zdl.util;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author ZDLegend
 * @create 2017/12/12
 */

public class AsyncUtil {

    private final static ExecutorService cachedThreadPool = Executors.newFixedThreadPool(8);
    private final static Timer timer = new Timer();

    /**
     * 将函数handle进行异步操作, 获取到的结果在resultHandle中执行
     */
    public static <V> void handle(Supplier<V> handle, Consumer<V> resultHandle){
        cachedThreadPool.submit(() -> {
            V result = handle.get();
            resultHandle.accept(result);
        });
    }

    public static <T, V> void handle(T obj, Function<T, V> handle, Consumer<V> resultHandle){
        cachedThreadPool.submit(() -> {
            V result = handle.apply(obj);
            resultHandle.accept(result);
        });
    }

    public static <V> void handle(V obj, Consumer<V> handle){
        cachedThreadPool.submit(() -> handle.accept(obj));
    }

    public static void handle(Runnable runnable){
        cachedThreadPool.submit(runnable);
    }

    /**
     * 添加一个定时器
     * @param runnable 定时任务
     * @param delay 延迟
     * @param period 周期
     */
    public static void addTimerTask(Runnable runnable, Date delay, long period){
        TimerTask task = new TimerTask(){
            public void run() {
                runnable.run();
            }
        };

        timer.schedule(task, delay, period);
    }

    public static void addTimerTask(TimerTask task, Date delay, long period){
        timer.schedule(task, delay, period);
    }

    /**
     * 添加一个即刻定时器
     * @param runnable 定时任务
     * @param period 周期
     */
    public static void addTimerTask(Runnable runnable, long period){
        addTimerTask(runnable, new Date(), period);
    }

    private static void stop(){
        cachedThreadPool.shutdown();
    }
}
