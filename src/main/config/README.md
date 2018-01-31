### config

#### hbase配置

```$xslt
连接hbase
clientPort="2181" // hbase 端口
quorum="hbase" // 本地配置hosts hbase指定IP
master="hbase:600000" // hbase 端口
carHrand="car" // hbase 指定 表

```

#### spark配置

```$xslt

export="importExport" // spark 名称
master="local" // spark 集群
SCAN_COLUMN_FAMILY="url" // 指定列族
SCAN_TIMERANGE_START="0" // 开始时间
SCAN_TIMERANGE_END="1494320834000" // 结束时间
SCAN_TIMESTAMP="1492230244265" // 指定时间和时间范围不同的是这个时间必须准确


```