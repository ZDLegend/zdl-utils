package zdl.util.flink;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

public class Mapper extends ProcessFunction<String, Tuple2<String, String>> {
    private String flag;

    public Mapper(String flag) {
        this.flag = flag;
    }

    @Override
    public void processElement(String value, Context ctx, Collector<Tuple2<String, String>> out) throws Exception {
        out.collect(new Tuple2<>(value, flag));
    }
}
