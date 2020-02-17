<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="search">
        <el-form-item>
          <el-input v-model="search.key" placeholder="用户姓名\手机号码"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
          <el-button type="primary" @click="formStaff(0)" v-if="checkPermission('系统用户-新增')">添加用户</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="selection" min-width="40"></el-table-column>
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
        <el-table-column label="操作" min-width="100">
          <template slot-scope="scope">
            <el-button
              @click="formStaff(scope.row.id)"
              type="success"
              size="mini"
              v-if="checkPermission('系统用户-编辑')"
            >编辑</el-button>
            <el-button
              @click="resetPassword(scope.row)"
              type="warning"
              size="mini"
              v-if="checkPermission('系统用户-重置密码')"
            >重置密码</el-button>
            <el-button
              @click="delStaff(scope.row)"
              type="danger"
              size="mini"
              v-if="checkPermission('系统用户-删除')"
            >删除</el-button>
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
      title="新增/编辑"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="100px">
        <el-form-item label="用户名：" prop="username" :rules="rules.val">
          <el-input type="input" v-model="userForm.username"></el-input>
        </el-form-item>
        <el-form-item label="姓名：" prop="name" :rules="rules.val">
          <el-input type="input" v-model="userForm.name"></el-input>
        </el-form-item>
        <el-form-item label="手机号码：" prop="phone" :rules="rules.phone">
          <el-input type="input" v-model="userForm.phone"></el-input>
        </el-form-item>

        <el-form-item label="角色：">
          <el-checkbox
            v-for="r in userForm.role"
            :key="r.id"
            :label="r.id"
            :checked="r.isChosen === 1"
            v-model="userForm.roleId"
            border
            size="medium"
            :rules="rules.val"
          >{{r.roleName}}</el-checkbox>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="formStaffSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>
<script>
import util from "@/util/util";
import {
  pageList,
  resetPw,
  staffForm,
  staffDetail,
  staffDelete
} from "@/api/system/staff";
import { userRole } from "@/api/system/role";
import { Message, MessageBox } from "element-ui";
import rules from "@/comm/rules";
import Paging from "../../../components/paging";
import { checkPermission } from "@/util/operator";

export default {
  components: {
    Paging
  },
  data() {
    return {
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
      listLoading: false,
      dialogVisible: false,
      list: [],
      search: {
        key: ""
      },
      userForm: {
        id: "",
        username: "",
        name: "",
        phone: "",
        roleId: [],
        role: []
      },
      rules,
      colomus: [
        {
          label: "登录名",
          width: 100,
          props: "username"
        },
        {
          label: "姓名",
          width: 100,
          props: "name"
        },
        {
          label: "手机号码",
          props: "phone",
          width: 100
        },
        {
          label: "角色信息",
          props: "roleName",
          width: 100
        }
      ]
    };
  },
  mounted() {
    let _this = this;
    this.getList();
  },
  methods: {
    checkPermission,
    formStaff(staffId) {
      let _this = this;
      _this.dialogVisible = true;
      if (this.$refs["userForm"] !== undefined) {
        _this.$refs["userForm"].resetFields();
      }
      if (staffId) {
        staffDetail(staffId).then(res => {
          _this.userForm = res.data;
        });
      } else {
        userRole({ staffId: staffId }).then(res => {
          if (res.status === 0) {
            _this.userForm.role = res.data;
          }
        });
      }
    },
    formStaffSubmit() {
      let _this = this;
      let formData = _this.userForm;

      _this.$refs["userForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        delete formData.role;
        staffForm(formData).then(res => {
          if (res.data) {
            _this.dialogVisible = false;
            Message({
              message: "保存成功",
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
    resetPassword(row) {
      MessageBox.confirm('确定要重置"' + row.username + '"的密码', "重置密码", {
        confirmButtonText: "重置密码",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        resetPw(row.id).then(res => {
          if (res.status === 0) {
            MessageBox.confirm(res.msg);
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
    delStaff(row) {
      let _this = this;
      MessageBox.confirm(
        '确定要删除"' + row.username + '"的用户信息吗',
        "删除",
        {
          confirmButtonText: "确定删除",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        staffDelete(row.id).then(res => {
          if (res.status === 0) {
            Message({
              message: "删除成功",
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
  }
};
</script>