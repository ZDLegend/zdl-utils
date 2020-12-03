package zdl.util.flink;

import org.apache.flink.api.common.functions.AbstractRichFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataStreamDAG {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        Map<Integer, StreamNode> nodeMap = new LinkedHashMap<>();

        DataStream<String> sourceStream = env.socketTextStream("192.168.110.50", 7777);
        StreamNode sourceNode = new StreamNode(0, null, -1, "source");
        sourceNode.setDataStream(sourceStream);

        ProcessFunction fa = getFunction("dag.transformation.Filter", "A");
        StreamNode op1 = new StreamNode(1, fa, 0, "operation");

        ProcessFunction fb = getFunction("dag.transformation.Mapper", "map");
        StreamNode op2 = new StreamNode(2, fb, 1, "operation");
        nodeMap.put(op2.getId(), op2);

        PrintSinkFunction<String> printFunction = new PrintSinkFunction<>();
        StreamNode sinkNode = new StreamNode(3, printFunction, 2, "sink");

        nodeMap.put(sourceNode.getId(), sourceNode);
        nodeMap.put(op1.getId(), op1);
        nodeMap.put(op2.getId(), op2);
        nodeMap.put(sinkNode.getId(), sinkNode);

        for (StreamNode node : nodeMap.values()) {
            switch (node.getType()) {
                case "source":
                    break;
                case "operation":
                    node.setDataStream(nodeMap.get(node.getPreNodeId()).getDataStream().process((ProcessFunction) node.getOp()));
                    break;
                case "sink":
                    nodeMap.get(node.getPreNodeId()).getDataStream().addSink((RichSinkFunction) node.getOp());
                    break;
            }
        }

        env.execute("");
    }

    private static ProcessFunction getFunction(String className, String flag) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> clazz = Class.forName(className);

        Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        Object function = constructor.newInstance(flag);
        ProcessFunction func = (ProcessFunction) function;
        return func;
    }

    private static class StreamNode {
        private int id;
        private AbstractRichFunction op;
        private int preNodeId;
        private String type;
        private DataStream dataStream;


        public StreamNode(int id, AbstractRichFunction op, int preNodeId, String type) {
            this.id = id;
            this.op = op;
            this.preNodeId = preNodeId;
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public DataStream getDataStream() {
            return dataStream;
        }

        public void setDataStream(DataStream ds) {
            this.dataStream = ds;
        }

        public int getPreNodeId() {
            return preNodeId;
        }

        public void setPreNodeId(int preNodeId) {
            this.preNodeId = preNodeId;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public DataStream getInput() {
            return dataStream;
        }

        public void setInput(DataStream input) {
            this.dataStream = input;
        }

        public AbstractRichFunction getOp() {
            return op;
        }

        public void setOp(ProcessFunction op) {
            this.op = op;
        }
    }

}