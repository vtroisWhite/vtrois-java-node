# log-config

## 基于jdk17+springboot 3.0

## 简介

用于通过各种方式，过滤调整logback的日志输出，并使用`ApplicationListener<EnvironmentChangeEvent>`动态更新过滤配置

## 作用

可通过配置中心设置配置，做到不重发发版也能暂时屏蔽、降级 生产环境不想打印的日志，解决服务器压力、报警等问题

### [LogLevelChange.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Flogback%2Flog_config%2Fconfig%2Fimpl%2FLogLevelChange.java)

设置某个class的log level，比如从info改为off，直接屏蔽此class的日志

```properties
# 配置解释：
# Service1 日志输出级别设置为 OFF，不打印日志，
# Service2 日志输出级别设置为 WARN，不打印INFO以及以下级别的日志，
logback.level.change=[\
  {"className":"com.java.node.logback.log_config.service.Service1","level":"OFF"},\
  {"className":"com.java.node.logback.log_config.service.Service2","level":"WARN"},\
  ]
```

### [LogLevelFilter.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Flogback%2Flog_config%2Fconfig%2Fimpl%2FLogLevelFilter.java)

设置某个class，不打印某个level的日志，比如日志级别为INFO，这种情况下会打印INFO、WARN、ERROR，那么可以设置屏蔽打印ERROR日志

```properties
# 配置解释：
# Service1 不输出ERROR等级的日志，INFO、WARN 正常输出
# Service2 不输出INFO、WARN等级的日志，ERROR 正常输出
logback.level.filter=[\
  {"className":"com.java.node.logback.log_config.service.Service1","filterLevelList":["ERROR"]},\
  {"className":"com.java.node.logback.log_config.service.Service2","filterLevelList":["INFO","WARN"]},\
  ]
```

### [LogAppenderFilter.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Flogback%2Flog_config%2Fconfig%2Fimpl%2FLogAppenderFilter.java)

为日志框架的appender增加filter，过滤不往这个appender里打印日志
比如当前 [logback-spring.xml](src%2Fmain%2Fresources%2Flogback-spring.xml)
中定义了4个appender：STDOUT、INFO、WARN、ERROR，分别往 控制台、info.log、warn.log、error.log 打印日志，那么如下配置可以做到

- Service1 跳过appender：ERROR，不向error.log中打印日志
- Service2 跳过appender：STDOUT、WARN，不向控制台、warn.log中打印日志

```properties
logback.appender.filter=[\
  {"className":"com.java.node.logback.log_config.service.Service1","filterAppenderList":["ERROR"]},\
  {"className":"com.java.node.logback.log_config.service.Service2","filterAppenderList":["STDOUT","WARN"]},\
]
```

### [LogConfigDynamicChangeListener.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Flogback%2Flog_config%2Fconfig%2FLogConfigDynamicChangeListener.java)

配置变化监听器，通过监听`ApplicationListener<EnvironmentChangeEvent>`
事件获取配置是否变更，比如nacos修改了配置文件，spring-cloud刷新配置后就会发出此Event，从而可以及时更新过滤配置。

## 结语

以上几种过滤类型，应该基本能满足过滤特定日志的需求，如果想要精确过滤某一行日志，也可以通过Spel表达式对 日志内容、日志参数、异常信息
进行精确判断，从而达到效果，后续有时间我也会更新此种过滤方式的实现
