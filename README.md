* [src目录结构](./src/README.md)
* [工程功能开发](./src/main/scala/README.md)

## 实现数据在存储介质中相互的导入导出

#### Hbase[schema格式]

```json

{"namespace": "example.avro",
 "type": "record",
 "name": "Test",
 "fields": [
     {"name": "C1", "type": "string"},
     {"name": "C3", "type": "string"},
     {"name": "C4", "type": "string"},
     {"name": "C5", "type": "string"},
     {"name": "C6", "type": "string"},
     {"name": "C7", "type": "string"},
     {"name": "C8", "type": "string"}
 ]
}

```


#### Hbase导入到出avro-file

* 命令

#### Hbase导入导出avro-parquet

* 命令


#### 数据库的操作

* [增加](./src/readme.md)
* [删除](./src/readme.md)
* [修改](./src/readme.md)
* [查询](./src/readme.md)

#### 参考工程:

* https://github.com/tmalaska/HBase-ToHDFS
* http://avro.apache.org/docs/current/gettingstartedjava.html
