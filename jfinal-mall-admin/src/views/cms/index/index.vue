<template>
  <section class="index-main">
    <el-row type="flex" :gutter="40" class="block-row">
      <el-col :span="14" class="static">
        <div class="title">
          <i class="el-icon-s-flag"></i>
          <span>商品订单</span>
        </div>
        <el-row type="flex" :gutter="40">
          <el-col :span="8">
            <div class="block-show block-brow">
              <div>{{orderInfo.unPayCount}}</div>
              <div>待支付</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="block-show block-green">
              <div>{{orderInfo.unShipCount}}</div>
              <div>待发货</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="block-show block-blue">
              <div>{{orderInfo.unReceverCount}}</div>
              <div>待收货</div>
            </div>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="10" class="static">
        <div class="title">
          <i class="el-icon-s-flag"></i>
          <span>实时概况</span>
          <span class="timestamp">更新时间: {{freshTime}}</span>
        </div>
        <el-row type="flex" :gutter="40">
          <el-col :span="12" class="block-show1">
            <div>当日订单数</div>
            <div class="num">{{orderInfo.dayPayCount}}</div>
          </el-col>
          <el-col :span="12" class="block-show1">
            <div>当日订单总金额(元)</div>
            <div class="num">￥{{orderInfo.dayPayAmount? orderInfo.dayPayAmount: 0}}</div>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-row type="flex" :gutter="40" class="block-row">
      <el-col :span="12" class="static">
        <div class="title">
          <i class="el-icon-s-flag"></i>
          <span>订单信息</span>
        </div>
        <el-row type="flex" :gutter="40">
          <el-col :span="6" class="block-show1">
            <div class="num">{{orderInfo.monthPayCount}}</div>
            <div>当月订单数</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">￥{{orderInfo.monthPayAmount}}</div>
            <div>当月订单金额</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">{{orderInfo.payCount}}</div>
            <div>累计订单数</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">￥{{orderInfo.totalPayAmount}}</div>
            <div>累计订单金额</div>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="12" class="static">
        <div class="title">
          <i class="el-icon-s-flag"></i>
          <span>用户信息</span>
        </div>
        <el-row type="flex" :gutter="40">
          <el-col :span="6" class="block-show1">
            <div class="num">{{userInfo.totalCount}}</div>
            <div>累计用户</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">{{userInfo.mouthCount}}</div>
            <div>当月新增用户</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">{{userInfo.weekCount}}</div>
            <div>当周新增用户</div>
          </el-col>
          <el-col :span="6" class="block-show1">
            <div class="num">{{userInfo.dayCount}}</div>
            <div>当日新增用户</div>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="block-row">
      <el-col :span="24" class="static">
        <div class="title">
          <i class="el-icon-s-flag"></i>
          <span>订单统计</span>
        </div>
        <div id="chartLine" style="width:100%; height:400px;"></div>
      </el-col>
    </el-row>
  </section>
</template>

<script>
import util from "@/util/util";
import echarts from "echarts";
import { userInfo, orderInfo, orderStatic } from "@/api/CMS/home.js";

export default {
  data() {
    return {
      freshTime: "",
      userInfo: {
        dayCount: 0,
        mouthCount: 0,
        totalCount: 0,
        weekCount: 0
      },
      orderInfo: {
        dayPayAmount: "",
        dayPayCount: "",
        monthPayAmount: "",
        monthPayCount: "",
        payCount: "",
        totalPayAmount: "",
        unPayCount: "",
        unReceverCount: "",
        unShipCount: ""
      },
      chartLine: null,
      chartLineData: {
        amount: [],
        count: [],
        xAxis: []
      }
    };
  },
  mounted() {
    this.freshTime = util.formatDate.format(new Date(), "yyyy-MM-dd hh:mm:ss");
    this.initUser();
    this.initOrder();
    this.initOrderStatic();
  },
  methods: {
    initUser() {
      let _this = this;
      userInfo().then(res => {
        if (res.status === 0) {
          _this.userInfo = res.data;
        }
      });
    },
    initOrder() {
      let _this = this;
      orderInfo().then(res => {
        if (res.status === 0) {
          _this.orderInfo = res.data;
          _this.orderInfo.totalPayAmount = util.abs(
            _this.orderInfo.totalPayAmount
          );
        }
      });
    },
    initOrderStatic() {
      let _this = this;
      orderStatic().then(res => {
        if (res.status === 0) {
          _this.chartLineData = res.data;
          _this.chartLineData.amount = _this.chartLineData.amount.map(d =>
            d ? d : 0
          );
          _this.drawCharts();
        }
      });
    },
    drawCharts() {
      this.drawLineChart();
    },
    drawLineChart() {
      let data = this.chartLineData;
      this.chartLine = echarts.init(document.getElementById("chartLine"));
      this.chartLine.setOption({
        title: {
          text: "订单统计"
        },
        tooltip: {
          trigger: "axis"
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },
        legend: {
          data: ["订单数量", "订单金额"]
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: data.xAxis
        },
        yAxis: {
          type: "value"
        },
        series: [
          {
            name: "订单数量",
            type: "line",
            stack: "总量",
            data: data.count
          },
          {
            name: "订单金额",
            type: "line",
            stack: "总量",
            data: data.amount
          }
        ]
      });
    }
  }
};
</script>

<style lang="less"  scoped>
.index-main {
  background-color: #f4f5f6;
  min-height: 800px;
  i {
    color: rgba(91, 137, 255, 1);
  }
  .block-row {
    height: 150px;
    .static {
      margin: 20px;
      background: rgb(255, 255, 255, 1);
      border: 1px solid rgba(241, 241, 241, 1);
      border-radius: 2px;
      .title {
        margin: 5px 5px 8px 5px;
        .timestamp {
          color: rgba(153, 153, 153, 1);
        }
      }
      .block-brow {
        background: rgb(249, 104, 104);
      }
      .block-green {
        background: rgb(151, 217, 131);
      }
      .block-blue {
        background: rgb(53, 206, 220);
      }
      .block-show {
        width: 100%;
        height: 60px;
        color: rgba(255, 255, 255, 1);
        text-align: center;
        font-family: PingFang SC;
        font-weight: bold;
      }
      .block-show1 {
        text-align: center;
        .num {
          font-size: 30px;
        }
      }
    }
  }
}
</style>