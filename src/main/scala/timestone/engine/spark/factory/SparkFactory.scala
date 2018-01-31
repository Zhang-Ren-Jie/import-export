package timestone.engine.spark.factory

import com.google.inject.{ImplementedBy, Inject, Singleton}
import org.apache.hadoop.hbase.client.{Delete, Get, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.{TableInputFormat, TableOutputFormat}
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import timestone.engine.mapreduce.factory.MapReduceFactory

/**
  * Created by lky on 17-4-11.
  */
@ImplementedBy(classOf[SparkFactoryImp])
trait  SparkFactory {

  def getSparkContext():SparkContext
  // 不限列族的导入
  def WriteSpark(filename: String,table:String)(fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
                (implicit times:Long = 1499408484000L): Unit

  // 导入
  def writeSpark(filename: String,table:String)(fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
                (implicit family:String="homoionym",times:Long = 1496650719000L): Unit

  //按版本删除列族中数据
  def deleteSpark(table:String)(fun: (RDD[(ImmutableBytesWritable, Result)]) => RDD[(ImmutableBytesWritable, Delete)] )
                 (implicit family1:String="homoionym",times1:Long = 1496650719000L): Unit
  //导出 指定列族
  def scanSpark(filename: String,start: String,end: String,table:String,family: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit

  // count 指定 列
  def countSpark(filename: String,start: String,end: String,table:String,family: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit
  //导出 指定列
  def scanSparkColumn(filename: String,start: String,end: String,table:String,column: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit

  // get
  def getSpark(filename: String,table:String)(fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Get)])

  //导入新格式的数据
  def writeSparkPro(filename: String,table:String)(fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
                (implicit family:String="homoionym",times:Long = 1496650719000L): Unit


}

@Singleton
class  SparkFactoryImp  @Inject()(mapReduceFactory: MapReduceFactory)  extends SparkFactory with Serializable{

  override def getSparkContext(): SparkContext = {
    val sparkConf = new SparkConf()
      .setAppName("importExport").setMaster("local")
    new SparkContext(sparkConf)
  }

  //写入新格式数据
  override def writeSparkPro(filename: String,table:String)
                         (fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
                         (implicit family:String="homoionym",times:Long = 1496650719000L): Unit = {
    //获取spark 配置
    val sc = getSparkContext()
    //获取hbase 配置
    val jobConfig = mapReduceFactory.getJobConfWrite(table)
    //写入文件
    val indataRDD = sc.textFile(filename)
    val reducerdd = indataRDD.map {
      a =>
        val b = a.split("\\|")
        ( ( b(0),b(1),b(2) ), b(3) )
    }.reduceByKey((x,y) => x+","+y).map{a=> a._1._1+"|"+a._1._2+"|"+a._1._3+"|"+a._2}
println(reducerdd)
    try {
      @transient
      //筛到fun 方法里
      val rdd = fun(reducerdd)
      rdd.saveAsHadoopDataset(jobConfig)
    } finally {
      sc.stop()
    }
  }


  override def WriteSpark(filename: String,table:String)
                         (fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
                         (implicit times:Long = 1499408484000L): Unit = {
    //获取spark 配置
    val sc = getSparkContext()
    //获取hbase 配置
    val jobConfig = mapReduceFactory.getJobConfWrite(table)
    //写入文件
    val indataRDD = sc.textFile(filename)
    try {
      @transient
      //筛到fun 方法里
      val rdd = fun(indataRDD)
      rdd.saveAsHadoopDataset(jobConfig)
    } finally {
      sc.stop()
    }
  }

  //写入
  override def writeSpark(filename: String,table:String)
    (fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Put)])
    (implicit family:String="homoionym",times:Long = 1496650719000L): Unit = {
    //获取spark 配置
    val sc = getSparkContext()
    //获取hbase 配置
    val jobConfig = mapReduceFactory.getJobConfWrite(table)
    //写入文件
    val indataRDD = sc.textFile(filename)
    try {
      @transient
      //筛到fun 方法里
      val rdd = fun(indataRDD)
      rdd.saveAsHadoopDataset(jobConfig)
    } finally {
      sc.stop()
    }
  }

  //删除
  override def deleteSpark(table:String)(fun: (RDD[(ImmutableBytesWritable, Result)]) => RDD[(ImmutableBytesWritable, Delete)])
                          (implicit family1:String="homoionym",times1:Long = 1496650719000L): Unit = {

    val sc = getSparkContext()
    val jobConfig = mapReduceFactory.getJobConfDel(table)
    //设置输出格式
    val job =  Job.getInstance(jobConfig, this.getClass.getName.split('$')(0))
    job.setOutputFormatClass(classOf[TableOutputFormat[String]])
    //读取数据并转化成rdd
    val hbaseRDD = sc.newAPIHadoopRDD(jobConfig, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
    //筛到fun 方法里
    val rdd = fun(hbaseRDD)
    rdd.saveAsNewAPIHadoopDataset(job.getConfiguration)

    sc.stop()
  }

  //扫描
  override def scanSpark(filename: String,start: String,end: String,table:String,family: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit = {
    val sc = getSparkContext()
    val jobConfig = mapReduceFactory.getJobConfScan(start,end,table,family)
    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(jobConfig, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
    //筛到fun 方法里
    val rdd1 = fun(hBaseRDD)
    rdd1.saveAsTextFile(filename)
    sc.stop()
  }

  //扫描
  override def countSpark(filename: String,start: String,end: String,table:String,family: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit = {
    val sc = getSparkContext()
    val jobConfig = mapReduceFactory.getJobConfScanColumn(start,end,table,family)
    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(jobConfig, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
    //筛到fun 方法里
    val rdd1 = fun(hBaseRDD)
    var count = rdd1.map{
      a =>
        val b = a.split("-")
          b(0)+"-"+b(1)
    }.map((_,1)).groupBy(_._1).mapValues(_.foldLeft(0){_+_._2})
    count.saveAsTextFile(filename)

    sc.stop()
  }

  // 扫描 列
  override def scanSparkColumn(filename: String,start: String,end: String,table:String,column: String)(fun: (RDD[(ImmutableBytesWritable, Result)]) =>RDD[String]) : Unit = {
    val sc = getSparkContext()
    val jobConfig = mapReduceFactory.getJobConfScanColumn(start,end,table,column)
    //读取数据并转化成rdd
    val hBaseRDD = sc.newAPIHadoopRDD(jobConfig, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
    //筛到fun 方法里
    val rdd1 = fun(hBaseRDD)
    rdd1.saveAsTextFile(filename)
    sc.stop()
  }

  override def getSpark(filename: String,table:String)(fun: (RDD[String]) => RDD[(ImmutableBytesWritable, Get)]): Unit = {
    //获取spark 配置
    val sc = getSparkContext()
    //获取hbase 配置
    val jobConfig = mapReduceFactory.getJobConfWrite(table)
    //写入文件
    val indataRDD = sc.textFile(filename)
    try {
      @transient
      //筛到fun 方法里
      val rdd = fun(indataRDD)
      rdd.saveAsHadoopDataset(jobConfig)
    } finally {
      sc.stop()
    }

  }




}