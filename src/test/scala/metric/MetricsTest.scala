package com.vito.dap_spark
package metric

import org.mockito.MockitoSugar
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class MetricsTest extends AnyFunSpec with Matchers with MockitoSugar {
  describe("Latency metric") {
    it("Should test the default value") {
      val latency = Metrics.latency

      latency.getValue should equal (0)
    }

    it("Should able to set latency as difference between now and provided value") {
      withObjectMocked[SystemTime.type] {
        when(SystemTime.now) thenAnswer(1632490567511L)
        val latency = Metrics.latency
        latency.addMaxTimestamp(1632490567510L)

        latency.getValue should equal (1)
      }
    }
  }
}
