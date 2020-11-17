package zdl.util.flink.demo

import com.alibaba.fastjson.JSON.parseObject
import com.alibaba.fastjson.JSONObject
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import zdl.util.flink.demo.SideOutputDemo.KEY

/**
 * @author ZDLegend
 * @date 2020/11/17/ 17:49
 * @version 1.0
 */
case class SideOutput(tpr: Double) extends ProcessFunction[JSONObject, JSONObject] {
  override def processElement(value: JSONObject,
                              ctx: ProcessFunction[JSONObject, JSONObject]#Context,
                              out: Collector[JSONObject]): Unit = {
    if (value.getInteger("id") >= tpr) {
      out.collect(value)
    } else {
      ctx.output(new OutputTag[JSONObject](KEY), value)
    }
  }
}

object SideOutputDemo {

  var KEY: String = "low-temp";

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val inputDStream: DataStream[String] = env.socketTextStream("hadoop102", 7777)

    val dataDstream: DataStream[JSONObject] = inputDStream.map(
      data => {
        return parseObject(data);
      })

    val resultDStrem: DataStream[JSONObject] = dataDstream
      .process(SideOutput(30.0))

    dataDstream.print("data")
    // 主流为高于30.0度
    resultDStrem.print("high")
    // 侧输出流需要进行获取
    resultDStrem.getSideOutput(new OutputTag[JSONObject](KEY)).print("low")

    env.execute("SideOutput test job")
  }

}
