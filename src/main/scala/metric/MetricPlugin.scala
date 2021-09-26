package com.vito.dap_spark
package metric

import com.codahale.metrics.{MetricRegistry}
import org.apache.spark.api.plugin.{DriverPlugin, ExecutorPlugin, PluginContext, SparkPlugin}

class MetricPlugin extends SparkPlugin {

  override def driverPlugin(): DriverPlugin = null

  override def executorPlugin(): ExecutorPlugin = {
    new ExecutorPlugin {
      override def init(myContext:PluginContext, extraConf:java.util.Map[String, String])  = {
        val metricRegistry = myContext.metricRegistry
        metricRegistry.register(MetricRegistry.name("Latency"), Metrics.latency)
      }
    }
  }

}


