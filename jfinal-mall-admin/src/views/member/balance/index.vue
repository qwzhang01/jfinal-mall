<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.key" placeholder="姓名/手机"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" v-on:click="getList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>
    <div class="table-container">
      <el-table
        :data="list"
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
  </section>
</template>

<script>
import util from "@/util/util";
import { pageList } from "@/api/member/balance";
import Paging from "../../../components/paging";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      colomus: [
        {
          label: "用户昵称",
          width: 120,
          props: "nickname"
        },
        {
          label: "手机号码",
          width: 140,
          props: "mobile"
        },
        {
          label: "金额",
          width: 120,
          props: "amount"
        },
        {
          label: "所属业务",
          width: 120,
          props: "amount_type",
          formatter: (row, column) => {
            let type = [
              "",
              "平台推广佣金",
              "店铺推广佣金",
              "特殊商品优惠返现",
              "充值",
              "商品销售收入",
              "提现",
              "消费",
              "推广首单奖励"
            ];
            return type[row.amount_type];
          }
        },
        {
          label: "类型",
          width: 90,
          props: "in_out_flag",
          formatter: (row, column) => {
            let type = ["", "收入", "支出"];
            return type[row.in_out_flag];
          }
        },
        {
          label: "状态",
          width: 90,
          props: "frozen_flag",
          formatter: (row, column) => {
            let type = ["", "冻结", " 正常", "取消"];
            return type[row.frozen_flag];
          }
        },
        {
          label: "生成时间",
          width: 120,
          props: "create_time"
        }
      ],
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
      search: {
        key: ""
      },
      list: []
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
    }
  },
  mounted() {
    this.getList();
  }
};
</script>

<style scoped>
</style>