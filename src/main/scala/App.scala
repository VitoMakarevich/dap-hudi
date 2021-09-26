package com.vito.dap_spark

import metric.Metrics

import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig.TBL_NAME
import org.apache.hudi.keygen.ComplexKeyGenerator
import org.apache.spark.SparkConf
import org.apache.hudi.config.HoodieIndexConfig._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql._

object App {
  def setup(): SparkConf = {
    new SparkConf().
      setMaster("local[*]").
      setAppName("dap-test").
      set("spark.metrics.namespace", "dap-hudi").
      set("spark.serializer", "org.apache.spark.serializer.KryoSerializer").
      set("spark.metrics.conf.*.sink.graphite.class", "org.apache.spark.metrics.sink.GraphiteSink").
      set("spark.metrics.conf.*.sink.graphite.host", "127.0.0.1").
      set("spark.metrics.conf.*.sink.graphite.port", "2003").
      set("spark.metrics.conf.*.sink.graphite.prefix", "spark-metrics").
      set("spark.metrics.conf.*.sink.graphite.period", "10").
      set("spark.metrics.conf.*.sink.graphite.unit", "seconds").
      set("spark.plugins", "com.vito.dap_spark.metric.MetricPlugin").
      set("spark.metrics.conf.*.source.jvm.class", "org.apache.spark.metrics.source.JvmSource").
      set("spark.sql.streaming.metricsEnabled", "true")
  }

  def main(args: Array[String]): Unit = {
    val sparkConf = setup()
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val serviceUrl = "pulsar://localhost:6650/"
    val topic = "persistent://public/default/cdc"
    val pulsarAdminUrl = "http://localhost:8080"

    val df = spark.readStream.
      format("pulsar").
      option("service.url", serviceUrl).
      option("admin.url", pulsarAdminUrl).
      option("topic", topic).
      option("startingOffsets", "earliest").
      load.
      select("id", "order", "value", "timestamp")

    val query = df.writeStream.
      trigger(Trigger.ProcessingTime("30 seconds")).
      option("checkpointLocation", "file:///Users/vmakarevich/IdeaProjects/dap-spark/checkpoint").
      foreachBatch { (ds: Dataset[Row], batchId: Long) =>{
        ds.persist()
        val max = ds.select(col("timestamp")).agg(functions.max("timestamp")).head.getAs[Long](0)
        Metrics.latency.addMaxTimestamp(max)
        ds.
          withColumn("year", year(to_date(from_unixtime(col("timestamp") / 1000)))).
          withColumn("month", month(to_date(from_unixtime(col("timestamp") / 1000)))).
          withColumn("day", dayofmonth(to_date(from_unixtime(col("timestamp") / 1000)))).
          write.
          format("hudi").
          option(PRECOMBINE_FIELD.key, "order").
          option(RECORDKEY_FIELD.key, "id").
          option(TBL_NAME.key, "cdc1").
          option(OPERATION.key, UPSERT_OPERATION_OPT_VAL).
          option(TABLE_TYPE.key, MOR_TABLE_TYPE_OPT_VAL).
          option(KEYGENERATOR_CLASS_NAME.key, classOf[ComplexKeyGenerator].getName).
          option(PARTITIONPATH_FIELD.key, "year,month,day").
          option(ASYNC_COMPACT_ENABLE.key, "true").
          option(HIVE_STYLE_PARTITIONING.key, "true").
          option(INDEX_TYPE.key, "GLOBAL_SIMPLE").
          mode(SaveMode.Overwrite).
          save("file:///Users/vmakarevich/IdeaProjects/dap-spark/output")
        ds.unpersist()
      } : Unit
    }.start()

    query.awaitTermination()
  }
}
