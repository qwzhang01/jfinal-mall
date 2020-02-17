<template>
  <div>
    <el-card shadow="always">
      <el-form
        :inline="true"
        :model="filters"
        @submit.native.prevent
        @keyup.enter.native="getList"
        class="toolbar"
      >
        <el-row :gutter="0">
          <el-col :span="3">
            <el-select v-model="filters.shipStatus" placeholder="发货状态">
              <el-option
                v-for="(item, index) in shipStatus"
                :label="item"
                :value="index"
                :key="index"
              ></el-option>
            </el-select>
          </el-col>
          <el-col :span="3">
            <el-select v-model="filters.activityType" placeholder="促销类型">
              <el-option label="普通订单" value="1" key="1"></el-option>
              <el-option label="秒杀订单" value="2" key="2"></el-option>
              <el-option label="拼团订单" value="3" key="3"></el-option>
            </el-select>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.mastOrderNo" placeholder="主订单号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.orderNo" placeholder="订单编号"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.mobile" placeholder="手机号码"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.goodName" placeholder="商品名称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.nickname" placeholder="用户昵称"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-input v-model="filters.leaderName" placeholder="推广人"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-date-picker
              :clearable="true"
              v-model="filters.payDateRange"
              type="daterange"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              range-separator="至"
              start-placeholder="支付时间"
              end-placeholder="支付时间"
            ></el-date-picker>
          </el-col>
          <el-col :span="3">
            <el-form-item>
              <el-button type="success" v-on:click="getList" size="small">查询</el-button>
              <el-button type="primary" size="small" v-on:click="downloadOrder">导出</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    <el-card shadow="always">
      <!--列表-->
      <el-table
        :data="list"
        :highlight-current-row="true"
        v-loading="listLoading"
        :stripe="true"
        size="small"
        style="width: 100%;"
      >
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
        <el-table-column label="发货结果" min-width="200">
          <template slot-scope="scope">
            <div>发货状态： {{smpFmtData.shipStatus[scope.row.shipStatus]}}</div>
            <div v-if="scope.row.shipStatus !== 0">发货时间： {{scope.row.shipping_time}}</div>
            <div v-if="scope.row.shipStatus !== 0">物流公司： {{scope.row.expressName}}</div>
            <div v-if="scope.row.shipStatus !== 0">物流单号： {{scope.row.shipping_code}}</div>
            <div v-if="scope.row.shipStatus === 2">收货时间： {{scope.row.confirmTime}}</div>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              @click="deliver(scope.row)"
              type="success"
              size="mini"
              v-if="checkStatus(scope.row) && checkPermission('订单发货-发货')"
            >发货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Paging
        :page="page"
        @handleCurrentChange="handleCurrentChange"
        @handleSizeChange="handleSizeChange"
      ></Paging>
    </el-card>
    <el-dialog
      title="填写发货信息"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="快递公司：" prop="expressCompanyId" :rules="rules.val">
          <el-select v-model="form.expressCompanyId" placeholder="请选择快递公司">
            <el-option
              v-for="item in expressCompanyList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号：" prop="shippingCode" :rules="rules.val">
          <el-input type="text" v-model="form.shippingCode" placeholder="请填写快递单号"></el-input>
        </el-form-item>
        <el-form-item label="运费：" prop="shippingPrice" :rules="rules.numPot2">
          <el-input type="text" v-model="form.shippingPrice" placeholder="请填写快递单号"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="deliverSubject()">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import util from "@/util/util";
import { orderList, downloadShippping, saveDeliver } from "@/api/order/order";
import { dict, expressCompany } from "@/api/system/param";
import Paging from "../../../components/paging";
import { checkPermission } from "@/util/operator";
import { Message, MessageBox } from "element-ui";
import NProgress from "nprogress";
import config from "./config.js";
import rules from "../../../comm/rules";

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
      shipStatus: config.dict.shipStatus,
      list: [],
      payStatus: [],
      smpFmtData: config.dict,
      expressCompanyList: [],
      dialogVisible: false,
      rules,
      form: config.form
    };
  },
  methods: {
    checkPermission,
    // 代发货状态的，细看是按钮
    checkStatus(row) {
      if (row.shipStatus === 0) {
        return true;
      }
      return false;
    },
    // 显示发货表单
    deliver(row) {
      this.dialogVisible = true;
      if (this.$refs["form"] !== undefined) {
        this.$refs["form"].resetFields();
      }
      this.form.orderId = row.orderId;
    },
    deliverSubject() {
      let _this = this;
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return false;
        }
        saveDeliver(_this.form).then(res => {
          if (res.status === 0) {
            _this.dialogVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          }
        });
      });
    },
    downloadOrder() {
      let date = this.filters.payDateRange;
      let para = this.filters;
      para.payDateRangeStart = date != null && date.length > 0 ? date[0] : null;
      para.payDateRangeEnd = date != null && date.length > 0 ? date[1] : null;
      delete para.payDateRange;
      delete para.rangeDate;
      downloadShippping(para);
    },
    //获取列表
    getList() {
      this.listLoading = true;
      let date = this.filters.payDateRange;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.filters
      };
      para.payDateRangeStart = date != null && date.length > 0 ? date[0] : null;
      para.payDateRangeEnd = date != null && date.length > 0 ? date[1] : null;

      delete para.payDateRange;
      delete para.rangeDate;

      NProgress.start();
      orderList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list.map(d => {
          if (d.shipStatus === 0) {
            d.shipping_time = "";
          }
          return d;
        });
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
    // 初始化快递公司数据
    expressCompany().then(res => {
      _this.expressCompanyList = res.data;
    });
    _this.getList();
  }
};
</script>
