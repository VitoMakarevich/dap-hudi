package com.vito.dap_spark
package metric

import java.util.Date

object SystemTime {
  def now(): Long = new Date().getTime
}
