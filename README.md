# vtrois-java-node

## 前言

本项目记录了个人在工作中，所写过的一些自认为不错的Java代码

如果对代码中哪些部分有疑问，或者认为哪些功能实现有更优雅的实现方式，欢迎提出issues交流讨论

## 各个模块说明

### [core](core)

核心模块，存放各个项目都要使用的工具类、常量。

### [simple](simple)

一些通过main方法直接运行示例的简单代码

- [bitOperation](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2FbitOperation)
  位运算（与、或、非、异或、左移、右移）的使用示例
- [emojiFilter](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2FemojiFilter)
  基于utf8编码下，通过byte信息获取字符的byte长度，并以此来过滤长度大于3的特殊emoji字符
- [eventHandle](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2FeventHandle)
  多策略消费一个事件时，如何更好的记录每个策略的执行结果、以及一段时间内总的消费结果
- [function](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2Ffunction)
  java.util.function 包下类的使用心得
- [gitlab](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2Fgitlab)
  基于 https://github.com/gitlab4j/gitlab4j-api 开源项目实现的 删除分支、新建分支、创建merge request请求、通过merge
  request请求 操作
- [joinAndSplit](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2FjoinAndSplit)
  如何更优雅在数据库一个字符串字段内，存储多个不同类型数据
- [sync](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2Fsync)
  synchronize 对象锁、类锁
- [timeRecord](simple%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fsimple%2FtimeRecord)
  如何更优雅的记录一段方法的执行耗时

### [web](web)

一些基于web容器实现的功能

- [dataSensitive](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FdataSensitive)
  接口响应值的脱敏
- [gracefulShutdown](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FgracefulShutdown)
  优雅停机的实现
- [invokeInterface](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FinvokeInterface)
  利用反射调用内部接口
- [paramConverter](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FparamConverter)
  请求数据的类型转换
- [paramSpelGetter](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FparamSpelGetter)
  基于spel表达式对请求数据的获取
- [requestHandle](web%2Fsrc%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fweb%2FrequestHandle)
  基于filter、ResponseBodyAdvice，对请求数据、响应数据进行处理

### [database](database)

数据库相关

- [double-write](database%2Fdouble-write%2F)：使用mybatis拦截器所实现的多数据库双写实现
- [multi-datasource](database%2Fmulti-datasource%2F)：如何配置不同mapper包下的sql连接不同的数据源，从而读写不同的数据库
- [mybatis-plus-v3x](database%2Fmybatis-plus-v3x%2F)：普通mybatis项目如何使用mybatis-plus框架，并自动生成dao层类，以及常用的crud操作
- [tk-mybatis-v4x](database%2Ftk-mybatis-v4x%2F)：普通mybatis项目如何使用tk-mybatis框架，并自动生成dao层类，以及常用的crud操作

### [logback](logback)

logback日志框架相关

- [log-config](logback%2Flog-config) 动态调整配置，从而过滤服务中打印的特定日志

### [bug-review](bug-review)

一些奇怪的bug

- [bug-scheduled-aop](bug-review%2Fbug-scheduled-aop%2F) `@Scheduled` 使用中发现的一个循环依赖情况下aop的bug，定位到了bug产生的原因，但还没有解决修复

## 后言

最后，如果本项目中对你的编程提供了一些帮助，欢迎留下一颗小小的star，Thanks~ 😊
