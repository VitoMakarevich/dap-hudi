package com.vito.dap_spark
package metric

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class SystemTimeTest extends AnyFunSpec with Matchers {
  describe("SystemTime") {
    it("Should return time now") {
      SystemTime.now shouldBe a[Long]
    }
  }

}
