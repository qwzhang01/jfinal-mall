<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.key" placeholder="用户名称"></el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="success" v-on:click="getList">查询</el-button>
            <el-button type="primary" v-on:click="addRole" v-if="checkPermission('系统角色-新增')">添加角色</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <!-- 列表数据部分 -->
    <div class="table-container">
      <!--列表-->
      <el-table
        :data="list"
        :highlight-current-row="true"
        v-loading="listLoading"
        style="width: 100%;"
        @row-click="rowClick"
      >
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
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button @click="editForm(scope.row)" type="success"
              size="mini" v-if="checkPermission('系统角色-编辑')">编辑</el-button>
            <el-button @click="delRole(scope.row)" type="danger" size="mini" v-if="checkPermission('系统角色-删除')">删除</el-button>
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

    <!-- 权限数据部分 -->
    <div class="table-container operator-container">
      <el-row :gutter="20">
        <!--权限分配-->
        <el-col :span="20">
          <el-card class="box-card" shadow="never">
            <div slot="header" class="clearfix">
              <span class="role-span">{{htmlName}}</span>
              <el-button
                v-if="showButton && checkPermission('系统角色-设置权限')"
                :loading="permissionLoading"
                icon="el-icon-check"
                size="mini"
                style="float: right; padding: 4px 10px"
                type="info"
                @click="savePermission"
              >保存</el-button>
            </div>
            <div style="min-height: 400px;max-height:500px;overflow-y: auto;">
              <el-tree
                ref="permission"
                :default-expand-all="true"
                :expand-on-click-node="true"
                :data="permissions"
                :props="defaultProps"
                show-checkbox
                node-key="id"
              />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 角色表单 -->
    <el-dialog
      title="新增/编辑"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="roleForm" :rules="rules" ref="roleForm" label-width="100px">
        <el-form-item label="角色名称：" prop="roleName" :rules="rules.val">
          <el-input type="input" v-model="roleForm.roleName"></el-input>
        </el-form-item>
        <el-form-item label="角色描述：" prop="desc" :rules="rules.val">
          <el-input type="input" v-model="roleForm.desc"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="formRoleSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>
<script>
import util from "@/util/util";
import { checkPermission } from "@/util/operator";
import {
  pageList,
  operatorTree,
  editPermission,
  get,
  del,
  save
} from "@/api/role";
import { Message, MessageBox } from "element-ui";
import Paging from "../../../components/paging";
import rules from "@/comm/rules";

export default {
  components: {
    Paging
  },
  data() {
    return {
      dialogVisible: false,
      rules,
      roleForm: {
        id: "",
        roleName: "",
        desc: ""
      },
      permissionLoading: false,
      showButton: false,
      defaultProps: {
        children: "children",
        label: "label"
      },
      currentRoleId: 0,
      htmlName: '权限分配',
      permissions: [], // 权限信息
      permissionIds: [], // 权限选中信息
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
          label: "角色名称",
          width: 100,
          props: "roleName"
        },
        {
          label: "描述",
          width: 100,
          props: "desc"
        }
      ]
    };
  },
  mounted: function() {
    this.getList();
    operatorTree().then(res => {
      this.permissions = res.data;
    });
  },
  methods: {
    // 点击每行，同步该角色的权限信息
    rowClick(row) {
      // 清空权限与菜单的选中
      this.$refs.permission.setCheckedKeys([]);
      // 点击后显示按钮
      this.showButton = true;
      // 角色赋值
      this.currentRoleId = row.id;
      // 处理权限数据
      this.$refs.permission.setCheckedKeys(row.operationIds);
      this.htmlName = row.roleName + "-权限分配"
    },
    checkPermission,
    savePermission() {
      this.permissionLoading = true;
      const role = { roleId: this.currentRoleId, operatorId: [] };
      this.$refs.permission.getCheckedKeys().forEach(function(data, index) {
        if (data > 0) {
          role.operatorId.push(data);
        }
      });
      editPermission(role).then(res => {
        if (res.status === 0) {
          Message({
            message: "保存成功",
            type: "success",
            duration: 3 * 1000
          });
          this.permissionLoading = false;
          this.getList();
        }
      });
    },
    addRole() {
      this.dialogVisible = true;
      this.roleForm = {
        id: "",
        roleName: "",
        desc: ""
      };
    },
    // 角色表单
    editForm(row) {
      this.dialogVisible = true;
      if (row) {
        this.roleForm = row;
      }
    },
    formRoleSubmit() {
      this.$refs["roleForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        this.dialogVisible = false;
        save(this.roleForm).then(res => {
          Message({
            message: "保存成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    delRole(row) {
      MessageBox.confirm(
        '确定要删除"' + row.roleName + '"的角色信息吗',
        "删除",
        {
          confirmButtonText: "确定删除",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        del(row.id).then(res => {
          Message({
            message: "删除成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
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
<style lang="less" scoped>
.table-container {
  width: 49%;
  float: left;
}
.operator-container {
  margin-left: 2%;
}
</style>