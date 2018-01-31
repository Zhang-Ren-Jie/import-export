package timestone.engine.spark.export.data

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import timestone.engine.spark.export.Car.CarRead
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-7-11.
  */
class Get @Inject() (
                      sparkFactory: SparkFactory,
                      carHrandService: CarHrandService
                    ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object Get {

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: Get = in.getInstance( classOf[Get])
    //导出 参数：路径文件，开始时间，结束时间，表名，列族
    c.tsparkFactory.scanSpark("/tmp/market_lables-1","1","1499738970000","index","lables"){
      rdd: RDD[(ImmutableBytesWritable, Result)] =>
        rdd.map{ c.tcarHrandService.RddGet}
    }
  }
}
