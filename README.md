# 舅服你商城系统介绍

把项目多余功能全删除以后一个脚手架。

## 介绍
Jfinal + element-ui 最纯的后台脚手架，没有任何多余组件，有权限、用户、角色管理，有基础的文件上传，系统可以采取正常的梭哈写，也支持任务异步。



## 项目结构
``` lua
jfinal-mall
├── jfinal-mall-api -- 后端接口
├── jfinal-mall-admin -- 后台系统
├── qw_original_v1 -- 数据库脚本
```



## 项目特色

- 最好用的私活神器，拿起来直接撸业务
- 最简单直接的部署方式，一个Tomcat、一个MySQL齐活
- 缓存使用ehcache，不用部署Redis
- 权限框架使用Shiro，逻辑上权限等于目录
- 使用Element-UI

##  你问我怎么用

- 导入数据库
- 修改Java项目中resources/config.properties 中的数据库连接信息
- maven  jetty:run，至此后端项目启动
- 前端项目 npm install
- npm run dev 至此，后端项目启动

## 好吧 张这个样子

![image](https://github.com/qwzhang01/img-storage/blob/master/Snipaste_2020-04-02_18-03-46.png)

