package timestone.engine.spark.export.Url

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-5-9.
  */
class UrlWrite @Inject() (
                           sparkFactory: SparkFactory,
                           carHrandService: CarHrandService
                         ) extends Serializable {

  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService
}

object UrlWrite {
  def main(args: Array[String]): Unit = {
      val in = Guice.createInjector(new Module(){
        override def configure(binder: Binder): Unit = {}
      })

    val c: UrlWrite = in.getInstance( classOf[UrlWrite])
    //导入时间
    implicit val times:Long = 1496650729000L
    //导入的列族
    implicit val family:String="url"
    //读取文件:文件名，表名
    c.tsparkFactory.writeSpark("/home/zrj/桌面/hbase/1.csv","car")
    {rdd: RDD[String] =>
      rdd.map( _.split(',') ).map { println ;c.tcarHrandService.rddUrlPut}
    }



  }
}