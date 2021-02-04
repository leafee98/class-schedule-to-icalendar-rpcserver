# Class Schedule To Icalendar (RPCServer)

借助于 [grpc](https://grpc.io) 实现的搭载 [class-schedule-to-icalendar-core](https://github.com/leafee98/class-schedule-to-icalendar-core) 的 RPC 服务, 将课程表到 ical 文件的过程实现到 RPC 调用

## 接口调用

详细请参见 [proto](/src/main/proto) 下的文件

## 运行

运行包含依赖的 jar 文件即可, 运行时参数如下

```
-l <listen port>       port to listen, default 8047
```
