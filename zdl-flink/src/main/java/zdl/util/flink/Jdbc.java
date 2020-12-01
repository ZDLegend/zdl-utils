package zdl.util.flink;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.io.jdbc.JDBCAppendTableSink;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2020/11/02/ 16:35
 */
public class Jdbc {

    public static void flinkJdbc() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(5000);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        String query = "select id,iname,sex,score from test1";
        Table result = tableEnv.sqlQuery(query);

        JDBCAppendTableSink sink = JDBCAppendTableSink.builder()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://localhost:3306/flink")
                .setUsername("root")
                .setPassword("123456")
                .setParameterTypes(
                        new TypeInformation[]{BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO,
                                BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO})
                .setQuery("REPLACE INTO test2 (id,iname,sex,score) VALUES(?,?,?,?)")
                .build();

        DataStream<Row> stream = tableEnv.toAppendStream(result, Row.class);

        stream.print();
        sink.emitDataStream(stream);

        env.execute();
    }
}
