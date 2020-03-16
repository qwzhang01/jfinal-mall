<template>
  <el-card class="box-card">
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
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button @click="auth(scope.row)" type="danger" size="mini" v-if="checkPermission('审核-审核')">审核</el-button>
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

    <el-dialog
      title="审核提现申请"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="authForm" :rules="rules" ref="authForm" label-width="100px" name>
        <el-form-item label="*审核：" prop="authStatus">
          <el-radio-group v-model="authForm.authStatus">
            <el-radio :label="2" border>通过</el-radio>
            <el-radio :label="5" border>不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="*审核说明：" prop="reason">
          <el-input type="textarea" v-model="authForm.reason"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="authSubject()">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>
<script>
import util from "@/util/util";
import { authList, downloadAuth, auth } from "@/api/withdrawal/pointWithdrawal";
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
          width: 100,
          props: "amount"
        },
        {
          label: "申请时间",
          props: "applyTime",
          width: 120
        },
        {
          label: "实际金额",
          width: 100,
          props: "actual_amount"
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
      ],
      authForm: {
        id: "",
        nickname: "",
        amount: "",
        authStatus: "",
        reason: ""
      },
      rules: {
        reason: [
          {
            max: 100,
            message: "说明在100字以内",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    checkPermission,
    auth(row) {
      this.dialogVisible = true;
      this.authForm.id = row.id;
      this.authForm.nickname = row.nickname;
      this.authForm.amount = row.amount;
      this.authForm.authStatus = 2;
      this.authForm.reason = "";
    },
    authSubject: function() {
      let _this = this;
      let formData = _this.authForm;

      _this.$refs["authForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        auth({
          id: formData.id,
          authStatus: formData.authStatus,
          reason: formData.reason
        }).then(res => {
          if (res.data) {
            _this.dialogVisible = false;
            Message({
              message: "审核成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          } else {
            Message({
              message: res.msg ? res.msg : "系统异常",
              type: "error",
              duration: 3 * 1000
            });
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
      para.authStatus = 1;
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
      para.authStatus = 1;

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
