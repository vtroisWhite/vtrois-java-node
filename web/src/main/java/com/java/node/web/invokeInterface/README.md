invokeInterface
==========

## 简介

基于反射，调用内部方法

## 作用

服务中有些内部方法，可能没有暴露到接口中，或者无法单独执行此方法，此功能的意义便是利用反射单独调用内部方法

## 说明

如[InvokeInterface.http](InvokeInterface.http)所示，
展示了方法中有单个参数、多个参数、普通String参数、自定义对象参数，的情况下，如何传参调用。并触发[InvokeInterfaceController.java](InvokeInterfaceController.java)
方法的task1、task2、task3、task4方法