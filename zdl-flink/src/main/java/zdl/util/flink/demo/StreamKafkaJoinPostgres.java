package zdl.util.flink.demo;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.api.java.typeutils.MapTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 需求：
 * 将postgresql中的数据读取到streamPgSql中，作为配置数据，包含code和name
 * 同时将streamPgSql通过广播，减少数据的内存消耗
 * <p>
 * 将kafka中的数据与postgresql中的数据进行join，清洗，得到相应的数据
 * <p>
 * Broadcast会将state广播到每个task
 * 注意该state并不会跨task传播
 * 对其修改，仅仅是作用在其所在的task
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/18/ 14:26
 */
public class StreamKafkaJoinPostgres {
    public static void main(String[] args) throws Exception {
        final String bootstrap = "bootstrap";
        final String zookeeper = "zookeeper";
        final String topic = "web";
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//        env.enableCheckpointing(5000);  //检查点 每5000ms
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", bootstrap);//kafka的节点的IP或者hostName，多个使用逗号分隔
        properties.setProperty("zookeeper.connect", zookeeper);//zookeeper的节点的IP或者hostName，多个使用逗号进行分隔
        properties.setProperty("group.id", "flinkStream");//flink consumer flink的消费者的group.id

        //1、读取postgresQL的配置消息
        DataStream<String> streamPgSql = env.addSource(new PostgresqlSource());

        final DataStream<HashMap<String, String>> conf = streamPgSql.map(value -> {
            String[] tokens = value.split("\\t");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(tokens[0], tokens[1]);
            System.out.println(tokens[0] + " : " + tokens[1]);
            return hashMap;
        });

        //2、创建MapStateDescriptor规则，对广播的数据的数据类型的规则
        MapStateDescriptor<String, Map<String, String>> ruleStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState"
                , BasicTypeInfo.STRING_TYPE_INFO
                , new MapTypeInfo<>(String.class, String.class));

        //3、对conf进行broadcast返回BroadcastStream
        final BroadcastStream<HashMap<String, String>> confBroadcast = conf.broadcast(ruleStateDescriptor);

        //读取kafka中的stream
        FlinkKafkaConsumer011<String> webStream = new FlinkKafkaConsumer011<>(topic, new SimpleStringSchema(), properties);
        webStream.setStartFromEarliest();
        DataStream<String> kafkaData = env.addSource(webStream).setParallelism(1);
        DataStream<Tuple5<String, String, String, String, String>> map
                = kafkaData.map(value -> {
            String[] tokens = value.split("\\t");
            return new Tuple5<>(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4]);
        })
                //使用connect连接BroadcastStream，然后使用process对BroadcastConnectedStream流进行处理
                .connect(confBroadcast)
                .process(new MyBroadcastProcessFunction());

        map.print();
        env.execute("Broadcast test kafka");
    }

    public static class MyBroadcastProcessFunction
            extends BroadcastProcessFunction<Tuple5<String, String, String, String, String>, HashMap<String, String>, Tuple5<String, String, String, String, String>> {
        private final HashMap<String, String> keyWords = new HashMap<>();
        MapStateDescriptor<String, Map<String, String>> ruleStateDescriptor = new MapStateDescriptor<>("RulesBroadcastState"
                , BasicTypeInfo.STRING_TYPE_INFO
                , new MapTypeInfo<>(String.class, String.class));

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
        }

        @Override
        public void processElement(Tuple5<String, String, String, String, String> value, ReadOnlyContext ctx, Collector<Tuple5<String, String, String, String, String>> out) throws Exception {
//                        Thread.sleep(10000);
            if (keyWords.isEmpty()) {
                Thread.sleep(10000);
                System.out.println("初始化数据：config，请等待！！！");
            }

            String result = keyWords.get(value.f3);
            if (result == null) {
                out.collect(new Tuple5<>(value.f0, value.f1, value.f2, value.f3, value.f4));
            } else {
                out.collect(new Tuple5<>(value.f0, value.f1, value.f2, result, value.f4));
            }

        }

        /**
         * 接收广播中的数据
         */
        @Override
        public void processBroadcastElement(HashMap<String, String> value, Context ctx, Collector<Tuple5<String, String, String, String, String>> out) {
//                        System.out.println("收到广播数据："+value.values());
//                        BroadcastState <String, Map <String, String>> broadcastState = ctx.getBroadcastState(ruleStateDescriptor);
            keyWords.putAll(value);
        }
    }
}
