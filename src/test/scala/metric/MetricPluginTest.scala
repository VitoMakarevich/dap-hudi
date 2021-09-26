package com.vito.dap_spark
package metric

import com.codahale.metrics.MetricRegistry
import org.apache.spark.api.plugin.{ExecutorPlugin, PluginContext}
import org.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters.mapAsJavaMapConverter

class MetricPluginTest extends AnyFunSpec with Matchers with MockitoSugar {
  describe("MetricPlugin") {
    it("Should return null for driver part") {
      val plugin = new MetricPlugin
      plugin.driverPlugin should be (null)
    }

    it("Should create executor plugin") {
      val plugin = new MetricPlugin
      val executorPlugin = plugin.executorPlugin
      val pluginContext = mock[PluginContext]
      val metricRegistry = mock[MetricRegistry]
      when(pluginContext.metricRegistry) thenReturn(metricRegistry)

      executorPlugin.init(pluginContext, Map.empty[String, String].asJava)
      verify(metricRegistry).register(MetricRegistry.name("Latency"), Metrics.latency)
      plugin.executorPlugin shouldBe a[ExecutorPlugin]
    }
  }
}
