<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="search">
        <el-form-item>
          <el-input v-model="search.key" placeholder="名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="success" v-on:click="getList">搜索</el-button>
          <el-button type="primary" v-on:click="brandForm">添加品牌</el-button>
        </el-form-item>
      </el-form>
    </el-col>
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
        <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
        <el-table-column width="120" align="center">
          <template slot-scope="scope">
            <el-image style="height: 80px;" :src="scope.row.logo" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
          :formatter="val.formatter"
          :show-overflow-tooltip="false"
        ></el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              @click="handleEdit(scope.$index, scope.row)"
              v-if="checkPermission('品牌管理-删除')"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
              v-if="checkPermission('品牌管理-删除')"
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
      <el-form :model="brankForm" :rules="rules" ref="brankForm" label-width="100px">
        <el-form-item label="品牌名称：" prop="name" :rules="rules.val">
          <el-input type="input" v-model="brankForm.name"></el-input>
        </el-form-item>
        <el-form-item label="关联分类：" prop="categoryId" :rules="rules.val">
          <el-cascader
            v-model="brankForm.categoryIds"
            :options="categoryTree"
            :show-all-levels="true"
            :props="{value: 'id', label: 'name', checkStrictly : true, multiple: false, expandTrigger: 'hover'}"
            @change="handleCascaderChange"
            clearable
          ></el-cascader>
        </el-form-item>
        <el-form-item label="品牌描述：" prop="desc" :rules="rules.val">
          <el-input type="input" v-model="brankForm.desc"></el-input>
        </el-form-item>
        <el-form-item label="品牌LOGO" prop="logo" :rules="rules.val">
          <el-upload
            :action="UpdateFileFast"
            list-type="picture-card"
            ref="my_upload"
            :on-preview="handlePictureCardPreview"
            :on-success="successImg"
            :on-remove="handleRemove"
          >
            <i class="el-icon-plus"></i>
          </el-upload>
          <el-dialog :visible.sync="uploadVisible" size="tiny">
            <img width="100%" :src="brankForm.logoShowPath" alt />
          </el-dialog>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="formBrandSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </el-card>
</template>
<script>
import util from "@/util/util";
import {
  brandList,
  deleteBrand,
  saveBrand,
  detailBrand
} from "@/api/good/brand.js";
import Paging from "../../../components/paging";
import { Message, MessageBox } from "element-ui";
import { checkPermission } from "@/util/operator";
import rules from "@/comm/rules";
import { tree } from "@/api/good/category";
import { UpdateFileFast } from "../../../api/comm.js";

export default {
  components: {
    Paging
  },
  data() {
    return {
      UpdateFileFast,
      listLoading: false,
      dialogVisible: false,
      uploadVisible: false,
      rules,
      page: {
        pageNumber: 1,
        pageSize: 10,
        totalPage: 1,
        totalRow: 0
      },
      list: [],
      search: {
        key: ""
      },
      saleStatus: [],
      colomus: [
        {
          label: "品牌名称",
          width: 100,
          props: "name"
        },
        {
          label: "品牌分类",
          width: 150,
          props: "categoryName"
        },
        {
          label: "品牌描述",
          width: 200,
          props: "desc"
        },

        {
          label: "商品状态",
          props: "status",
          width: 100,
          formatter: function(row, column, cellValue, index) {
            // 0正常 1审核中 2审核失败
            let statusArray = ["正常", "审核中", "审核失败"];
            return statusArray[cellValue];
          }
        }
      ],
      categoryTree: [],
      // 品牌表单
      brankForm: {
        id: "",
        name: "",
        categoryIds: [],
        categoryId: "",
        logo: "",
        logoShowPath: "",
        desc: ""
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
    checkPermission,
    // 删除品牌
    handleDelete(index, row) {
      MessageBox.confirm('确定要删除"' + row.name + '"的品牌信息吗', "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteBrand(row.id).then(res => {
          Message({
            message: "删除成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    handleEdit(index, row) {
      let _this = this;
      this.dialogVisible = true;
      if (this.$refs["brankForm"] !== undefined) {
        this.$refs["brankForm"].resetFields();
      }
      detailBrand(row.id).then(res => {
        _this.brankForm = res.data;
        if (_this.brankForm.categoryIds) {
          _this.brankForm.categoryIds = _this.brankForm.categoryIds.map(i =>
            parseInt(i)
          );
        }
      });
    },
    // 显示新增品牌表单
    brandForm() {
      this.dialogVisible = true;
      if (this.$refs["brankForm"] !== undefined) {
        this.$refs["brankForm"].resetFields();
      }
    },
    // 分类级联
    handleCascaderChange(value) {
      let l = value.length;
      let cId = value[l - 1];
      this.brankForm.categoryId = cId + "";
    },
    //提交品牌表单
    formBrandSubmit() {
      let form = this.brankForm;
      delete form.categoryIds;

      this.$refs["brankForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        saveBrand(form).then(res => {
          if (res.status === 0) {
            this.dialogVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          } else {
            Message({
              message: res.msg ? res.msg : "保存失败",
              type: "warning",
              duration: 3 * 1000
            });
          }
        });
      });
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.response.data.showPath;
      this.uploadVisible = true;
    },
    successImg(response, file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1);
      }
      if (response.status == 0) {
        this.brankForm.logo = response.data.savePath;
        this.brankForm.logoShowPath = response.data.showPath;
      } else {
        comm.message(res.msg, "warning", this);
      }
    },
    handleRemove(file, fileList) {
      console.log(this.fileList);
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
      brandList(para).then(res => {
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