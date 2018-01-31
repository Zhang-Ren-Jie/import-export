package timestone.model.yaml

import com.google.inject.{Binder, Guice, Inject, Module}
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.rdd.RDD
import timestone.engine.spark.factory.SparkFactory
import timestone.storage.hbase.dao.CarHrandService

/**
  * Created by zrj on 17-5-10.
  */
class Car_to_Yaml  @Inject() (
                           sparkFactory: SparkFactory,
                           carHrandService: CarHrandService
                         ) extends Serializable {
  val tsparkFactory: SparkFactory = sparkFactory
  val tcarHrandService: CarHrandService = carHrandService

}

object Car_to_Yaml {

  def main(args: Array[String]): Unit = {
    val in = Guice.createInjector(new Module(){
      override def configure(binder: Binder): Unit = {}
    })
    val c: Car_to_Yaml = in.getInstance( classOf[Car_to_Yaml])
    //导出 参数：路径文件，开始时间，结束时间，表名，列族
    c.tsparkFactory.scanSpark("/tmp/yaml_1-3","1","1496650739000","car","url"){
      rdd: RDD[(ImmutableBytesWritable, Result)] =>
        rdd.flatMap{ c.tcarHrandService.rddGetYaml}
    }
  }
}