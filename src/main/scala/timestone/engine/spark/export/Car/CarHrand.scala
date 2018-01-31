package timestone.engine.spark.export.Car

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by lky on 17-4-11.
  */
class CarHrand  @Inject() (
                            sparkFactory: SparkFactory,
                            carHrandService: CarHrandService
                          ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object CarHrand{

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: CarHrand = in.getInstance( classOf[CarHrand])

    //导入时间
    implicit val times:Long = 1496797774000L
    //导入的列族
    implicit val family:String="homoionym"
    //读取文件:文件名，表名
    c.tsparkFactory.writeSpark("/tmp/homoionym/part-00002","car")
    {rdd: RDD[String]  =>
      rdd.map( _.split(',') ).map{ println;c.tcarHrandService.rddPut}


    }
  }
}
