# About
Components:
1. Pulsar standalone
1. Java-based data generator
1. InfluxDB with Graphite endpoint for storing metrics
1. Grafana for building dashboards
1. Spark structured streaming job

How it works:
1. Java-based generator ingests rows in pulsar in format(id: uuid, order: long, value: string, timestamp: long)
1. Spark structured streaming job reads data from pulsar using connector
   and saves into Hudi table optimized for cdc case(with partitioning, async
   compaction, global index, merge-on-read, etc)
1. Spark sends both built-in and custom-defined `latency` metrics that represents
   the latest processed timestamp
1. Grafana shows metric values(built-in and custom)
1. The test output is queryable either using dataframe API / spark-sql(snapshot and updates)

What I did for the first time:
1. Writing code in scala.
1. Writing tests in scala/for spark jobs.
1. Setting up metrics for spark(although you might think it should be easy, 
   before spark 3.0 you needed to write all the code by yourself, 
   including the sending part, this is why Hudi has different 
   from the built-in mechanism and needs to be set up separately).
1. Worked with grafana(setup and writing dashboards).

Next steps:
1. Continue learning scala.
1. Continue learning structured streaming.
1. Deploy to Google cloud to test on more machines(GC offers 500$ for free as a start bonus).
1. Try to write an OSS package for spark metrics. there is one existing that uses monkey-patching to behave as spark 
   creates metrics, but it uses spark private packages that may be changed. Also, spark supported version is 1.6, so 
   it may not work for future versions.

# Spark-sql useful commands
```sql
create table cdc using hudi
options (
   primaryKey = 'id',
   preCombineField = 'order',
   type = 'mor'
)
partitioned by (year,month,day)
location '/Users/vmakarevich/IdeaProjects/dap-spark/output';

select * from cdc;
```