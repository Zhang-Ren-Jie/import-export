//package timestone.core.conf.factory
//
//import java.io.File
//
//import com.google.inject.ImplementedBy
//import com.typesafe.config.{Config, ConfigFactory}
//
///**
//  * Created by lky on 17-4-11.
//  */
//@ImplementedBy(classOf[ConfFactoryImp])
//trait ConfFactory {
//
//  var _map:Map[String,Config] = Map.empty;
//
//  val keyThis = "$this"
//
//
//  def getConf()
//
//  def get(key:String): String
//
//}
//
//class ConfFactoryImp extends  ConfFactory with Serializable{
//
//  override def getConf(): Unit = {
//      if(!_map.isDefinedAt(keyThis))
//      synchronized{
//          _map += ( keyThis ->
//            ConfigFactory.parseFile(
//              new File(
//                Thread.currentThread().getContextClassLoader.getResource("./application.conf").getPath))
//          )
//      }
//  }
//
//  override def get(key: String): String = {
//    getConf
//    _map.get(keyThis).get.getString(key)
//  }
//}
