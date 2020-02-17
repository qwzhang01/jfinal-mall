<template>
  <div class="table_box">
    <el-table
      :data="table"
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
      :fit="true"
    >
      <el-table-column type="selection" :fixed="false" width="55"></el-table-column>
      <el-table-column
        v-for="(item,key) in tableKey"
        :key="key"
        :fixed="item.fixed"
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
            <img
              :src="scope.row[item.type]"
              :style="{'width':'100%;','height':(item.width/2)+'px'}"
            />
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
 * 表格（拖拽）
 * =========
 * 组件说明
 * ps:必填参数sortId
 * =========
 */
import Sortable from "sortablejs";
import comm from "../comm/";
export default {
  props: ["table", "tableKey", "columId"],
  data() {
    return {
      dialogVisible: false,
      dialogImageUrl: ""
    };
  },
  mounted() {
    this.rowDrop();
    this.columnDrop();
  },
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
    },
    //行拖拽
    rowDrop() {
      const tbody = document.querySelector(".el-table__body-wrapper tbody");
      const _this = this;
      Sortable.create(tbody, {
        onEnd({ newIndex, oldIndex }) {
          _this.newtable(newIndex, oldIndex);
        }
      });
    },
    // 替换
    newtable(newIndex, oldIndex) {
      let cId = this.columId;
      const _this = this;
      let table_Data = _this.table[oldIndex];
      // 替换
      _this.table[oldIndex] = _this.table[newIndex];
      _this.table[newIndex] = table_Data;

      let strId = _this.table[oldIndex][cId] + "," + _this.table[newIndex][cId];
      let tableList = _this.table;

      _this.$emit("sortFun", { strId, tableList });
    },
    //列拖拽
    columnDrop() {
      const wrapperTr = document.querySelector(".el-table__header-wrapper tr");
      this.sortable = Sortable.create(wrapperTr, {
        animation: 180,
        delay: 0,
        onEnd: evt => {
          const oldItem = this.dropCol[evt.oldIndex];
          this.dropCol.splice(evt.oldIndex, 1);
          this.dropCol.splice(evt.newIndex, 0, oldItem);
        }
      });
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