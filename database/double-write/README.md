double-write (数据双写)
=======================

## 简介

使用mybatis拦截器，实现多数据源数据双写

## 作用

当做数据库迁移时，需要涉及到数据库双写，除了 MQ、binlog同步外，使用mybatis的拦截器也是一种实现方案。

相较于网上的其他mybatis拦截器双写demo，此demo实现了解决使用声明式事务时：事务回滚、新建子事务，会导致的数据一致性问题.

## 大致的处理流程

- 执行了 insert、update、delete 操作，拦截器获取到执行的sql，并解析成完整的填充了占位符的sql
- 将sql暂存于ThreadLocal，当发生事务提交时，拿出待执行的sql列表，提交到线程池中
- 线程池异步执行sql，结束。

## 配置说明：

#### [test.http](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2Ftest.http)

http接口调用类

#### [schema.sql](src%2Fmain%2Fresources%2Fschema.sql)

双写demo要读写的表，需要在db1、db2中提前创建完成

#### [SessionFactoryConfig.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2Fdatasource%2FSessionFactoryConfig.java)

创建事务管理器bean时，通过设置自定义的 `TransactionSynchronizationManager.registerSynchronization(new MyTransactionSynchronization());`
解决 @Transactional 的事务一致性问题。

```java
            @Override
            protected void prepareSynchronization(DefaultTransactionStatus status, TransactionDefinition definition) {
                super.prepareSynchronization(status, definition);
                if (status.isNewSynchronization()) {
                    DoubleWriteTransactionUtil.openTransaction(dataSource);
                }
            }
```

#### [DoubleWriteTransactionUtil.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FDoubleWriteTransactionUtil.java) [MyTransactionSynchronization.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FMyTransactionSynchronization.java)

监听声明式事务的 提交、回滚、挂起 操作，并做出相应的调整

- 开启事务(DoubleWriteTransactionUtil.openTransaction)：标注当前已经进入了声明式事务模式，只有事务关闭时才执行双写sql
- 挂起事务(MyTransactionSynchronization.suspend)：标注开启了子事务，单独记录之后的新sql，并当发生事务提交时，仅处理子事务的sql
- 挂起事务(MyTransactionSynchronization.resume)：标注子事务关闭，恢复到主事务，新sql记录到主事务中
- 事务关闭(MyTransactionSynchronization.afterCompletion)：标注当前事务结束，判断事务状态是回滚、还是提交，如果是回滚则放弃双写，如果是提交，则执行双写

#### [mybatis-config.xml](src%2Fmain%2Fresources%2Fmybatis-config.xml)

```xml
<!--配置拦截器-->
<plugins>
    <plugin interceptor="com.java.node.database.double_write.doubleWrite.MyInterceptor"/>
</plugins>
```

#### [MyInterceptor.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FMyInterceptor.java) 核心的mybatis拦截器

-
将执行的update类型的sql，填充占位符为完整sql，使用 [CCJSqlParserFunction.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FCCJSqlParserFunction.java)
解析出sql的表名称，判断此表是否要进行双写
-
将sql使用 [DoubleWriteUtil.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FDoubleWriteUtil.java)
提交，并判断当前是否处于声明式事务，是的话则暂存threadLocal，否的话直接执行
-
声明式事务结束时，[DoubleWriteTransactionUtil.java](src%2Fmain%2Fjava%2Fcom%2Fjava%2Fnode%2Fdatabase%2Fdouble_write%2FdoubleWrite%2FDoubleWriteTransactionUtil.java)
将要执行的sql提交到线程池，并执行
