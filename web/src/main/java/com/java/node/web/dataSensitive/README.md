dataSensitive
=============

## 简介

通过注解的方式，重写jackson的序列化，对api接口响应数据脱敏

## 作用

解决业务需求要对敏感字段脱敏的需求

## 说明

- [SensitiveTypeEnum.java](SensitiveTypeEnum.java) 脱敏的字段类型，以及脱敏的Function
- [Sensitive.java](Sensitive.java) 自定义的脱敏注解
- [SensitiveSerialize.java](SensitiveSerialize.java) 脱敏注解的功能实现

请求示例：[dataSensitive.http](dataSensitive.http)

```
脱敏前：
{
    "uid": 123,
    "userName": "云天明",
    "registerPhone": "13312345678",
    "bankCardNum": "6212260001007512123",
    "cid": "14013320000101002X"
}
脱敏后：
{
    "uid": 123,
    "userName": "云**",
    "registerPhone": "133****5678",
    "bankCardNum": "6212 **** **** *** 2123",
    "cid": "1401**********002X"
}
```

