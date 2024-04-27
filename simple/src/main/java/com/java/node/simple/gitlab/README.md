gitlab
======

## 简介

基于 https://github.com/gitlab4j/gitlab4j-api 开源项目实现的 删除分支、新建分支、创建merge request请求、通过merge request请求
操作

## 作用

解决多个微服务上线发版时，每个服务都要操作分支的繁琐重复工作，也能解决人为误操作的问题

## 说明

- [ProjectUtil.java](util%2FProjectUtil.java)
  核心的调用gitlab api工具，实现的功能有：删除分支、创建分支、合并分支、校验分支是否存在
- [PublishOperation.java](operation%2FPublishOperation.java) 发布分支到灰度、生产的实现接口，目前有两种实现
  - [PublishOperation1.java](operation%2FPublishOperation1.java) 发布逻辑：备份原有的线上分支 》 删除旧线上分支 》
    集成分支创建为新的线上分支
  - [PublishOperation2.java](operation%2FPublishOperation2.java) 发布逻辑：集成分支合并到线上分支
- [MergeRequestApiOwn.java](util%2FMergeRequestApiOwn.java) 重写的gitlab4j-api的merge方法，因为我们使用的gitlab的api接口有些不一致，因此重写
- [GitLabUtil.java](util%2FGitLabUtil.java) 执行发布的逻辑
  - host ： 要指定为自己部门gitlab的域名
  - optTypeMap : 标记每个要发布的项目的发布逻辑是什么
- [GitlabTest.java](GitlabTest.java) 执行演示类，需要指定自己的token，位于 http://gitlabhost/profile/account
