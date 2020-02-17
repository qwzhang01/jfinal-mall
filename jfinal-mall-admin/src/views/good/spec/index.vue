<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.name" placeholder="规格名称"></el-input>
          </el-form-item>
          <el-form-item>
            <el-cascader
              v-model="search.categoryIds"
              :options="categoryTree"
              :show-all-levels="true"
              :props="{value: 'id', label: 'name', checkStrictly : false, multiple: false, expandTrigger: 'hover'}"
              @change="handleCascaderChange"
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item>
            <el-button type="success" v-on:click="getList">搜索</el-button>
            <el-button type="primary" v-on:click="handleAddSpec">添加规格</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table
        :data="list"
        highlight-current-row
        v-loading="listLoading"
        style="width: 100%;"
        :stripe="true"
        height="650"
      >
        <el-table-column type="index" label="序号" width="50" align="center"></el-table-column>
        <el-table-column label="规格名称" width="100" align="center" prop="name"></el-table-column>
        <el-table-column label="规格项目" width="850" align="left">
          <template slot-scope="scope">
            <el-tag
              v-for="tag in scope.row.item"
              :key="tag.id"
              closable
              size="small"
              effect="plain"
              type="success"
              @close="handleDeleteItem(tag)"
            >{{tag.item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属分类" width="200" align="center" prop="categoryName"></el-table-column>
        <el-table-column label="操作" width="300" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="success"
              @click="handleAddSpecItem(scope.$index, scope.row)"
              v-if="checkPermission('商品规格-新增') || checkPermission('商品规格-编辑')"
            >添加规格项</el-button>
            <el-button
              size="mini"
              type="success"
              @click="handleEdit(scope.$index, scope.row)"
              v-if="checkPermission('商品规格-新增') || checkPermission('商品规格-编辑')"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
              v-if="checkPermission('商品规格-删除')"
            >删除</el-button>
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

    <!-- 表单 -->
    <el-dialog
      title="新增/编辑"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="specForm" :rules="rules" ref="specForm" label-width="100px">
        <el-form-item label="规格名称：" prop="name" :rules="rules.val">
          <el-input type="input" v-model="specForm.name"></el-input>
        </el-form-item>
        <el-form-item label="所属分类：" prop="categoryId" :rules="rules.val">
          <el-cascader
            v-model="specForm.categoryIds"
            :options="categoryTree"
            :show-all-levels="true"
            :props="{value: 'id', label: 'name', checkStrictly : false, multiple: false, expandTrigger: 'hover'}"
            @change="handleCascaderChangeForm"
            clearable
          ></el-cascader>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="formSpecSubmit()">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 表单 -->
    <el-dialog
      title="新增规格项"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="inputVisible"
      width="30%"
    >
      <el-form :model="specItemForm" :rules="rules" ref="specItemForm" label-width="100px">
        <el-form-item label="规格项：" prop="item" :rules="rules.val">
          <el-input type="input" v-model="specItemForm.item"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="inputVisible = false">取 消</el-button>
        <el-button type="primary" @click="formSpecItemSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </section>
</template>
<script>
import util from "@/util/util";
import {
  specList,
  deleteSpec,
  saveSpec,
  detailSpec,
  saveSpecItem,
  deleteSpecItem
} from "@/api/good/spec.js";
import Paging from "../../../components/paging";
import { Message, MessageBox } from "element-ui";
import { checkPermission } from "@/util/operator";
import rules from "@/comm/rules";
import { tree } from "@/api/good/category";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      dialogVisible: false,
      inputVisible: false,
      rules,
      page: {
        pageNumber: 1,
        pageSize: 10,
        totalPage: 1,
        totalRow: 0
      },
      list: [],
      search: {
        name: "",
        categoryIds: [],
        categoryId: ""
      },
      categoryTree: [],
      // 规格表单
      specForm: {
        id: "",
        name: "",
        categoryIds: [],
        categoryId: ""
      },
      // 规格项表单
      specItemForm: {
        id: "",
        item: "",
        specId: ""
      }
    };
  },
  mounted() {
    let _this = this;
    tree().then(res => {
      if (res.status == 0) {
        _this.categoryTree = res.data;
      }
    });
    this.getList();
  },
  methods: {
    // 添加规格项
    handleAddSpecItem(index, row) {
      this.inputVisible = true;
      if (this.$refs["specItemForm"] !== undefined) {
        this.$refs["specItemForm"].resetFields();
      }
      this.specItemForm.specId = row.id;
    },
    // 保存规格项
    formSpecItemSubmit() {
      this.$refs["specItemForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        saveSpecItem(this.specItemForm).then(res => {
          if (res.status === 0) {
            this.inputVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    // 显示新增规格表单
    handleAddSpec() {
      this.dialogVisible = true;
      if (this.$refs["specForm"] !== undefined) {
        this.$refs["specForm"].resetFields();
      }
    },
    // 编辑规格
    handleEdit(index, row) {
      let _this = this;
      this.dialogVisible = true;
      if (this.$refs["specForm"] !== undefined) {
        this.$refs["specForm"].resetFields();
      }
      detailSpec(row.id).then(res => {
        _this.specForm = res.data;
        _this.specForm.categoryIds = _this.specForm.categoryIds.map(i => parseInt(i));
      });
    },
    //提交规格表单
    formSpecSubmit() {
      let form = this.specForm;
      delete form.categoryIds;
      this.$refs["specForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        saveSpec(form).then(res => {
          if (res.status === 0) {
            this.dialogVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    // 删除规格项
    handleDeleteItem(tag) {
      MessageBox.confirm('确定要删除"' + tag.item + '"的规格项吗', "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteSpecItem(tag.id).then(res => {
          if (res.status == 0) {
            Message({
              message: "删除成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    // 删除规格
    handleDelete(index, row) {
      MessageBox.confirm('确定要删除"' + row.name + '"的规格信息吗', "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteSpec(row.id).then(res => {
          Message({
            message: "删除成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    // 表单分类级联
    handleCascaderChangeForm(value) {
      let l = value.length;
      let cId = value[l - 1];
      this.specForm.categoryId = cId;
    },
    // 搜索条件分类级联
    handleCascaderChange(value) {
      let l = value.length;
      let cId = value[l - 1];
      this.search.categoryId = cId;
    },
    checkPermission,
    // 获取分页数据
    getList() {
      this.listLoading = true;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        name: this.search.name,
        categoryId: this.search.categoryId
      };
      //NProgress.start();
      specList(para).then(res => {
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