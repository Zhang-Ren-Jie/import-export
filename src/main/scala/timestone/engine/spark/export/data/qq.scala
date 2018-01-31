package timestone.engine.spark.export.data

import org.apache.hadoop.hbase.client
import org.apache.hadoop.hbase.client.{Delete, Put, Result}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import timestone.engine.spark.factory.SparkFactory

object qq {


  def getSparkContext(): SparkContext = {
    val sparkConf = new SparkConf()
      .setAppName("importExport").setMaster("local")
    new SparkContext(sparkConf)
  }


  def main(args: Array[String]): Unit = {
    val sc = getSparkContext()
    val indataRDD = sc.makeRDD(Array("A|b|c|0"))
    val reducerdd = indataRDD.map{
      a =>
        val strings = a.split("\\|")
        ((strings(0),strings(1),strings(2)),strings(3))

    }.reduceByKey((x,y) => x+","+y).map{a=> a._1._1+"|"+a._1._2+"|"+a._1._3+"|"+a._2}

    println(reducerdd.collect().toBuffer)
  }


}
