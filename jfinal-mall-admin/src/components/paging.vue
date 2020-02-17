<template>
  <div>
    <!--分页-->
    <el-col :span="24" class="pagination">
      <slot></slot>
      <!-- 
          current-page:
          page-size:当前页数
          total：总条数
      -->
      <el-pagination
        style="float:right;"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page.pageNumber"
        :page-sizes="[10,20,30,40]"
        :page-size="page.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.totalRow"
      ></el-pagination>
    </el-col>
  </div>
</template>

<script>
// 导航
export default {
  props: ["page"],
  data() {
    return {};
  },
  mounted() {},
  methods: {
    handleSizeChange(val) {
      this.$emit("handleSizeChange", val);
    },
    handleCurrentChange(val) {
      this.$emit("handleCurrentChange", val);
    }
  }
};
</script>

<style lang="scss" scoped>
.pagination {
  margin: 10px 0px;
  .el-form-item {
    margin-bottom: 10px;
  }
}
</style>