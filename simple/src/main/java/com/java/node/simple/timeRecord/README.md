timeRecord
==========

## 简介

记录一个方法的执行耗时

## 作用

可以记录个别方法调用的耗时情况，并保存到数据库，并且改动起来比较简单

```
原来的调用：
Object result1 = doSomething1(param);
Object result2 = doSomething2(param);

改造后的调用：
TimeRecord record = TimeRecord.initRecord();
Object result1 = record.recordCallable("方法1", ()->doSomething1(param)).call();
Object result2 = record.recordCallable("方法1", ()->doSomething2(param)).call();
```

[TimeRecordTest.java](TimeRecordTest.java) 为使用示例，运行结果：记录了每个方法的执行的起始、结束时间

```json
{
  "startTs": 1713716433169,
  "costTs": 1623,
  "nodeTimeMap": {
    "callable2": {
      "cost": 403,
      "endTime": 815,
      "startTime": 412
    },
    "callable1": {
      "cost": 408,
      "endTime": 412,
      "startTime": 4
    },
    "supplier1": {
      "cost": 404,
      "endTime": 1220,
      "startTime": 816
    },
    "runnable1": {
      "cost": 402,
      "endTime": 1623,
      "startTime": 1221
    }
  }
}
```
