package zdl.util.flink;


import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.Utils;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.operators.ProcessOperator;
import org.apache.flink.streaming.api.operators.SimpleOperatorFactory;
import org.apache.flink.streaming.api.operators.StreamSink;
import org.apache.flink.streaming.api.transformations.OneInputTransformation;
import org.apache.flink.streaming.api.transformations.SinkTransformation;
import org.apache.flink.util.Collector;


public class OperatorChain {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Transformation<String> sourceTransformation = env.socketTextStream("192.168.110.50", 7777).getTransformation();

        StringFilter filterA = new StringFilter("A");
        StringFilter filterB = new StringFilter("B");

        OneInputTransformation<String, String> filterATransformation = new OneInputTransformation<>(
                sourceTransformation,
                "filterA ",
                SimpleOperatorFactory.of(new ProcessOperator<>(env.clean(filterA))),
                TypeExtractor.getUnaryOperatorReturnType(
                        filterA,
                        ProcessFunction.class,
                        0,
                        1,
                        TypeExtractor.NO_INDEX,
                        sourceTransformation.getOutputType(),
                        Utils.getCallLocationName(),
                        true),
                env.getParallelism());

        OneInputTransformation<String, String> filterBTransformation = new OneInputTransformation<>(
                filterATransformation,
                "filter B",
                SimpleOperatorFactory.of(new ProcessOperator<>(env.clean(filterB))),
                TypeExtractor.getUnaryOperatorReturnType(
                        filterB,
                        ProcessFunction.class,
                        0,
                        1,
                        TypeExtractor.NO_INDEX,
                        filterATransformation.getOutputType(),
                        Utils.getCallLocationName(),
                        true),
                env.getParallelism());

        PrintSinkFunction<String> printFunction = new PrintSinkFunction<>();
        StreamSink<String> sinkOperator = new StreamSink<>(env.clean(printFunction));


        env.addOperator(new SinkTransformation(filterBTransformation, "sink", sinkOperator, env.getParallelism()));
        env.addOperator(filterBTransformation);
        env.addOperator(filterATransformation);

        env.execute("transformation");
    }

}

class StringFilter extends ProcessFunction<String, String> {
    private String flag;

    public StringFilter(String flag) {
        this.flag = flag;
    }

    @Override
    public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
        if (!value.equals(this.flag)) {
            out.collect(value);
        }
    }
}