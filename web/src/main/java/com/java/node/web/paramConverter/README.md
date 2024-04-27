paramConverter
==========

## 简介

将前端传过来的数据进行格式转换，解决后端类型不匹配无法反序例化spring报错的问题

## 作用

比如前端传了个long类型时间戳，但是我们通过java.util.Date接收，就会因为类型不匹配无法接收，那么就可以通过转换器解决这类问题。

## 说明

- [MyWebAppConfigurer.java](config%2FMyWebAppConfigurer.java) 添加自定义的转换器

### 将long类型时间戳，转成Date类型

- [TimestampToDate.java](config%2FTimestampToDate.java) 注解
- [TimestampToDateFactory.java](config%2FTimestampToDateFactory.java) 功能实现

### 解决form-data无法直接接收json对象的问题

- [ObjectFormat.java](config%2FObjectFormat.java) 注解
- [ObejctFormatFactory.java](config%2FObejctFormatFactory.java) 功能实现

测试用例：[ParamConverter.http](ParamConverter.http)