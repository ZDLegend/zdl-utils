package zdl.util.flink;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

class Filter extends ProcessFunction<String, String> {
    private String flag;

    public Filter(String flag) {
        this.flag = flag;
    }

    @Override
    public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
        if (!value.equals(this.flag)) {
            out.collect(value);
        }
    }
}