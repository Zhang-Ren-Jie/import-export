
hbase 启动
```
docker run -it -v "$(pwd)/conf/hbase/:/hbase/conf" \
  --hostname hbase \
  -v /data/docker-data/hbase:/tmp/hbase-root \
  103.31.151.220:5000/harisekhon/hbase:1.2
```


hue 启动
```
docker run --dns 172.17.0.2 -it  \
  -v "$(pwd)/conf/hue/pseudo-distributed.ini:/hue/desktop/conf/pseudo-distributed.ini" \
  103.31.151.220:5000/gethue/hue
```

-v "$(pwd)/conf/hbase/:/etc/hbase/conf" \

