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
            <el-button type="primary" v-on:click="download">导出</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="selection" min-width="30"></el-table-column>
        <el-table-column type="index" label="序号" width="70"></el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
        ></el-table-column>
        <el-table-column label="操作" min-width="170">
          <template slot-scope="scope">
            <el-button
              @click="handlePay(scope.row)"
              type="danger"
              size="mini"
              v-if="checkPermission('打款-打款')"
            >打款</el-button>
            <el-button
              @click="handlePayOffLine(scope.row)"
              type="danger"
              size="mini"
              v-if="checkPermission('打款-人工打款')"
            >人工打款</el-button>
          </template>
        </el-table-column>
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
import {
  authList,
  downloadAuth,
  pay,
  payHand
} from "@/api/withdrawal/pointWithdrawal";
import { Message, MessageBox } from "element-ui";
import Paging from "../../../components/paging";
import { checkPermission } from "@/util/operator";

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
          width: 70,
          props: "amount"
        },
        {
          label: "实际金额",
          width: 70,
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
          label: "开户行",
          width: 100,
          props: "bankName"
        },
        {
          label: "持卡人",
          width: 80,
          props: "bankUserName"
        },
        {
          label: "银行卡号",
          width: 100,
          props: "bankCard"
        }
      ]
    };
  },
  methods: {
    checkPermission,
    handlePayOffLine(row) {
      let _this = this;
      let name = row.nickname;
      MessageBox.confirm(
        "请仔细核对“" + name + "”的提现申请，确定修改状态为打款完成状态",
        "确定人工打款",
        {
          confirmButtonText: "确定人工打款",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        payHand({ id: row.id }).then(res => {
          if (res.status === 0) {
            Message({
              message: "打款成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          }
        });
      });
    },
    handlePay(row) {
      let _this = this;
      let name = row.nickname;
      MessageBox.confirm(
        "请仔细核对“" + name + "”的提现申请，确定后系统会自动将钱转入对方账户",
        "确定打款",
        {
          confirmButtonText: "确定打款",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        pay({ id: row.id }).then(res => {
          if (res.status === 0) {
            Message({
              message: "打款成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          }
        });
      });
    },
    download() {
      let date = this.search.rangeDate;
      let para = this.search;
      para.startDate =
        date != null && date.length > 0
          ? util.formatDate.format(date[0], "yyyy-MM-dd")
          : null;
      para.endDate =
        date != null && date.length > 0
          ? util.formatDate.format(date[1], "yyyy-MM-dd")
          : null;
      para.authStatus = 2;
      delete para.rangeDate;

      downloadAuth(para);
    },
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
      para.authStatus = 2;

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