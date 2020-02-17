<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.withdrawSn" placeholder="申请编号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="search.nickname" placeholder="姓名"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="search.userMobile" placeholder="手机"></el-input>
          </el-form-item>

          <el-form-item>
            <el-date-picker
              v-model="search.rangeDate"
              type="daterange"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="['00:00:00', '23:59:59']"
              placeholder="申请时间"
            ></el-date-picker>
          </el-form-item>

          <el-form-item>
            <el-button type="success" v-on:click="getList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="selection" min-width="40"></el-table-column>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
        ></el-table-column>
      </el-table>

      <!-- 分页 -->
      <Paging
        :page="page"
        @handleCurrentChange="handleCurrentChange"
        @handleSizeChange="handleSizeChange"
      >
        <!-- <el-button type="danger"  @click="batchRemove" >批量删除</el-button> -->
      </Paging>
    </div>
  </section>
</template>
<script>
import util from "@/util/util";
import { authList, pay } from "@/api/withdrawal/pointWithdrawal";
import { Message, MessageBox } from "element-ui";
import Paging from "../../../components/paging";

export default {
  components: {
    Paging
  },
  data() {
    return {
      dialogVisible: false,
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
        withdrawSn: "",
        nickname: "",
        userMobile: "",
        rangeDate: []
      },
      colomus: [
        {
          label: "申请编号",
          width: 100,
          props: "withdraw_sn"
        },
        {
          label: "申请人",
          width: 100,
          props: "nickname"
        },
        {
          label: "手机号码",
          width: 100,
          props: "mobile"
        },
        {
          label: "申请金额",
          width: 100,
          props: "amount"
        },
        {
          label: "实际金额",
          width: 100,
          props: "actual_amount"
        },
        {
          label: "申请时间",
          props: "applyTime",
          width: 130
        },
        {
          label: "审核时间",
          props: "authTime",
          width: 130
        },
        {
          label: "审核人",
          props: "authName",
          width: 100
        },
        {
          label: "拒绝原因",
          props: "refuseReason",
          width: 100
        },
        {
          label: "开户行",
          width: 100,
          props: "bankName"
        },
        {
          label: "持卡人",
          width: 100,
          props: "bankUserName"
        },
        {
          label: "银行卡号",
          width: 150,
          props: "bankCard"
        }
      ]
    };
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
      para.startDate =
        date != null && date.length > 0
          ? util.formatDate.format(date[0], "yyyy-MM-dd")
          : null;
      para.endDate =
        date != null && date.length > 0
          ? util.formatDate.format(date[1], "yyyy-MM-dd")
          : null;
      para.authStatus = 5;

      delete para.rangeDate;
      //NProgress.start();
      authList(para).then(res => {
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
  },
  mounted() {
    this.getList();
  }
};
</script>