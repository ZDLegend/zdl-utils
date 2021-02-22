package zdl.util.flink.demo;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.CoGroupFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/12/ 17:21
 */
public class FlinkDemo {

    private static int index = 1;

    private static final String LOCALHOST = "localhost";

    /**
     * map可以理解为映射，对每个元素进行一定的变换后，映射为另一个元素。
     */
    public static void mapDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.map操作。
        DataStream<String> result = textStream.map(s -> (index++) + ".您输入的是：" + s);
        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * flatmap可以理解为将元素摊平，每个元素可以变为0个、1个、或者多个元素。
     */
    public static void flatMapDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.flatMap操作，对每一行字符串进行分割
        DataStream<String> result = textStream.flatMap((String s, Collector<String> collector) -> {
            for (String str : s.split("")) {
                collector.collect(str);
            }
        })
                //这个地方要注意，在flatMap这种参数里有泛型算子中。
                //如果用lambda表达式，必须将参数的类型显式地定义出来。
                //并且要有returns，指定返回的类型
                //详情可以参考Flink官方文档：https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/java_lambdas.html
                .returns(Types.STRING);

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * 逻筛选
     */
    public static void filterDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.filter操作，筛选非空行。
        DataStream<String> result = textStream.filter(line -> !line.trim().equals(""));
        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * 逻辑上将Stream根据指定的Key进行分区，是根据key的散列值进行分区的。
     */
    public static void keyByDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.
        DataStream<Tuple2<String, Integer>> result = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple2.of(line.trim(), 1))
                //如果要用Lambda表示是，Tuple2是泛型，那就得用returns指定类型。
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                //keyBy进行分区，按照第一列，也就是按照单词进行分区
                .keyBy(value -> value.f0)
                //指定窗口，每10秒个计算一次
                .timeWindow(Time.of(10, TimeUnit.SECONDS))
                //计算个数，计算第1列
                .sum(1);
        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * reduce是归并操作，它可以将KeyedStream 转变为 DataStream。
     */
    public static void reduceDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.
        DataStream<Tuple2<String, Integer>> result = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple2.of(line.trim(), 1))
                //如果要用Lambda表示是，Tuple2是泛型，那就得用returns指定类型。
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                //keyBy进行分区，按照第一列，也就是按照单词进行分区
                .keyBy(value -> value.f0)
                //指定窗口，每10秒个计算一次
                .timeWindow(Time.of(10, TimeUnit.SECONDS))
                //对每一组内的元素进行归并操作，即第一个和第二个归并，结果再与第三个归并...
                .reduce((Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) -> new Tuple2<>(t1.f0, t1.f1 + t2.f1));

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * 给定一个初始值，将各个元素逐个归并计算。它将KeyedStream转变为DataStream。
     */
    public static void foldDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //3.
        DataStream<String> result = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple2.of(line.trim(), 1))
                //如果要用Lambda表示是，Tuple2是泛型，那就得用returns指定类型。
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                //keyBy进行分区，按照第一列，也就是按照单词进行分区
                .keyBy(value -> value.f0)
                //指定窗口，每10秒个计算一次
                .timeWindow(Time.of(10, TimeUnit.SECONDS))
                //指定一个开始的值，对每一组内的元素进行归并操作，即第一个和第二个归并，结果再与第三个归并...
                .aggregate(new MyAppendAggregate());

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * union可以将多个流合并到一个流中，以便对合并的流进行统一处理。是对多个流的水平拼接。
     * 参与合并的流必须是同一种类型。
     */
    public static void unionDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream9000 = env.socketTextStream(LOCALHOST, 9000, "\n");
        DataStream<String> textStream9001 = env.socketTextStream(LOCALHOST, 9001, "\n");
        DataStream<String> textStream9002 = env.socketTextStream(LOCALHOST, 9002, "\n");

        DataStream<String> mapStream9000 = textStream9000.map(s -> "来自9000端口：" + s);
        DataStream<String> mapStream9001 = textStream9001.map(s -> "来自9001端口：" + s);
        DataStream<String> mapStream9002 = textStream9002.map(s -> "来自9002端口：" + s);

        //3.union用来合并两个或者多个流的数据，统一到一个流中
        DataStream<String> result = mapStream9000.union(mapStream9001, mapStream9002);

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * 根据指定的Key将两个流进行关联。
     */
    public static void windowJoinDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream9000 = env.socketTextStream(LOCALHOST, 9000, "\n");
        DataStream<String> textStream9001 = env.socketTextStream(LOCALHOST, 9001, "\n");
        //将输入处理一下，变为tuple2
        DataStream<Tuple2<String, String>> mapStream9000 = textStream9000
                .map((MapFunction<String, Tuple2<String, String>>) s -> Tuple2.of(s, "来自9000端口：" + s));

        DataStream<Tuple2<String, String>> mapStream9001 = textStream9001
                .map((MapFunction<String, Tuple2<String, String>>) s -> Tuple2.of(s, "来自9001端口：" + s));

        //3.两个流进行join操作，是inner join，关联上的才能保留下来
        DataStream<String> result = mapStream9000.join(mapStream9001)
                //关联条件，以第0列关联（两个source输入的字符串）
                .where(t1 -> t1.getField(0)).equalTo(t2 -> t2.getField(0))
                //以处理时间，每10秒一个滚动窗口
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                //关联后输出
                .apply((t1, t2) -> t1.getField(1) + "|" + t2.getField(1));

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    /**
     * 关联两个流，关联不上的也保留下来。
     */
    public static void coGroupDemo() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream9000 = env.socketTextStream(LOCALHOST, 9000, "\n");
        DataStream<String> textStream9001 = env.socketTextStream(LOCALHOST, 9001, "\n");
        //将输入处理一下，变为tuple2
        DataStream<Tuple2<String, String>> mapStream9000 = textStream9000
                .map((MapFunction<String, Tuple2<String, String>>) s -> Tuple2.of(s, "来自9000端口：" + s));

        DataStream<Tuple2<String, String>> mapStream9001 = textStream9001
                .map((MapFunction<String, Tuple2<String, String>>) s -> Tuple2.of(s, "来自9001端口：" + s));

        //3.两个流进行coGroup操作,没有关联上的也保留下来，功能更强大
        DataStream<String> result = mapStream9000.coGroup(mapStream9001)
                //关联条件，以第0列关联（两个source输入的字符串）
                .where(t1 -> t1.getField(0)).equalTo(t2 -> t2.getField(0))
                //以处理时间，每10秒一个滚动窗口
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                //关联后输出
                .apply((CoGroupFunction<Tuple2<String, String>, Tuple2<String, String>, String>) (iterable, iterable1, collector) -> {
                    StringBuilder stringBuffer = new StringBuilder();
                    stringBuffer.append("来自9000的stream:");
                    for (Tuple2<String, String> item : iterable) {
                        stringBuffer.append(item.f1).append(",");
                    }
                    stringBuffer.append("来自9001的stream:");
                    for (Tuple2<String, String> item : iterable1) {
                        stringBuffer.append(item.f1).append(",");
                    }
                    collector.collect(stringBuffer.toString());
                });

        //4.打印输出sink
        result.print();
        //5.开始执行
        env.execute();
    }

    public static void splitDemo(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> textStream = env.socketTextStream(LOCALHOST, 9000, "\n");
        //flink.1.11.1显示SplitStream类过时，推荐用keyBy的方式进行窗口处理或SideOutput侧输出流处理；注意，使用split切分后的流，不可二次切分，否则会抛异常
        SplitStream<Tuple3<String, String, Integer>> split = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple3.of(line.trim(), "111", 1))
                .split((OutputSelector<Tuple3<String, String, Integer>>) value -> {
                    List<String> output = new ArrayList<>();
                    if (value.f1.equals("man")) {
                        output.add("man");
                    } else {
                        output.add("girl");
                    }
                    return output;
                });

        //Datastream
        //DataStream<Tuple3<String, String, Integer>> dataStream = env.fromCollection(tuple3List);


        //查询指定名称的数据流
        DataStream<Tuple4<String, String, Integer, String>> dataStream1 = split.select("man")
                .map((MapFunction<Tuple3<String, String, Integer>, Tuple4<String, String, Integer, String>>) t3 -> Tuple4.of(t3.f0, t3.f1, t3.f2, "男"));

        DataStream<Tuple4<String, String, Integer, String>> dataStream2 = split.select("girl")
                .map((MapFunction<Tuple3<String, String, Integer>, Tuple4<String, String, Integer, String>>) t3 -> Tuple4.of(t3.f0, t3.f1, t3.f2, "女"));
        //打印:男
        dataStream1.print();
        //打印：女
        dataStream2.print();

        env.execute("flink Split job");
    }

    public static void StreamBroadcastDemo() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //设置并行度
        env.setParallelism(1);

        //自定义广播流，产生拦截数据的配置信息
        DataStreamSource<String> filterData = env.addSource(new RichSourceFunction<>() {

            private boolean isRunning = true;
            //测试数据集
            String[] data = new String[]{"java", "python", "scala"};

            /**
             * 模拟数据源，每1分钟产生一次数据，实现数据的跟新
             *
             * @param cxt
             * @throws Exception
             */
            @Override
            public void run(SourceContext<String> cxt) throws Exception {
                int size = data.length;
                while (isRunning) {
                    TimeUnit.MINUTES.sleep(1);
                    int seed = (int) (Math.random() * size);
                    //在数据集中随机生成一个数据进行发送
                    cxt.collect(data[seed]);
                    System.out.println("发送的关键字是：" + data[seed]);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });

        //1、定义数据广播的规则：
        MapStateDescriptor<String, String> configFilter
                = new MapStateDescriptor<>("configFilter", BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO);

        //2、对filterData进行广播
        BroadcastStream<String> broadcastConfig = filterData.setParallelism(1).broadcast(configFilter);

        //定义数据集
        DataStreamSource<String> dataStream = env.addSource(new RichSourceFunction<>() {
            private boolean isRunning = true;
            //测试数据集
            String[] data = new String[]{
                    "java代码量太大",
                    "python代码量少，易学习",
                    "php是web开发语言",
                    "scala流式处理语言，主要应用于大数据开发场景",
                    "go是一种静态强类型、编译型、并发型，并具有垃圾回收功能的编程语言"
            };

            /**
             * 模拟数据源，每3s产生一次
             *
             * @param ctx
             * @throws Exception
             */
            @Override
            public void run(SourceContext<String> ctx) throws Exception {
                int size = data.length;
                while (isRunning) {
                    TimeUnit.SECONDS.sleep(3);
                    int seed = (int) (Math.random() * size);
                    //在数据集中随机生成一个数据进行发送
                    ctx.collect(data[seed]);
                    System.out.println("上游发送的消息：" + data[seed]);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });

        //3、dataStream对广播的数据进行关联（使用connect进行连接）
        DataStream<String> result = dataStream.connect(broadcastConfig).process(new MyBroadcastProcessFunction());

        result.print();

        env.execute("broadcast test");
    }

    public static class MyBroadcastProcessFunction extends BroadcastProcessFunction<String, String, String> {
        //拦截的关键字
        private String keyWords = null;

        /**
         * open方法只会执行一次
         * 可以在这实现初始化的功能
         * 4、设置keyWords的初始值，否者会报错：java.lang.NullPointerException
         *
         * @param parameters
         * @throws Exception
         */
        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            keyWords = "java";
            System.out.println("初始化keyWords：java");
        }

        /**
         * 6、 处理流中的数据
         *
         * @param value
         * @param ctx
         * @param out
         * @throws Exception
         */
        @Override
        public void processElement(String value, ReadOnlyContext ctx, Collector<String> out) throws Exception {
            if (value.contains(keyWords)) {
                out.collect("拦截消息:" + value + ", 原因:包含拦截关键字：" + keyWords);
            }
        }

        /**
         * 5、对广播变量的获取更新
         *
         * @param value
         * @param ctx
         * @param out
         * @throws Exception
         */
        @Override
        public void processBroadcastElement(String value, Context ctx, Collector<String> out) throws Exception {
            keyWords = value;
            System.out.println("更新关键字：" + value);
        }
    }

    public static class MyAppendAggregate implements AggregateFunction<Tuple2<String, Integer>, String, String> {

        @Override
        public String createAccumulator() {
            return "结果：";
        }

        /**
         * Adds the given input value to the given accumulator, returning the
         * new accumulator value.
         *
         * <p>For efficiency, the input accumulator may be modified and returned.
         *
         * @param value       The value to add
         * @param accumulator The accumulator to add the value to
         * @return The accumulator with the updated state
         */
        @Override
        public String add(Tuple2<String, Integer> value, String accumulator) {
            return accumulator + " " + value.f0;
        }

        /**
         * Gets the result of the aggregation from the accumulator.
         *
         * @param accumulator The accumulator of the aggregation
         * @return The final aggregation result.
         */
        @Override
        public String getResult(String accumulator) {
            return accumulator;
        }

        /**
         * Merges two accumulators, returning an accumulator with the merged state.
         *
         * <p>This function may reuse any of the given accumulators as the target for the merge
         * and return that. The assumption is that the given accumulators will not be used any
         * more after having been passed to this function.
         *
         * @param a An accumulator to merge
         * @param b Another accumulator to merge
         * @return The accumulator with the merged state
         */
        @Override
        public String merge(String a, String b) {
            return a + " " + b;
        }
    }

}
