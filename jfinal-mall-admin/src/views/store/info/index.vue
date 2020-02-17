<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="search">
        <el-form-item>
          <el-input v-model="search.key" placeholder="客户姓名"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="selection" min-width="40"></el-table-column>
        <el-table-column type="index" label="序号" width="50"></el-table-column>
        <el-table-column prop="name" label="姓名" min-width="60"></el-table-column>
        <el-table-column prop="mobile" label="手机" min-width="80"></el-table-column>
        <el-table-column label="套餐" min-width="60">
          <template slot-scope="scope">{{scope.row.productkey === 1? '129套餐':'199套餐'}}</template>
        </el-table-column>
        <el-table-column label="状态" min-width="60">
          <template slot-scope="scope">{{scope.row.status === 1? '未审核':'审核通过'}}</template>
        </el-table-column>
        <el-table-column prop="promName" label="上报人" min-width="100"></el-table-column>
        <el-table-column label="身份证正面" min-width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.cardFront" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="身份证反面" min-width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.cardBack" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="协议" min-width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.agreement" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="手持协议" min-width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.agreementHand" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="半身相" min-width="100">
          <template slot-scope="scope">
            <el-image :src="scope.row.bust" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope" v-if="scope.row.status === 1">
            <el-button @click="authFn(scope.row)" type="success" size="mini">审核</el-button>
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
      title="设置平台佣金比例"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="authForm" :rules="rules" ref="authForm" label-width="100px" name>
        <el-form-item label="平台分佣：" prop="butlerRate">
          <el-input type="text" v-model="authForm.butlerRate"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="subjectButlerRate()">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>
<script>
import util from "@/util/util";
import { pageList, auth } from "@/api/client";
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
        key: ""
      },
      colomus: [
        {
          label: "店铺名称",
          width: 100,
          props: "storeName"
        },
        {
          label: "店主",
          width: 100,
          props: "userName"
        },
        {
          label: "平台抽成比例",
          width: 100,
          props: "commissionRate"
        },
        {
          label: "总销售额",
          width: 100,
          props: "saleAmount"
        },
        {
          label: "总平台总佣金",
          props: "commissionAmount",
          width: 130
        },
        {
          label: "门店地址",
          props: "storeAddress",
          width: 130
        }
      ],
      authForm: {
        storeId: "",
        butlerRate: ""
      },
      rules: {
        butlerRate: [
          { required: true, message: "平台佣金为空" },
          {
            validator(rule, value, callback) {
              var reg = /^-?\d{1,5}(?:\.\d{1,3})?$/;
              if (reg.test(value)) {
                callback();
              } else {
                callback(new Error("请输入大于零小于十万不超过三位小数的数字"));
              }
            },
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    authFn(row) {
      MessageBox.confirm("确定要审核吗", "审核", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        auth({ id: row.id }).then(res => {
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
    setButlerRate(row) {
      this.dialogVisible = true;
      this.authForm.storeId = row.storeId;
      this.authForm.butlerRate = "";
    },
    subjectButlerRate() {
      let _this = this;
      let formData = _this.authForm;
      _this.$refs["authForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        auth(formData).then(res => {
          if (res.status === 0) {
            _this.dialogVisible = false;
            Message({
              message: "设置成功",
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
    getList() {
      this.listLoading = true;
      let date = this.search.rangeDate;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };

      //NProgress.start();
      pageList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
        //NProgress.done();
      });
    },
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