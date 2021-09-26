package com.vito.dap_spark
package metric

import com.codahale.metrics.Gauge

object Metrics {
  val latency = new Gauge[Long] {
    private var value: Long = _;
    def addMaxTimestamp (value: Long): Unit = {
      this.value = SystemTime.now - value
    }

    override def getValue: Long = this.value
  }
}
