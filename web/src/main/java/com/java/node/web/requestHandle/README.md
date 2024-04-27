requestHandle
==========

## 简介

通过Filter过滤器，统一对请求数据做处理。ResponseBodyAdvice对ResponseBody响应数据做统一处理

## 作用

- 对接口请求进行统一的验签、解密，对响应值做统一的加密加签
- 对涉及敏感数据的接口统一进行加密、脱敏操作
- 获取请求参数，进行非法数据的过滤修改

## 说明

- 启动类上新增`@ServletComponentScan`注解
- [EncryptFilter.java](EncryptFilter.java)
  过滤器，获取处理http请求的内容。目前限制了必需传sign值，否则算签名不通过。并对sign修改增加"pass="字符
- [ResponseBodyAdvice.java](ResponseBodyAdvice.java)
  对ResponseBody做处理，目前对响应类型为RequestHandleResponse类型的数据，将sign字段的值增加"sign："字符
- 测试用例位于 [RequestHandle.http](RequestHandle.http)