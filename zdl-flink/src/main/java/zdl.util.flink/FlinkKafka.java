package zdl.util.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.formats.json.JsonNodeDeserializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import java.util.Properties;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/02/ 13:51
 */
public class FlinkKafka {
    public static void kafkaConsumer() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        //配置kafka信息
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "172.24.112.13:9092,172.24.112.14:9092,172.24.112.15:9092");
        props.setProperty("zookeeper.connect", "172.24.112.13:2181,172.24.112.14:2181,172.24.112.15:2181");
        props.setProperty("group.id", "test");

        //读取数据
        FlinkKafkaConsumer011<ObjectNode> consumer
                = new FlinkKafkaConsumer011<>("topic001", new JsonNodeDeserializationSchema(), props);

        //设置只读取最新数据
        consumer.setStartFromLatest();

        //添加kafka为数据源
        DataStreamSource<ObjectNode> stream = env.addSource(consumer);

        stream.print();

        env.execute("Kafka_Flink");
    }

    public static void kafkaProducer(DataStreamSource<String> dataStreamSource) {
        //配置kafka信息
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "172.24.112.13:9092,172.24.112.14:9092,172.24.112.15:9092");

        FlinkKafkaProducer011<String> producer
                = new FlinkKafkaProducer011<>("topic001", new SimpleStringSchema(), props);
        dataStreamSource.addSink(producer);
    }
}
