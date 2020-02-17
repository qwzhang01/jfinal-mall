<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-select v-model="search.orderStatus" placeholder="订单状态">
            <el-option
              v-for="item in orderStatus"
              :label="item.value"
              :value="item.key"
              :key="item.key"
            ></el-option>
          </el-select>
          <el-select v-model="search.payStatus" placeholder="支付状态">
            <el-option
              v-for="item in payStatus"
              :label="item.value"
              :value="item.key"
              :key="item.key"
            ></el-option>
          </el-select>
          <el-form-item>
            <el-input v-model="search.orderNo" placeholder="订单编号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="search.mobile" placeholder="手机号码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="search.goodName" placeholder="商品名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="search.nickname" placeholder="用户昵称"></el-input>
          </el-form-item>

          <el-form-item>
            <el-date-picker
              v-model="search.rangeDate"
              type="daterange"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :default-time="['00:00:00', '23:59:59']"
              placeholder="下单时间"
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
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column
          v-for="(val,i) in configHeader"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :formatter="val.fmtFn? val.fmtFn: fmtData"
          :min-width="val.width"
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
import { lotteryPageList } from "@/api/order/order";
import { dict } from "@/api/system/param";
import Paging from "../../../components/paging";

export default {
  components: {
    Paging
  },
  data() {
    return {
      list: [],
      orderStatus: [],
      payStatus: [],
      search: {
        orderNo: "",
        mobile: "",
        orderStatus: "",
        payStatus: "",
        nickname: "",
        goodName: "",
        activityStatus: "",
        rangeDate: []
      },
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
      configHeader: [
        {
          label: "订单编号",
          props: "orderNo",
          width: 180,
          fmtFn: function(row, column) {
            return row.masterOrderSn + "\r\n" + row.orderSn;
          }
        },
        {
          label: "用户昵称",
          width: 130,
          props: "nickname"
        },
        {
          label: "商品名称",
          props: "title",
          width: 380
        },
        {
          label: "抽奖结果",
          props: "isWin",
          width: 100
        },
        {
          label: "开奖时间",
          props: "openTime",
          width: 150
        },
        {
          label: "订单金额",
          width: 100,
          props: "orderAmount"
        },
        {
          label: "订单状态",
          width: 100,
          props: "orderStatus"
        },
        {
          label: "支付状态",
          width: 100,
          props: "payStatus"
        },
        {
          label: "开始时间",
          width: 150,
          props: "startTime"
        },
        {
          label: "结束时间",
          width: 150,
          props: "endTime"
        }
      ],
      smpFmtData: {
        isWin: ["", "中奖", "未中奖"],
        orderStatus: [
          "待确认",
          "已确认",
          "已收货",
          "已取消",
          "已完成",
          "已作废"
        ],
        payStatus: ["待支付", "已支付", "部分支付", "已退款", "拒绝退款"],
        activityType: ["", "普通订单", "秒杀订单", "拼团抽奖"]
      }
    };
  },
  methods: {
    fmtData(row, column) {
      if (column.property === "isWin") {
        if (!row.openTime) {
          return "未开奖";
        }
      }
      let tmp = row[column.property];
      tmp =
        (this.smpFmtData[column.property] &&
          this.smpFmtData[column.property][tmp]) ||
        tmp;
      return tmp;
    },
    getList() {
      this.listLoading = true;
      let date = this.search.rangeDate;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };
      para.rangeDateStart =
        date != null && date.length > 0
          ? util.formatDate.format(date[0], "yyyy-MM-dd")
          : null;
      para.rangeDateEnd =
        date != null && date.length > 0
          ? util.formatDate.format(date[1], "yyyy-MM-dd")
          : null;

      delete para.rangeDate;
      //NProgress.start();
      lotteryPageList(para).then(res => {
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
    let _this = this;
    dict("pay_status").then(res => {
      _this.payStatus = res.data;
    });
    dict("order_status").then(res => {
      _this.orderStatus = res.data;
    });
    _this.getList();
  }
};
</script>
