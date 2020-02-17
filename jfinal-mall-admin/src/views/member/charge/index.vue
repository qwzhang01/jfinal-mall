<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.key" placeholder="昵称/手机"></el-input>
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
import { pageList } from "@/api/member/charge";
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
        key: ""
      },      
      colomus: [
        {
          label: "用户昵称",
          width: 100,
          props: "nickname"
        },
         {
          label: "充值编号",
          width: 100,
          props: "orderSn"
        },
        {
          label: "充值金额(元)",
          width: 150,
          props: "amount"
        },
        {
          label: "状态",
          width: 40,
          props: "payStatus",
          formatter: function(row, column) {
            let payStatus = {0: '待支付', 1: '充值成功', 2: '交易关闭'}
            return payStatus[row.payStatus]
          }
        },
        {
          label: "充值时间",
          width: 150,
          props: "createTime"
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