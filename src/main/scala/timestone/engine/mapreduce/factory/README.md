### spark 配置

#### MapReduceFactory

- getJobConfWrite // spark 写入的配置文件，配置了 表名
- getJobConfRead  // spark 读取的配置文件，配置了 表名/开始时间-结束时间
- getJobConfDel // spark 删除的配置文件，配置了 表名
- getJobConfScan // spark 扫描的配置文件，配置了 表名/开始时间-结束时间/列族
