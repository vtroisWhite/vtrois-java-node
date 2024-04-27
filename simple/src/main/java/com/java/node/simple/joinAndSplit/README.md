joinAndSplit
============

## 简介

通过分隔符拼接，在一个字符串内，存下多个字段内容

## 作用

由于数据库中会留下几个通用的扩展字段，用于保存定制化的扩展信息，当有些产品扩展信息过多时，会导致扩展字段不够用，因此要在一个字段内保存多个扩展信息，
并且做到只需要get、set，比如保存 需要加密的字符串、小数金额， 无需每次都进行转换，直接get set即可

## 说明

[ExtEntity.java](custom%2FExtEntity.java)
数据库实体类，存在 ext1、ext2 两个通用扩展字段

[Custom1ExtDataSaveConfig.java](custom%2FCustom1ExtDataSaveConfig.java)
定制扩展存储1：在ext1字段中依次存下 手机号（加密）、姓名、年龄、余额、地址，存储效果为："{手机号},{姓名},{年龄},{地址}"

[JoinSplitTest.java](JoinSplitTest.java) 为使用示例，运行结果

```
在0号位存下手机号密文:MTMzMTIzNDU2Nzg=
在3号位存下金额:MTMzMTIzNDU2Nzg=,,,1.25
在4号位存下地址:MTMzMTIzNDU2Nzg=,,,1.25,省\,市1\\,区1\\,\,县2\\\,\,\,镇村街户
在2号位存下年龄:MTMzMTIzNDU2Nzg=,,17,1.25,省\,市1\\,区1\\,\,县2\\\,\,\,镇村街户
获取手机号:13312345678
获取姓名:null
获取年龄:17
获取金额:1.25
获取地址:省,市1\,区1\,,县2\\,,,镇村街户
```
