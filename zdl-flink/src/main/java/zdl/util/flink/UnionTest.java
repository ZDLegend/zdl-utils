package zdl.util.flink;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


public class UnionTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment(
                "192.168.110.50",
                8081);
        env.setParallelism(1);
        DataStream<String> s1 = env.socketTextStream("192.168.110.50", 7777);
        //DataStream<String> s2 = env.socketTextStream("192.168.110.50", 7778);

        s1.print();
        JobExecutionResult test = env.execute("12345");
        //JobClient jobClient = env.executeAsync("test");
        System.out.println(test.getJobID());

    }
}
