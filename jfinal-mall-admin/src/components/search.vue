<template>
  <el-col :span="span? span: 24" class="search_box">
    <div v-for="(item,key) in search" :key="key" :style="item.style" class="search">
      <!-- 输入框 -->
      <div class="demonstration" :style="item.titleStyle">{{item.title}}</div>
      <el-input
        v-model="searchFrom[item.value]"
        clearable
        :placeholder="item.placeholder?item.placeholder:'请输入内容'"
        v-if="item.type=='input'"
      ></el-input>

      <!-- 时间 开始---结束 -->
      <el-date-picker
        v-if="item.type=='time'"
        v-model="searchFrom[item.value]"
        type="daterange"
        align="right"
        unlink-panels
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        :picker-options="pickerOptions2"
      ></el-date-picker>

      <!-- 日期-->
      <el-date-picker
        v-if="item.type=='date'"
        v-model="searchFrom[item.value]"
        align="right"
        type="date"
        placeholder="选择日期"
      ></el-date-picker>

      <!-- 时间范围 -->
      <div v-if="item.type=='time2'">
        <el-time-select
          placeholder="起始时间"
          v-model="searchFrom[item.value]"
          :picker-options="item.picker_options"
        ></el-time-select>—
        <el-time-select
          placeholder="结束时间"
          v-model="searchFrom[item.value2]"
          :picker-options="item.picker_options"
        ></el-time-select>
      </div>
      <!-- 日期时间 -->
      <div v-if="item.type=='time3'">
        <el-date-picker
          v-model="searchFrom[item.value]"
          type="datetimerange"
          range-separator=" 至 "
          :placeholder="item.placeholder?item.placeholder:'请选择'"
        ></el-date-picker>
      </div>
      <!-- 下拉 -->
      <el-select
        v-if="item.type=='select'"
        v-model="searchFrom[item.value]"
        :placeholder="item.placeholder?item.placeholder:'请选择'"
      >
        <el-option
          v-for="(items,key) in item.options"
          :key="key"
          :label="items.label"
          :value="items"
        ></el-option>
      </el-select>
    </div>
    <slot></slot>
  </el-col>
</template>

<script>
/**
 * 搜索
 * =========
 * 组件说明
 * ps:
 * =========
 */
export default {
  props: ["search", "searchFrom", "span"],
  data() {
    return {
      input: "",
      value: "",
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [
          {
            text: "今天",
            onClick(picker) {
              picker.$emit("pick", new Date());
            }
          },
          {
            text: "昨天",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit("pick", date);
            }
          },
          {
            text: "一周前",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", date);
            }
          }
        ]
      },
      value7: ""
    };
  },
  mounted() {},
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    }
  }
};
</script>

<style lang="scss" scoped>
.el-date-editor.el-input {
  width: 200px;
}
.search_box {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: flex-end;
  padding-top: 0.5rem;
  height: 60px;
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