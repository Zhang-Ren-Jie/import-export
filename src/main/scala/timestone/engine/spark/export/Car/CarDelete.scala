package timestone.engine.spark.export.Car

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-4-17.
  */

class CarDelete  @Inject() (
                           sparkFactory: SparkFactory,
                           carHrandService: CarHrandService
                         ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object CarDelete {

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: CarDelete = in.getInstance( classOf[CarDelete])
    //删除的时间/版本
    implicit val times1:Long = 1496660729000L
    //删除的列族
    implicit val family1:String="car"
    //表名
    c.tsparkFactory.deleteSpark ("car")
    {
      rdd: RDD[(ImmutableBytesWritable, Result)] =>
        rdd.map( c.tcarHrandService.rddDelete )
    }
  }
}