package zdl.util.doc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * java 1.8 特性示例
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/17/ 09:23
 */
public class Java8Character {

    /**
     * 1.Lambda表达式
     */
    public void testLambda() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        list.forEach(System.out::println);
        list.forEach(e -> System.out.println("方式二：" + e));
    }

    /**
     * Stream函数式操作流元素集合
     */
    public void testStream() {
        List<Integer> nums = Arrays.asList(1, 1, null, 2, 3, 4, null, 5, 6, 7, 8, 9, 10);
        System.out.println(
                nums.stream()//转成Stream
                        .filter(Objects::nonNull)//过滤
                        .distinct()//去重
                        .mapToInt(num -> num * 2)//map操作
                        .skip(2)//跳过前2个元素
                        .limit(4)//限制取前4个元素
                        .peek(System.out::println)//流式处理对象函数
                        .sum()//求和
        );
    }

    /**
     * 3.接口新增：默认方法与静态方法
     */
    public interface JDK8Interface1 {

        //1.接口中可以定义静态方法了
        public static void staticMethod() {
            System.out.println("接口中的静态方法");
        }

        //2.使用default之后就可以定义普通方法的方法体了
        public default void defaultMethod() {
            System.out.println("接口中的默认方法");
        }
    }

    public void testMethodReference() {
        //构造器引用。语法是Class::new，或者更一般的Class< T >::new，要求构造器方法是没有参数；
        final Car car = Car.create(Car::new);
        final List<Car> cars = Collections.singletonList(car);
        //静态方法引用。语法是Class::static_method，要求接受一个Class类型的参数；
        cars.forEach(Car::collide);
        //任意对象的方法引用。它的语法是Class::method。无参，所有元素调用；
        cars.forEach(Car::repair);
        //特定对象的方法引用，它的语法是instance::method。有参，在某个对象上调用方法，将列表元素作为参数传入；
        final Car police = Car.create(Car::new);
        cars.forEach(police::follow);
    }

    /**
     * 4.方法引用,与Lambda表达式联合使用
     */
    public static class Car {
        public static Car create(final Supplier<Car> supplier) {
            return supplier.get();
        }

        public static void collide(final Car car) {
            System.out.println("静态方法引用 " + car.toString());
        }

        public void repair() {
            System.out.println("任意对象的方法引用 " + this.toString());
        }

        public void follow(final Car car) {
            System.out.println("特定对象的方法引用 " + car.toString());
        }
    }

    //5.引入重复注解
    //6.类型注解

    /**
     * 7.最新的Date/Time API (JSR 310)
     * <li>注：TemporalAdjusters类中有许多常用的特殊的日期的方法（类方法），使用时可以仔细查看，可以很大程度减少日期判断的代码量！
     */
    public void time() {
        //获取当前日期
        LocalDate nowDate = LocalDate.now();
        System.out.println(nowDate);
        //自定义时间：2018-08-08
        LocalDate formatDate = LocalDate.parse("2018-08-08");
        System.out.println(formatDate);
        //获取当天所属本月中的第几天
        System.out.println(nowDate.getDayOfMonth());//21
        //获取当天所属本年中的第几天
        System.out.println(nowDate.getDayOfYear());//81
        //获取当天所属本周的周几
        System.out.println(nowDate.getDayOfWeek());//THURSDAY
        System.out.println(nowDate.getDayOfWeek().getValue());//4
        //获取当月所属本年的第几月，与new Date() 相比它是从1开始，抛弃了之前的从0开始
        System.out.println(nowDate.getMonth());//MARCH
        System.out.println(nowDate.getMonth().getValue());//3
        System.out.println(nowDate.getMonthValue());//3
        //获取年
        System.out.println(nowDate.getYear());//2021
        //获取次日期适用的时段
        System.out.println(nowDate.getEra());//CE
        //获取本月第一天
        System.out.println(nowDate.with(TemporalAdjusters.firstDayOfMonth()));//2021-03-01
        //获取本月第二天
        System.out.println(nowDate.withDayOfMonth(2));
        //获取本月最后一天，无须再判断28、29、30、31
        System.out.println(nowDate.with(TemporalAdjusters.lastDayOfMonth()));
        //获取下一天日期
        System.out.println(nowDate.plusDays(1));

        /*
         * 获取当前时间 format: HH:mm:ss
         * 1、nowTime.getHour() ：16（获取小时数）
         * 2、nowTime.getMinute() ：45（获取分钟数）
         * 3、nowTime.getSecond() ：51（获取秒数）
         * 4、nowTime.getNano() ：805000000（获取纳秒数）
         * 5、LocalTime zero = LocalTime.of(0, 0, 0) ：00:00:00（构建自定义时间）
         * LocalTime mid = LocalTime.parse("12:00:00") ：12:00:00
         */
        System.out.println(LocalTime.now());

        /*
         * 获取当前日期时间 format: yyyy-MM-dd HH:mm:ss
         * 1、LocalDateTime nowDateTime = LocalDateTime.now(); 获取当前日期时间：2019-03-21T16:45:51.591
         * 2、DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
         * String nowDateTime = LocalDateTime.now().format(formatter);
         * 使用自定义格式器DateTimeFormatter替换了Java8之前的SimpleDateFormat
         */
        System.out.println(LocalDateTime.now());
        System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));

        /*
         * 最新的JDBC整合了Java8中的Date/Time API，将Java8中的Date/Time类型与数据库类型进行了映射
         * 数据库类型 -> Java类型
         * date -> LocalDate
         * time -> LocalTime
         * timestamp -> LocalDateTime
         */
    }

    /**
     * 8.新增base64加解密API
     */
    public void testBase64() {
        final String text = "就是要测试加解密！！abjdkhdkuasu!!@@@@";
        String encoded = Base64.getEncoder()
                .encodeToString(text.getBytes(StandardCharsets.UTF_8));
        System.out.println("加密后=" + encoded);

        final String decoded = new String(
                Base64.getDecoder().decode(encoded),
                StandardCharsets.UTF_8);
        System.out.println("解密后=" + decoded);
    }

    /**
     * 9.数组并行（parallel）操作
     */
    public void testParallel() {
        long[] arrayOfLong = new long[20000];
        //1.给数组随机赋值
        Arrays.parallelSetAll(arrayOfLong,
                index -> ThreadLocalRandom.current().nextInt(1000000));
        //2.打印出前10个元素
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();
        //3.数组排序
        Arrays.parallelSort(arrayOfLong);
        //4.打印排序后的前10个元素
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();
    }

    //10.JVM的PermGen空间被移除：取代它的是Metaspace（JEP 122）元空间
    //-XX:MetaspaceSize初始空间大小，达到该值就会触发垃圾收集进行类型卸载，同时GC会对该值进行调整
    //-XX:MaxMetaspaceSize最大空间，默认是没有限制
    //-XX:MinMetaspaceFreeRatio在GC之后，最小的Metaspace剩余空间容量的百分比，减少为分配空间所导致的垃圾收集
    //-XX:MaxMetaspaceFreeRatio在GC之后，最大的Metaspace剩余空间容量的百分比，减少为释放空间所导致的垃圾收集
}
