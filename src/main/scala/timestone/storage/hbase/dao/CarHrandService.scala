package timestone.storage.hbase.dao

import java.util

import com.google.inject.{ImplementedBy, Inject, Singleton}
import org.apache.hadoop.hbase.client.{Delete, Put, Result, Scan}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.util.Bytes
import timestone.storage.hbase.factory.HbaseFactory
import org.apache.hadoop.hbase.CellUtil
import timestone.engine.spark.factory.SparkFactory

import scala.collection.immutable.Iterable

/**
  * Created by lky on 17-4-1.
  * 汽车体系库管理
  *   整体：导入、导出
  *   基于版本：删除、覆盖、添加
  */
@ImplementedBy(classOf[CarHrandServiceImp])
trait CarHrandService{
  //导入 不指定列族
  def RddPut(arr:Array[String]) (implicit times:Long ): (ImmutableBytesWritable, Put)
  def RddPutJson(arr:Array[String]) (implicit times:Long ): (ImmutableBytesWritable, Put)

  //导入新格式pro
  def RddPutPro(arr:Array[String]) (implicit times:Long ): (ImmutableBytesWritable, Put)

  // 指定列族的导入
  def rddPut(arr:Array[String]) (implicit family:String,times:Long ): (ImmutableBytesWritable, Put)
  //导出
  def rddGet(res:(ImmutableBytesWritable, Result)):String
  // 获取 username 列 以供 形成索引表
  def rddGetName(res:(ImmutableBytesWritable, Result)):String
  // 导出标签
  def RddGet(res:(ImmutableBytesWritable, Result)):String

  //删除
  def rddDelete(x:(ImmutableBytesWritable,Result))(implicit family:String,times:Long ):(ImmutableBytesWritable, Delete)

  //url导入
  def rddUrlPut(arr:Array[String])(implicit family:String,times:Long ): (ImmutableBytesWritable, Put)

  //car to yaml
  def rddGetYaml(res:(ImmutableBytesWritable, Result)): Iterable[String]

  //hbase to csv
  def rddGetCsv(res:(ImmutableBytesWritable, Result)):String
}

@Singleton
class CarHrandServiceImp @Inject()(hbaseFactory:HbaseFactory,sparkFactory:SparkFactory)  extends  CarHrandService with Serializable {

  override def RddPut(arr:Array[String]) (implicit times:Long = 1499408484000L): (ImmutableBytesWritable, Put) = {
    var put:Put = null
    var n = 1
    try{
      put = new Put(Bytes.toBytes(arr(0)))
      put.addColumn(Bytes.toBytes(arr(1)),Bytes.toBytes(arr(2)),times,Bytes.toBytes(""))
    }catch{
      case (e:Exception) =>
        n+1
        put = new Put(Bytes.toBytes("1-1-1-1-1-1-1-1"+n))
        put.addColumn(Bytes.toBytes("data"),Bytes.toBytes("yichang"),times,Bytes.toBytes("1"))
    }

    (new ImmutableBytesWritable,put)
  }

  override def RddPutJson(arr:Array[String]) (implicit times:Long = 1499408484000L): (ImmutableBytesWritable, Put) = {
    var put:Put = null
    var n = 1
    try{          //电话，当前时间，文本，姓名，职业，行业，爬虫时间，城市
      put = new Put(Bytes.toBytes(arr(0)))
      put.addColumn(Bytes.toBytes(arr(1)),Bytes.toBytes(arr(2).toString),times,Bytes.toBytes(arr(3)))
    }catch{
      case (e:Exception) =>
        n+1
        put = new Put(Bytes.toBytes("1-1-1-1-1-1-1-1"+n))
        put.addColumn(Bytes.toBytes("identification"),Bytes.toBytes("yichang"),times,Bytes.toBytes("1"))
    }

    (new ImmutableBytesWritable,put)
  }


  //导入新格式
  override def RddPutPro(arr:Array[String]) (implicit times:Long = 1499408484000L): (ImmutableBytesWritable, Put) = {
    var put:Put = null
    var n = 1
    try{          //电话，当前时间，文本，姓名，职业，行业，爬虫时间，城市
      put = new Put(Bytes.toBytes(arr(0)))
      put.addColumn(Bytes.toBytes(arr(1)),Bytes.toBytes(arr(2).toString),times,Bytes.toBytes(arr(3)))
    }catch{
      case (e:Exception) =>
        n+=1
        put = new Put(Bytes.toBytes("1-1-1-1-1-1-1-1"+n))
        put.addColumn(Bytes.toBytes("master_data"),Bytes.toBytes("yichang"),times,Bytes.toBytes("1"))
    }

    (new ImmutableBytesWritable,put)
  }


  override def rddPut(arr:Array[String]) (implicit family:String="homoionym",times:Long = 1496650719000L): (ImmutableBytesWritable, Put) = {
    val put = new Put(Bytes.toBytes(arr(0).toString))
    //导入近意词
    put.addColumn(Bytes.toBytes(family),Bytes.toBytes(arr(1).toString),times,Bytes.toBytes(" "))
    (new ImmutableBytesWritable,put)
  }

  override def rddDelete(x:(ImmutableBytesWritable, Result))
                        (implicit family:String="homoionym",times:Long = 1496650719000L) :(ImmutableBytesWritable, Delete) = {

    val key = Bytes.toString(x._2.getRow)
    //Delete操作
    val delete = new Delete(Bytes.toBytes(key))
    //删除 指定指定列族的时间戳版本
    delete.deleteFamilyVersion(Bytes.toBytes(family), times)
    (new ImmutableBytesWritable,delete)

  }


//待 改成导出默认格式
  override   def rddGet(res:(ImmutableBytesWritable, Result)):String = {

    val result = res._2.asInstanceOf[Result]
    //result.rawCells().mkString(",")
      //获取行键
    result.rawCells().map{
      cell =>
        //取出 Rowkey Column拼成csv文件
        val Rowkey = Bytes.toString(result.getRow())
        val Family = Bytes.toString(CellUtil.cloneFamily(cell))
        val Column = Bytes.toString(CellUtil.cloneQualifier(cell))
        val Value = Bytes.toString(CellUtil.cloneValue(cell))
        val Time = cell.getTimestamp()
        Rowkey//+","+Family+","+Column+","+Time+","+Value

    }.mkString("\n")
  }

  // 获取 username 列 以供 形成索引表
  override   def rddGetName(res:(ImmutableBytesWritable, Result)):String = {

    val result = res._2.asInstanceOf[Result]
    //获取行键
    result.rawCells().map{
      cell =>
        //取出 Rowkey Column拼成csv文件
        val Rowkey = Bytes.toString(result.getRow())
        val Family = Bytes.toString(CellUtil.cloneFamily(cell))
        val Column = Bytes.toString(CellUtil.cloneQualifier(cell))
        val Value = Bytes.toString(CellUtil.cloneValue(cell))
        val Time = cell.getTimestamp()

        // username 索引表结构
        if ( Value != "\"\""){
          "4-"+Value.replaceAll("\"","").replaceAll(" ","")+"-"+Rowkey+",lables,username"
        }
        // contact 索引表 构建
//        if ( Value != "\"\""){
//          "5-"+Value.replaceAll("\"","")+"-"+Rowkey+",lables,contact"
//        }else { "no" }
    }.mkString("\n").replaceAll("no\n","")
  }



  override   def RddGet(res:(ImmutableBytesWritable, Result)):String = {
    val result = res._2.asInstanceOf[Result]
    //获取行键
    result.rawCells().map{
      cell =>
        //取出 Rowkey Column拼成csv文件
        val Rowkey = Bytes.toString(result.getRow())
        val Family = Bytes.toString(CellUtil.cloneFamily(cell))
        val Column = Bytes.toString(CellUtil.cloneQualifier(cell))
        val Value = Bytes.toString(CellUtil.cloneValue(cell))
        val Time = cell.getTimestamp()
//println("value------------------------------------"+Value)
        val arr = Value.split("-")
//        println("arr-----------"+arr)
        val labs = arr(1).split(":")
//        println("labs----------"+labs)
        val obj = (1 to labs.size).collect{
          case (n:Int)=>
            labs.combinations(n).toList.collect{ case (x:Array[String])=>
              List(arr(0),x.toList.mkString(":"),arr(2)).mkString("-")
            }
        }.flatten.toList
        val lab =obj.map{
          a =>
          a+"-"+Rowkey+","+Family+","+Column+","
        }.mkString("\n")
          lab
    }.mkString("\n")
    }



//url  写入
  override def rddUrlPut(arr:Array[String])(implicit family:String="url",times:Long = 1496650719000L): (ImmutableBytesWritable, Put) = {
    val put = new Put(Bytes.toBytes(arr(0).toString))
    //导入url
    put.addColumn(Bytes.toBytes(family),Bytes.toBytes(arr(1).toString),times,Bytes.toBytes(arr(2).replaceAll("\\.\\|","    ").toString))
    (new ImmutableBytesWritable,put)
  }

  //car to yaml
  override   def rddGetYaml(res:(ImmutableBytesWritable, Result)): Iterable[String] = {

    val result = res._2.asInstanceOf[Result]

  result.rawCells().map{
    cell =>
      val Rowkey = Bytes.toString(result.getRow())
      val Family = Bytes.toString(CellUtil.cloneFamily(cell))
      val Column = Bytes.toString(CellUtil.cloneQualifier(cell))
      val Value = Bytes.toString(CellUtil.cloneValue(cell))
      val Time = cell.getTimestamp()

      (Rowkey,Column,Value)

  }.groupBy(x => x._2).map{
    case  (col:String,codes:Array[(String,String,String)]) =>
       "  "+col+" : "+ codes.toList.map{a=> s""" "${a._1}":"${a._3}" """}.mkString("{",",","}")

  }

  }


  override   def rddGetCsv(res:(ImmutableBytesWritable, Result)):String = {

    val result = res._2.asInstanceOf[Result]

    //获取行键
    result.rawCells().map{
      cell =>
        //取出 Rowkey Familiy拼成csv文件
        val Rowkey = Bytes.toString(result.getRow())
        val Family = Bytes.toString(CellUtil.cloneFamily(cell))
        val Column = Bytes.toString(CellUtil.cloneQualifier(cell))
        val Value = Bytes.toString(CellUtil.cloneValue(cell))
        val Time = cell.getTimestamp()
        List(Rowkey,Column,Value)

    }.flatten.mkString(",")
  }



}





