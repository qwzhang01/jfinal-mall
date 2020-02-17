<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.mobile" placeholder="手机号码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" v-on:click="getList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <!-- 列表数据部分 -->
    <div class="table-container">
      <!--列表-->
      <el-table
        :data="list"
        :highlight-current-row="true"
        v-loading="listLoading"
        style="width: 100%;"
      >
        <el-table-column type="selection" min-width="40"></el-table-column>
        <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
          :formatter="val.formatter"
          :show-overflow-tooltip="false"
        ></el-table-column>
      </el-table>

      <!-- 分页 -->
      <Paging
        :page="page"
        @handleCurrentChange="handleCurrentChange"
        @handleSizeChange="handleSizeChange"
      ></Paging>
    </div>
  </section>
</template>
<script>
import util from "@/util/util";
import { pageList } from "@/api/system/sms";
import Paging from "../../../components/paging";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      page: {
        // 页码
        pageNumber: 1,
        // 每页显示条数
        pageSize: 10,
        // 总页面
        totalPage: 1,
        // 总条数
        totalRow: 0
      },
      list: [],
      search: {
        mobile: ""
      },
      colomus: [
        {
          label: "手机号码",
          width: 40,
          props: "mobile"
        },
        {
          label: "短信内容",
          width: 150,
          props: "msg"
        },
        {
          label: "状态",
          width: 40,
          props: "status",
          formatter: function(row, column) {
            if (column === 0) {
              return "发送失败";
            }
            return "发送成功";
          }
        },
        {
          label: "发送时间",
          width: 50,
          props: "add_time",
          formatter: function(row, column) {
            return util.formatDate.format(
              new Date(parseInt(row.add_time) * 1000),
              "yyyy-MM-dd hh:mm:ss"
            );
          }
        },
        {
          label: "详细记录",
          props: "error_msg",
          formatter: function(row, column) {
            if(row.status == 0){
              return row.error_msg
            }
            return ''
          }
        }
      ]
    };
  },
  mounted: function() {
    this.getList();
  },
  methods: {
    getList() {
      this.listLoading = true;
      let date = this.search.rangeDate;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };
      //NProgress.start();
      pageList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
        //NProgress.done();
      });
    },
    // 前往第几页
    handleCurrentChange(val) {
      this.page.pageNumber = val;
      this.getList();
    },
    // 每页显示条数
    handleSizeChange(val) {
      this.page.pageSize = val;
      this.getList();
    }
  }
};
</script>
<style lang="less" scoped>
</style>