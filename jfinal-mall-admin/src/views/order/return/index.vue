<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="filters">
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
            <el-select v-model="filters.status" placeholder="退货状态">
              <el-option
                v-for="item in statusSelect"
                :label="item.value"
                :value="item.key"
                :key="item.key"
              ></el-option>
            </el-select>
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
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="index" label="序号" width="70"></el-table-column>
        <el-table-column
          v-for="(val,i) in configHeader"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :formatter="val.fmtData"
          :min-width="val.width"
        ></el-table-column>

        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              @click="agreeRefund(scope.row)"
               type="success"
              size="mini"
              v-if="scope.row.status === 3"
            >同意退款</el-button>
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
import { returnPageList, refund } from "@/api/order/order";
import Paging from "../../../components/paging";
import { Message, MessageBox } from "element-ui";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      list: [],
      filters: {
        orderNo: "",
        mobile: "",
        nickname: "",
        goodName: "",
        status: "",
        rangeDate: []
      },
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
      statusName: {
        "-2": "用户取消",
        "-1": "不同意",
        "0": "待审核",
        "1": "通过",
        "2": "已发货",
        "3": "待退款",
        "4": "换货完成",
        "5": "退款完成",
        "6": "申诉仲裁"
      },
       activityType: {
        1: '普通订单',
        2: '秒杀订单',
        3: '拼团抽奖'
      },
      configHeader: [
        {
          label: "订单编号",
          props: "orderNo",
          width: 180,
          fmtData: function(row) {
            return row.masterOrderNo + "\r\n" + row.orderNo;
          }
        },
        {
          label: "用户昵称",
          width: 130,
          props: "nickname"
        },
        {
          label: "用户手机",
          width: 130,
          props: "mobile"
        },
        {
          label: "订单商品",
          props: "title",
          width: 380
        },
        {
          label: "退款金额",
          props: "refundMoney",
          width: 130
        },
        {
          label: "退款状态",
          props: "status",
          width: 130,
          fmtData: (row, column) => {
            return this.statusName[row.status.toString()];
          }
        },
         {
          label: "订单类型",
          width: 100,
          props: "activityType",
           formatterFn: (row, column) => {
            return this.activityType[row.activityType];
          }
        },
        {
          label: "退款原因",
          props: "reason",
          width: 130
        },
        {
          label: "商品数量",
          props: "goodNum",
          width: 130
        },
        {
          label: "商品价格",
          width: 100,
          props: "goodPrice"
        },
        {
          label: "订单金额",
          width: 100,
          props: "orderAmount"
        },
        {
          label: "支付时间",
          width: 150,
          props: "payTime"
        },
        {
          label: "支付方式",
          width: 120,
          props: "payName"
        },
        {
          label: "下单时间",
          width: 150,
          props: "orderTime"
        }
      ]
    };
  },
  methods: {
    statusSelect() {
      let status = this.statusName;
      let select = [];
      for (var key in status) {
        select.push({
          key,
          value: status[key]
        });
      }
      return select;
    },
    // 同意退款
    agreeRefund(row) {
      MessageBox.confirm(
        '确定同意"' + row.nickname + '"的退款申请吗',
        "退款审核",
        {
          confirmButtonText: "同意退款",
          cancelButtonText: "拒绝退款",
          type: "warning"
        }
      ).then(() => {
        refund(row.orderId).then(res => {
          Message({
            message: res.status === 0 ? "退款成功" : "退款失败",
            type: "success",
            duration: 3 * 1000
          });
        });
        this.getList();
      });
    },
    // 获取分页列表数据
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
      //NProgress.start();
      returnPageList(para).then(res => {
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
    this.statusSelect = this.statusSelect();
    this.getList();
  }
};
</script>