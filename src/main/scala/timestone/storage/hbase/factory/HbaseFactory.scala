package timestone.storage.hbase.factory

import com.google.inject.{ImplementedBy, Inject, Singleton}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory, Table}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import timestone.core.conf.factory.ConfFactory


/**
  * Created by lky on 17-4-1.
  */
@ImplementedBy(classOf[HbaseFactoryImp])
trait  HbaseFactory {

  protected var _conn:Connection

  protected var _tabs:Map[String,Table]

  def getConn(): Connection
  def getTable(table:String): Table
  def getConfig(): Configuration


}

@Singleton
class HbaseFactoryImp  @Inject()()   extends  HbaseFactory with Serializable{

  override var _conn: Connection = null

  override protected var _tabs: Map[String, Table] = Map.empty


  // 写入数据
  override def getConn(): Connection = {
    synchronized{
      if( _conn == null ) {
        //Connection 的创建是个重量级的工作，线程安全，是操作hbase的入口
        val conf = getConfig()
        _conn = ConnectionFactory.createConnection(conf)
      }
    }
    _conn
  }

  override def getTable(tablename: String): Table = {
    if(_tabs.isDefinedAt(tablename) ) {
      _tabs += ( tablename -> getConn.getTable( TableName.valueOf(tablename)) )
    }
    _tabs.get(tablename).get
  }

  override def getConfig(): Configuration = {
    val conf = HBaseConfiguration.create()
    val tablename = "market"
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    conf.set("hbase.zookeeper.quorum", "ubuntulenovo3")
    conf.set("hbase.master","ubuntulenovo3:60000")
//    conf.set("hbase.zookeeper.quorum", "hbase")
//    conf.set("hbase.master","hbase:60000")
    conf.set(TableInputFormat.INPUT_TABLE, tablename)
    conf
  }

}
