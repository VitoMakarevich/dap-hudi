name := "dap-spark"

version := "0.1"

idePackagePrefix := Some("com.vito.dap_spark")
scalaVersion := "2.12.10"
val pulsar4ScalaVersion = "2.7.3"
val sparkVersion = "3.1.2"

libraryDependencies += "io.streamnative.connectors" %% "pulsar-spark-connector" % "3.1.1.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test"
libraryDependencies += "org.mockito" %% "mockito-scala" % "1.16.42" % "test"
libraryDependencies += "org.apache.spark" %% "spark-sql" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-avro" % sparkVersion
libraryDependencies += "org.apache.spark" %% "spark-streaming" % sparkVersion
libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"
libraryDependencies += "org.apache.hudi" %% "hudi-spark3-bundle" % "0.9.0"