package timestone.engine.spark.export.data

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by lky on 17-4-11.
  */
class Write  @Inject()(
                            sparkFactory: SparkFactory,
                            carHrandService: CarHrandService
                          ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}
object Write{

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: Write = in.getInstance( classOf[Write])

    //导入时间
    implicit val times:Long = 1512540303300L
    //导入的列族
    //implicit val family:String="data"
    //读取文件:文件名，表名  // car bank room industry
    c.tsparkFactory.WriteSpark("/data/porsche/10000/zhaohuini_cs.txt","porsche")
    {rdd: RDD[String]  =>
      rdd.map( _.split('|') ).map{ println;c.tcarHrandService.RddPutJson}
    }

  }
}

