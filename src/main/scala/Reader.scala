package com.vito.dap_spark

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

object Reader {
  def setup(): SparkConf = {
    new SparkConf().
      setMaster("local[*]").
      setAppName("dap-test-reader").
      set("spark.metrics.namespace", "dap-hudi").
      set("spark.serializer", "org.apache.spark.serializer.KryoSerializer").
      set("spark.metrics.conf.*.sink.graphite.class", "org.apache.spark.metrics.sink.GraphiteSink").
      set("spark.metrics.conf.*.sink.graphite.host", "127.0.0.1").
      set("spark.metrics.conf.*.sink.graphite.port", "2003").
      set("spark.metrics.conf.*.sink.graphite.prefix", "spark-metrics").
      set("spark.metrics.conf.*.sink.graphite.period", "1").
      set("spark.metrics.conf.*.sink.graphite.unit", "seconds").
      set("spark.plugins", "com.vito.dap_spark.metric.MetricPlugin").
      set("spark.metrics.conf.*.source.jvm.class", "org.apache.spark.metrics.source.JvmSource").
      set("spark.sql.streaming.metricsEnabled", "true")
  }

  def main(args: Array[String]): Unit = {
    val sparkConf = setup()
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val df = spark.read.
      format("hudi").
      load("file:///Users/vmakarevich/IdeaProjects/dap-spark/output")
    df.createOrReplaceTempView("hudi_dap")
    spark.sql("select id, order, timestamp, value from hudi_dap").
      repartition(1).
      write.
      mode(SaveMode.Overwrite).
      format("json").
      save("file:///Users/vmakarevich/IdeaProjects/dap-spark/json-compacted-output")
  }
}


