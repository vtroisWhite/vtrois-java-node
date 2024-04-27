eventHandle
===========

## 简介

当消费者处理消息时，一个事件可能要处理多个业务逻辑，这里展示如何实现多个处理策略处理同一个事件，以及如何记录执行结果与汇总

## 作用

如简介所示，多策略处理，与处理完成后的结果统计

## 说明

### 参数

- [EventEnum.java](dto%2FEventEnum.java) [SubEventEnum.java](dto%2FSubEventEnum.java) 定义的一级、二级事件
- [EventMsg.java](dto%2FEventMsg.java) 事件生产者生产的事件内容
- [EventParam.java](dto%2FEventParam.java) 事件消费时的参数
- [HandleResult.java](dto%2FHandleResult.java) 消费结果

### 处理器

- [HandlerEnum.java](handlerSerivce%2FHandlerEnum.java)  有哪些处理器
- [HandlerService.java](handlerSerivce%2FHandlerService.java) 处理器接口

  - [impl](handlerSerivce%2Fimpl) 具体的处理器
- [EventHandleTest.java](EventHandleTest.java) 使用方法示例，运行日志：

```
23:31:26.216 [main] INFO com.java.node.simple.eventHandle.EventHandleTest - 埋点处理,成功:[],缺少数据:[发送营销短信, 同步用户信息],无需处理:[],失败:[],耗时:0ms
23:31:26.216 [main] INFO com.java.node.simple.eventHandle.EventHandleTest - 埋点处理,未成功,缺少数据,处理器:记录活跃记录,msg:缺少a
23:31:26.216 [main] INFO com.java.node.simple.eventHandle.EventHandleTest - 埋点处理,成功:[],缺少数据:[记录活跃记录],无需处理:[],失败:[],耗时:0ms
23:31:26.219 [main] INFO com.java.node.simple.eventHandle.EventHandleTest - 埋点处理,成功:[发送营销短信],缺少数据:[],无需处理:[],失败:[],耗时:0ms
23:31:26.219 [main] INFO com.java.node.simple.eventHandle.EventHandleTest - 埋点处理,成功:[记录活跃记录],缺少数据:[],无需处理:[],失败:[],耗时:0ms
23:31:26.634 [main] WARN com.java.node.simple.eventHandle.EventHandleTest - 事件处理,已运行591毫秒,处理情况:【REGISTER:2013条,LOGIN:2039条,ACTIVE:1991条,BIND_BANK_CARD:1962条,VERIFY_ID_CARD:1995条,】,在过去的0分钟处理情况【REGISTER:2013条,LOGIN:2039条,ACTIVE:1991条,BIND_BANK_CARD:1962条,VERIFY_ID_CARD:1995条,】,子事件总处理结果：【{"LOGIN":{"APP":1022,"H5":1017},"ACTIVE":{"APP":978,"H5":1013},"VERIFY_ID_CARD":{"BACK":958,"FRONT":1037},"REGISTER":{"APP":973,"H5":1040},"BIND_BANK_CARD":{"NONE":1962}}】
```
