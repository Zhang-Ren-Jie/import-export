package timestone.engine.spark.export.data

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-7-20.
  */
class LableCount @Inject() (
                             sparkFactory: SparkFactory,
                             carHrandService: CarHrandService
                           ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object LableCount {

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: LableCount = in.getInstance( classOf[LableCount])
    //导出 参数：路径文件，开始时间，结束时间，表名，列族
    c.tsparkFactory.countSpark("/home/zrj/文档/index_count/index_count-medical-1.csv","1","1499738970000","index","lables:medical"){
      rdd: RDD[(ImmutableBytesWritable, Result)] =>
        rdd.map{ c.tcarHrandService.rddGet}
    }
  }
}
