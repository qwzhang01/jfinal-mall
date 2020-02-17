<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="filters">
          <el-form-item>
            <el-input v-model="filters.i_name" placeholder="服务人员"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.master_order_sn" placeholder="主订单号"></el-input>
          </el-form-item>
          <el-form-item>
            <el-input v-model="filters.order_sn" placeholder="订单编号"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="success" v-on:click="getList">查询</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table
        :data="list"
        :stripe="true"
        highlight-current-row
        v-loading="listLoading"
        style="width: 100%;"
      >
        <el-table-column type="selection" min-width="40"></el-table-column>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column
          v-for="(val,i) in configHeader"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :formatter="fmtData"
          :min-width="val.width"
          :show-overflow-tooltip="true"
        ></el-table-column>

        <el-table-column label="操作" min-width="150">
          <template slot-scope="scope">
            <template v-if="scope.row.i_active === 0 || scope.row.i_active === 3">
              <el-button @click="assignServer(scope.row)" type="success" size="mini">分派服务人员</el-button>
            </template>
            <template v-if="scope.row.i_active === 2">
              <el-button @click="reAssignServer(scope.row)" type="success" size="mini">重新分派</el-button>
              <el-button @click="cancelAssignServer(scope.row)" type="danger" size="mini">取消上门服务</el-button>
              <el-button @click="donw(scope.row)" type="success" size="mini">完成</el-button>
            </template>            
          </template>
        </el-table-column>
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

    <el-dialog
      title="分配服务员"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form ref="assignForm" :model="assignForm" label-width="80px" :rules="rules">
        <el-form-item label="客户地址" :rules="rules.val">
          <el-input v-model="assignForm.address"></el-input>
        </el-form-item>
        <el-form-item label="服务时间" :rules="rules.val">
          <el-col :span="11">
            <el-date-picker
              type="datetime"
              placeholder="选择时间"
              v-model="assignForm.dateStart"
              style="width: 100%;"
            ></el-date-picker>
          </el-col>
          <el-col class="line" :span="2">-</el-col>
          <el-col :span="11">
            <el-date-picker
              type="datetime"
              placeholder="选择时间"
              v-model="assignForm.dateEnd"
              style="width: 100%;"
            ></el-date-picker>
          </el-col>
        </el-form-item>
        <el-form-item label="服务人员" :rules="rules.val">
          <el-select v-model="assignForm.workerId" placeholder="服务人员">
            <el-option v-for="item in workers" :label="item.name" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="assignSubject()">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>
<script>
import util from "@/util/util";
import rules from "../../../comm/rules";
import { Message, MessageBox } from "element-ui";
import {
  doorServiceList,
  assignWork,
  doneDoorService,
  cancelAssign,
  done
} from "@/api/order/doorService";
import { all } from "@/api/order/work";
import Paging from "../../../components/paging";

export default {
  components: {
    Paging
  },
  data() {
    return {
      rules,
      dialogVisible: false,
      list: [],
      orderStatus: [],
      payStatus: [],
      filters: {
        i_name: "",
        order_sn: "",
        master_order_sn: ""
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
          label: "主订单编号",
          props: "master_order_sn",
          width: 180
        },
        {
          label: "订单编号",
          props: "order_sn",
          width: 180
        },
        {
          label: "商品名称",
          width: 130,
          props: "goods_name"
        },
        {
          label: "客户姓名",
          width: 130,
          props: "nickname"
        },
        {
          label: "联系方式",
          width: 130,
          props: "userMobile"
        },
        {
          label: "状态",
          props: "i_active",
          width: 100
        },
        {
          label: "预约时间",
          width: 100,
          props: "i_start_time"
        },
        {
          label: "安装地址",
          width: 100,
          props: "i_address"
        },
        {
          label: "服务人员",
          width: 100,
          props: "i_name"
        },
        {
          label: "服务联系方式",
          width: 130,
          props: "i_phone"
        }
      ],
      smpFmtData: {
        i_active: ["待安装", "已安装", "已分派", "已取消"]
      },
      workers: [],
      assignForm: {
        i_id: "",
        address: "",
        dateStart: "",
        dateEnd: "",
        workerId: ""
      }
    };
  },
  methods: {
    assignServer(row) {
      this.dialogVisible = true;
      this.assignForm.i_id = row.i_id;
      this.assignForm.address = "";
      this.assignForm.dateStart = "";
      this.assignForm.dateEnd = "";
      this.assignForm.workerId = "";
    },
    reAssignServer(row) {
      this.dialogVisible = true;
      this.assignForm.i_id = row.i_id;
      this.assignForm.address = row.i_address;
      this.assignForm.dateStart = row.i_start_time;
      this.assignForm.dateEnd = row.i_end_time;
      this.assignForm.workerId = row.worker_id;
    },
    assignSubject() {
      this.$refs["assignForm"].validate(valid => {
        if (!valid) {
          return false;
        }

        assignWork(this.assignForm).then(res => {
          if (res && res.status === 0) {
            this.dialogVisible = false;
            Message({
              message: "分配成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    cancelAssignServer(row) {
      MessageBox.confirm('确定要取消"' + row.i_name + '"的预约吗', "取消预约", {
        confirmButtonText: "确定取消",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        cancelAssign({ i_id: row.i_id }).then(res => {
          Message({
            message: "取消成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    donw(row) {
      MessageBox.confirm('确定要完成"' + row.i_name + '"的服务吗', "完成服务", {
        confirmButtonText: "确定完成",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        doneDoorService({ i_id: row.i_id }).then(res => {
          Message({
            message: "完成成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    //获取用户列表
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

      //NProgress.start();
      doorServiceList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
        //NProgress.done();
      });
    },
    fmtData(row, column) {
      let tmp = row[column.property];
      tmp =
        (this.smpFmtData[column.property] &&
          this.smpFmtData[column.property][tmp]) ||
        tmp;
      return tmp;
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
    _this.getList();
    all().then(res => {
      _this.workers = res.data;
    });
  }
};
</script>