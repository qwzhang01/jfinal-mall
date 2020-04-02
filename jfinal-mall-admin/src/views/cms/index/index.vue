<template>
  <section class="index-main">
    <h1>舅服你商城系统介绍</h1>

    <p>把项目多余功能全删除以后一个脚手架。</p>

    <h2>介绍</h2>

    <p>Jfinal + element-ui 最纯的后台脚手架，没有任何多余组件，有权限、用户、角色管理，有基础的文件上传，系统可以采取正常的梭哈写，也支持任务异步。</p>

    <h2>项目结构</h2>

    <p>
      <code>
        lua
        jfinal-mall
        ├── jfinal-mall-api -- 后端接口
        ├── jfinal-mall-admin -- 后台系统
        ├── qw_original_v1 -- 数据库脚本
      </code>
    </p>

    <h2>项目特色</h2>

    <ul>
      <li>最好用的私活神器，拿起来直接撸业务</li>
      <li>最简单直接的部署方式，一个Tomcat、一个MySQL齐活</li>
      <li>缓存使用ehcache，不用部署Redis</li>
      <li>权限框架使用Shiro，逻辑上权限等于目录</li>
      <li>使用Element-UI</li>
    </ul>

    <h2>你问我怎么用</h2>

    <ul>
      <li>导入数据库</li>
      <li>修改Java项目中resources/config.properties 中的数据库连接信息</li>
      <li>maven jetty:run，至此后端项目启动</li>
      <li>前端项目 npm install</li>
      <li>npm run dev 至此，后端项目启动</li>
    </ul>
  </section>
</template>

<script>
export default {
  data() {
    return {};
  },
  mounted() {},
  methods: {}
};
</script>

<style lang="less"  scoped>
.index-main {
  background-color: #f4f5f6;
  min-height: 800px;
}
</style>