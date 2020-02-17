<template>
  <el-col :span="span?span: 24">
    <!-- 导航菜单 -->
    <el-menu
      :default-active="activeIndex"
      mode="horizontal"
      active-text-color="#373b46"
      @select="handleSelect"
    >
      <el-menu-item v-for=" (item,key) in nav " :key="key" :index="key.toString()">{{item.name}}</el-menu-item>
      <slot></slot>
    </el-menu>
  </el-col>
</template>

<script>
// 导航
export default {
  props: ["nav", "span"],
  data() {
    return {
      activeIndex: "0"
    };
  },
  mounted() {},
  methods: {
    handleSelect(key) {
      this.$emit("nav_btn", key);
    }
  }
};
</script>

<style scoped>
.el-menu-demo {
  width: 100%;
}
</style>