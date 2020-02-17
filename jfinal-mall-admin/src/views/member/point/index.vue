<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="filters" @submit.native.prevent @keyup.enter.native="getList">
        <el-form-item>
          <el-input v-model="filters.key" placeholder="姓名/手机"></el-input>
        </el-form-item>
        <el-select v-model="filters.type" placeholder="请选择积分类型">
          <el-option v-for="item in type" :label="item.name" :value="item.key" :key="item.key"></el-option>
        </el-select>
        <el-select v-model="filters.business_type" placeholder="请选择积分来源">
          <el-option v-for="item in businessType" :label="item.name" :value="item.key" :key="item.key"></el-option>
        </el-select>
        <el-select v-model="filters.is_withdraw" placeholder="请选择提现类型">
          <el-option v-for="item in isWithdraw" :label="item.name" :value="item.key" :key="item.key"></el-option>
        </el-select>
        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>
    <div class="table-container">
      <el-table
        :data="users"
        :stripe="true"
        highlight-current-row
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
  </el-card>
</template>

<script>
import util from "@/util/util";
import { page as pageApi } from "@/api/member/point";
import Paging from "../../../components/paging";
import config from "./config.js";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      colomus: config.colomus,
      page: config.page,
      type: config.type,
      businessType: config.businessType,
      isWithdraw: config.isWithdraw,
      filters: config.search,
      users: []
    };
  },
  methods: {
    // 前往第几页
    handleCurrentChange(val) {
      this.page.pageNumber = val;
      this.getList();
    },
    // 每页显示条数
    handleSizeChange(val) {
      this.page.pageSize = val;
      this.getList();
    },
    //获取用户列表
    getList() {
      this.listLoading = true;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.filters
      };
      //NProgress.start();
      pageApi(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.users = res.data.list;
        this.listLoading = false;
        //NProgress.done();
      });
    }
  },
  mounted() {
    this.getList();
  }
};
</script>