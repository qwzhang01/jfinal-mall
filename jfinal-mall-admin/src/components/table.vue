<template>
  <div class="table_box">
    <el-table
      :data="table"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      :fit="true"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        v-for="(item,key) in tableKey"
        :key="key"
        :prop="item.type"
        :label="item.name"
        :width="item.width"
      >
        <template slot-scope="scope">
          <!-- 图片 -->
          <div
            v-if="item.type=='image'||item.type=='imgPath'"
            class="image"
            @click="lookImges(scope.row[item.type])"
          >
            <el-image :src="scope.row[item.type]" fit="cover"></el-image>
          </div>
          <div v-else>{{scope.row[item.type]}}</div>
        </template>
      </el-table-column>

      <slot></slot>
    </el-table>
    <!-- 图片预览 -->
    <el-dialog :visible.sync="dialogVisible" size="tiny">
      <img width="100%" :src="dialogImageUrl" alt />
    </el-dialog>
  </div>
</template>

<script>
/**
 * 表格（无拖拽）
 * =========
 * 组件说明
 * ps:
 * =========
 */
import comm from "../comm/";
export default {
  props: ["table", "tableKey"],
  data() {
    return {
      dialogVisible: false,
      dialogImageUrl: ""
    };
  },
  mounted() {},
  methods: {
    handleClick(row) {
      console.log(row);
    },
    // 选择
    handleSelectionChange(val) {
      this.$emit("SelectionChange", val);
    },
    // 预览图片
    lookImges(img) {
      this.dialogVisible = !this.dialogVisible;
      this.dialogImageUrl = img;
      console.log(img);
    }
  }
};
</script>

<style lang="scss" scoped>
.search_box,
.image {
  display: flex;
  width: 100%;
  align-items: center;
}
.image {
  justify-content: center;
}
.image img:hover {
  cursor: pointer;
}
.demonstration {
  text-align: center;
}
.search {
  display: flex;
  width: 100%;
  align-items: center;
}
</style>