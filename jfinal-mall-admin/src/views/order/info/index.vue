<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="filters" @submit.native.prevent @keyup.enter.native="getList">
        <el-select v-model="filters.activityType" placeholder="促销类型">
          <el-option label="普通订单" value="1" key="1"></el-option>
          <el-option label="秒杀订单" value="2" key="2"></el-option>
          <el-option label="拼团订单" value="3" key="3"></el-option>
        </el-select>
        <el-select v-model="filters.orderStatus" placeholder="订单类型">
          <el-option
            v-for="item in orderStatus"
            :label="item.value"
            :value="item.key"
            :key="item.key"
          ></el-option>
        </el-select>
        <el-select v-model="filters.payStatus" placeholder="支付状态">
          <el-option
            v-for="item in payStatus"
            :label="item.value"
            :value="item.key"
            :key="item.key"
          ></el-option>
        </el-select>
        <el-form-item>
          <el-input v-model="filters.mastOrderNo" placeholder="主订单号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.orderNo" placeholder="订单编号"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.mobile" placeholder="手机号码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.goodName" placeholder="商品名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.nickname" placeholder="用户昵称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.leaderName" placeholder="推广人"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.storeName" placeholder="店铺名称"></el-input>
        </el-form-item>

        <el-form-item>
          <el-date-picker
            v-model="filters.rangeDate"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['00:00:00', '23:59:59']"
            placeholder="下单时间"
          ></el-date-picker>
        </el-form-item>

        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
          <el-button type="primary" v-on:click="downloadOrder">导出</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="selection" min-width="40"></el-table-column>
        <el-table-column type="index" label="序号" width="80" fixed></el-table-column>
        <el-table-column label="订单编号" min-width="180" fixed>
          <template slot-scope="scope">
            <div>{{scope.row.masterOrderNo}}</div>
            <div>{{scope.row.orderNo}}</div>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(val,i) in configHeader"
          :key="i"
          :fixed="val.fixed? true: false"
          :prop="val.props"
          :label="val.label"
          :formatter="val.fmtFn? val.fmtFn: fmtData"
          :min-width="val.width"
        ></el-table-column>
        <el-table-column
          label="操作"
          min-width="180"
          v-if="checkPermission('订单信息-测试退款') || checkPermission('订单信息-线下支付')"
        >
          <template slot-scope="scope">
            <el-button
              @click="refundTest(scope.row)"
              type="danger"
              size="mini"
              v-if="scope.row.payStatus === 1 && checkPermission('订单信息-测试退款')"
            >退款</el-button>
            <el-button
              @click="payOffLine(scope.row)"
              type="danger"
              size="mini"
              v-if="scope.row.orderStatus === 0 && scope.row.payStatus === 0 && checkPermission('订单信息-线下支付')"
            >线下支付</el-button>
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
  </el-card>
</template>
<script>
import util from "@/util/util";
import {
  orderList,
  download,
  payOffLine as payOffLineApi,
  refundLanuchTest
} from "@/api/order/order";
import { dict } from "@/api/system/param";
import Paging from "../../../components/paging";
import { checkPermission } from "@/util/operator";
import { Message, MessageBox } from "element-ui";
import NProgress from "nprogress";
import config from "./config.js";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      page: config.page,
      filters: config.search,
      configHeader: config.colomus,
      list: [],
      orderStatus: [],
      payStatus: [],
      smpFmtData: config.dict
    };
  },
  methods: {
    checkPermission,
    refundTest(row) {
      MessageBox.confirm(
        '请慎重确认:是否要退款"' + row.title + '"的金额',
        "退款",
        {
          confirmButtonText: "确定退款",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        refundLanuchTest(row.orderId).then(res => {
          if (res.status === 0) {
            Message({
              message: "退款成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    // 线下支付
    payOffLine(row) {
      MessageBox.confirm(
        '请慎重确认:是否已经完成"' + row.title + '"线下支付',
        "线下支付",
        {
          confirmButtonText: "确定已完成线下支付",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        payOffLineApi(row.orderId).then(res => {
          if (res.status === 0) {
            Message({
              message: "支付完成",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    downloadOrder() {
      let date = this.filters.rangeDate;
      let para = this.filters;
      para.rangeDateStart =
        date != null && date.length > 0
          ? util.formatDate.format(date[0], "yyyy-MM-dd")
          : null;
      para.rangeDateEnd =
        date != null && date.length > 0
          ? util.formatDate.format(date[1], "yyyy-MM-dd")
          : null;

      delete para.rangeDate;
      download(para);
    },
    // 获取列表
    getList() {
      this.listLoading = true;
      let date = this.filters.rangeDate;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.filters
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
      NProgress.start();
      orderList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
        NProgress.done();
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
    },
    fmtData(row, column) {
      let tmp = row[column.property];
      tmp =
        (this.smpFmtData[column.property] &&
          this.smpFmtData[column.property][tmp]) ||
        tmp;
      return tmp;
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
