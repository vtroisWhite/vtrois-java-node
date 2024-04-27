paramSpelGetter
==========

## 简介

基于SpEL表达式，动态获取入参的数据

## 作用

通过SpEL表达式灵活的获取参数数据，对调用方进行一系列规则限制

## 说明

[ParamSpelGetterController.java](ParamSpelGetterController.java) 为SpEL表达式的使用示例

[SpelGetterAspect.java](SpelGetterAspect.java) 为解析示例

以下为运行日志

入参：

```
key=k1
param=
{
  "id": "1",
  "name": "name1",
  "next": {
    "id": "12",
    "name": "name12"
  },
  "nextList": [
    {
      "id": "111",
      "name": "nextList1"
    },
    {
      "id": "112",
      "name": "nextList2"
    }
  ]
}
```

表达式：

```
    @SpelGetterAnnotation(
            countLimitExpress = {
                    "${server.port:1234}",
                    "${server.ppp:1234}",
            },
            spelExpressions = {
                    "#key",
                    "#param",
                    "#param.id",
                    "#param.next.name",
                    "#param.nextList[1].name",
                    "'nextList中第一个数组的id为:'+(#param.nextList[0].id)",
            })
```

获取结果：

```
c.j.n.w.p.SpelGetterAspect               : countLimitExpress,key:${server.port:1234},value:8081
c.j.n.w.p.SpelGetterAspect               : countLimitExpress,key:${server.ppp:1234},value:1234
c.j.n.w.p.SpelGetterAspect               : 切面：{"param":{"id":1,"name":"name1","next":{"id":12,"name":"name12"},"nextList":[{"id":111,"name":"nextList1"},{"id":112,"name":"nextList2"}]},"key":"k1"}
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:k1
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:ParamSpelGetterParam(id=1, name=name1, next=ParamSpelGetterParam(id=12, name=name12, next=null, nextList=null), nextList=[ParamSpelGetterParam(id=111, name=nextList1, next=null, nextList=null), ParamSpelGetterParam(id=112, name=nextList2, next=null, nextList=null)])
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:1
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:name12
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:nextList2
c.j.n.w.p.SpelGetterAspect               : spel表达式解析:nextList中第一个数组的id为:111
c.j.n.w.p.ParamSpelGetterController      : 入参,key:k1,param:{"id":1,"name":"name1","next":{"id":12,"name":"name12"},"nextList":[{"id":111,"name":"nextList1"},{"id":112,"name":"nextList2"}]}
```