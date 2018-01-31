package timestone.engine.mapreduce.factory

import com.google.inject.{ImplementedBy, Inject, Singleton}
import timestone.engine.spark.factory.SparkFactory
//import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.mapred.JobConf
//import timestone.core.conf.factory.ConfFactory
import timestone.storage.hbase.factory.HbaseFactory
import timestone.engine.spark.factory

/**
  * Created by lky on 17-4-11.
  */
@ImplementedBy(classOf[MapReduceFactoryImp])
trait  MapReduceFactory {

  def getJobConfWrite(table:String):JobConf

  def getJobConfDel(table:String):JobConf

  def getJobConfScan(start:String,end:String,table:String,family:String):JobConf
  def getJobConfScanColumn(start:String,end:String,table:String,column:String):JobConf
}

@Singleton
class MapReduceFactoryImp  @Inject()(hbaseFactory:HbaseFactory)
  extends  MapReduceFactory with Serializable{

  override def getJobConfWrite(table:String): JobConf = {

    val hbaseConf = hbaseFactory.getConfig()

    val jobConf = new JobConf(hbaseConf)
    //设置输出的格式
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    //设置输出的表名
    jobConf.set(TableOutputFormat.OUTPUT_TABLE,table)

    jobConf
  }

  override def getJobConfDel(table:String): JobConf = {
    val hbaseConf = hbaseFactory.getConfig()

    val jobConf = new JobConf(hbaseConf)
    //设置输出的格式
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    //设置输出的表名
    jobConf.set(TableInputFormat.INPUT_TABLE,table)
    jobConf.set(TableOutputFormat.OUTPUT_TABLE,table)


    jobConf

  }

  override def getJobConfScan(start:String,end:String,table:String,family:String): JobConf = {

    val hbaseConf = hbaseFactory.getConfig()

    val jobConf = new JobConf(hbaseConf)
    //设置输出的格式
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    //设置输出的表名
    jobConf.set(TableInputFormat.INPUT_TABLE,table)
    jobConf.set(TableInputFormat.SCAN_TIMERANGE_START,start)
    jobConf.set(TableInputFormat.SCAN_TIMERANGE_END,end)
    jobConf.set(TableInputFormat.SCAN_COLUMN_FAMILY,family)
    //jobConf.set(TableInputFormat.SCAN_COLUMNS,"materdata:username")

    jobConf
  }

  // scan 列
  override def getJobConfScanColumn(start:String,end:String,table:String,column:String): JobConf = {

    val hbaseConf = hbaseFactory.getConfig()

    val jobConf = new JobConf(hbaseConf)
    //设置输出的格式
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    //设置输出的表名
    jobConf.set(TableInputFormat.INPUT_TABLE,table)
    jobConf.set(TableInputFormat.SCAN_TIMERANGE_START,start)
    jobConf.set(TableInputFormat.SCAN_TIMERANGE_END,end)
    jobConf.set(TableInputFormat.SCAN_COLUMNS,column)

    jobConf
  }


}

