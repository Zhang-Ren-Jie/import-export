package timestone.engine.spark.export.data

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

class WritePro @Inject()(
                          sparkFactory: SparkFactory,
                          carHrandService: CarHrandService
                        ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object WritePro {
  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: WritePro = in.getInstance( classOf[WritePro])

    //导入时间
    implicit val times:Long = 1515485844777L

    c.tsparkFactory.writeSparkPro("/data/porsche/50000/all.txt","porsche")
    {rdd: RDD[String]  =>
      rdd.map( _.split('|') ).map{ println;c.tcarHrandService.RddPutPro}
    }
  }
}