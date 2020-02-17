<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="filters" @submit.native.prevent @keyup.enter.native="getList">
        <el-form-item>
          <el-input v-model="filters.key" placeholder="姓名/手机"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="filters.firstLeader" placeholder="推广人"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
          <!-- <el-button type="primary" v-on:click="downloadUser">导出</el-button> -->
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
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              @click="auth(scope.row)"
              type="success"
              size="mini"
              v-if="scope.row.status === 1"
            >审核</el-button>
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

    <!-- 下线送可以提现积分表单 -->
    <el-dialog
      title="线下送积分"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="form" :rules="rules" ref="form" label-width="150px">
        <el-form-item label="额度：" prop="point" :rules="rules.val">
          <el-input type="number" v-model="form.point" placeholder="线下充值积分额度"></el-input>
        </el-form-item>
        <el-form-item label="是否可以提现：" prop="isWithdrow" :rules="rules.val">
          <el-radio-group v-model="form.isWithdrow">
            <el-radio :label="1" border>是</el-radio>
            <el-radio :label="2" border>否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="formSubject()">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>

<script>
import util from "@/util/util";
import { userList, auth } from "@/api/member/userinfo";
import Paging from "../../../components/paging";
import config from "./config.js";
import { checkPermission } from "@/util/operator";
import rules from "../../../comm/rules";
import { Message, MessageBox } from "element-ui";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      colomus: config.colomus,
      page: config.page,
      filters: config.search,
      users: [],
      // 显示表单
      dialogVisible: false,
      form: config.form,
      rules
    };
  },
  methods: {
    checkPermission,
    downloadUser() {
      let para = this.filters;
      download(para);
    },
    // 审核
    auth(row) {
      MessageBox.confirm("确定要审核吗", "审核", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        auth(row.user_id).then(res => {
          if (res.status === 0) {
            Message({
              message: "审核成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    // 人工送可以提现的积分
    giveWithdrowPoint(row) {
      this.dialogVisible = true;
      if (this.$refs["form"] !== undefined) {
        this.$refs["form"].resetFields();
      }
      this.form.userId = row.userId;
    },
    // 人工送积分表单保存
    formSubject() {
      let _this = this;
      this.$refs["form"].validate(valid => {
        if (!valid) {
          return false;
        }
        savePoint(_this.form).then(res => {
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
      userList(para).then(res => {
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

<style scoped>
</style>