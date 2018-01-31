package timestone.engine.spark.export.Car

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-4-15.
  */

class CarRead  @Inject() (
                            sparkFactory: SparkFactory,
                            carHrandService: CarHrandService
                          ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object CarRead {

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: CarRead = in.getInstance( classOf[CarRead])
    //导出 参数：路径文件，开始时间，结束时间，表名，列族
    c.tsparkFactory.scanSpark("/tmp/market_username-5","1","1499738970000","market","homoionym"){
      rdd: RDD[(ImmutableBytesWritable, Result)] =>
        rdd.map{ c.tcarHrandService.rddGet}
    }
  }
}
