package zdl.util.flink.demo

;

import java.util.Properties

import org.apache.flink.api.common.ExecutionMode
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.state.MapStateDescriptor
import org.apache.flink.api.common.time.Time
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.BroadcastStream
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.apache.flink.util.Collector

object CheckPointDemo {

  def main(args: Array[String]): Unit = {

    //获取执行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //用来决定在region (failover strategy)失败策略中的region范围
    env.getConfig.setExecutionMode(ExecutionMode.PIPELINED)

    /**
     * --------------------------------------checkpoint的配置-----------------------------------------------
     */
    env.enableCheckpointing(1000) //每1s checkpoint 一次
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE) //默认是EXACTLY_ONCE
    env.getCheckpointConfig.setCheckpointInterval(1000) //每隔 1s进行一次checkpoint 的工作
    env.getCheckpointConfig.setCheckpointTimeout(6000) //如果checkpoint操作在6s之内没有完成，那么就discard终端该checkpoint操作
    //true：假如在checkpoint过程中产生了Error，那么Task直接显示失败
    //false：产生了error，Task继续运行，checkpoint会降级到之前那个状态
    env.getCheckpointConfig.setTolerableCheckpointFailureNumber(1) //默认为true
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1) //在统一时间只能同时有1个checkpoint操作，其他的操作必须等当前操作执行完或者超时之后才能执行
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION) //清除或保留状态
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(0) //下一个checkpoint操作触发之前最小的阻塞时间，必须>=0

    /** --------------------------------------配置重启策略----------------------------------------------------
     * When a task failure happens, (当一个任务失败后)
     * Flink needs to restart the failed task and other affected tasks to recover the job to a normal state.
     * （Flink 需要重启失败的任务和其他受影响的task并恢复到一个正常的状态）
     * 重启配置与checkpoint设置有关：
     * 如果没有开启checkpoint，那么重启策略为：no restart！
     * 如果开启了checkpoint，那么重启策略默认为：fixed-delay strategy is used with Integer.MAX_VALUE
     *
     * restart-strategy 可以在flink-conf.yaml中进行设置，也可以通过env.setRestartStrategy（）设置
     */

    /**
     * 固定延迟重启策略(Fixed Delay Restart Strategy)
     * 固定延迟重启策略会尝试一个给定的次数来重启Job，如果超过了最大的重启次数，Job最终将失败。在连续的两次重启尝试之间，重启策略会等待一个固定的时间。
     * 重启策略可以配置flink-conf.yaml的下面配置参数来启用，作为默认的重启策略:
     *
     * restart-strategy: fixed-delay
     */
    //    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
    //      3, // 重启次数
    //      Time.of(10, TimeUnit.SECONDS) // 延迟时间间隔
    //    ))

    /**
     * 失败率重启策略
     * 失败率重启策略在Job失败后会重启，但是超过失败率后，Job会最终被认定失败。在两个连续的重启尝试之间，重启策略会等待一个固定的时间。
     * 失败率重启策略可以在flink-conf.yaml中设置下面的配置参数来启用:
     *
     * restart-strategy:failure-rate
     */
    //    env.setRestartStrategy(RestartStrategies.failureRateRestart(
    //      3, // 每个测量时间间隔最大失败次数
    //      Time.of(5, TimeUnit.MINUTES), //失败率测量的时间间隔
    //      Time.of(10, TimeUnit.SECONDS) // 两次连续重启尝试的时间间隔
    //    ))

    /**
     * 无重启策略
     * Job直接失败，不会尝试进行重启
     *
     * restart-strategy: none
     */
    //    env.setRestartStrategy(RestartStrategies.noRestart())

    //env.setRestartStrategy(new RestartStrategies.FallbackRestartStrategyConfiguration) //自动按照fixed-dalay重启策略

    /*env.setRestartStrategy(
      new RestartStrategies.FailureRateRestartStrategyConfiguration(
      10,
      Time.minutes(5),
      Time.seconds(10)))*/

    //env.setRestartStrategy(new RestartStrategies.FixedDelayRestartStrategyConfiguration(5,Time.seconds(4)))

    val config = new RestartStrategies.FailureRateRestartStrategyConfiguration(3, Time.minutes(5), Time.seconds(10))
    env.setRestartStrategy(config)

    val ds: DataStream[String] = env.addSource(new FlinkKafkaConsumer011[String]("topic", new SimpleStringSchema(), new Properties()))

    val ds1: DataStream[(Int, Char)] = env.fromElements((1, '男'), (2, '女'))

    val describer = new MapStateDescriptor[Int, Char]("genderInfo", classOf[Int], classOf[Char])

    val bcStream: BroadcastStream[(Int, Char)] = ds1.broadcast(describer)

    val resultStream: DataStream[String] = ds.connect(bcStream).process(
      new BroadcastProcessFunction[String, (Int, Char), String] {
        override def processElement(value: String,
                                    ctx: BroadcastProcessFunction[String, (Int, Char), String]#ReadOnlyContext,
                                    out: Collector[String]): Unit = {
          val gender: Char = ctx.getBroadcastState(describer).get(value.length).charValue()
          out.collect(String.valueOf(gender))
        }

        override def processBroadcastElement(value: (Int, Char), ctx: BroadcastProcessFunction[String, (Int, Char), String]#Context, out: Collector[String]): Unit = {
          ctx.getBroadcastState(describer).put(value._1, value._2)

        }
      }
    )

    ds.print("before:")
    resultStream.print("after:")
    env.execute("checkpoint")
  }
}