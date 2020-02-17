<template>
  <section class="app-container">
    <!-- 分类树 -->
    <div class="custom-tree-container">
      <div class="block">
        <el-tree
          :data="treeDate"
          :show-checkbox="false"
          node-key="id"
          :props="props"
          highlight-current
          :expand-on-click-node="false"
          :default-expand-all="false"
          :render-content="renderContent"
          accordion
          @node-click="handleNodeClick"
        ></el-tree>
      </div>
    </div>
    <!--  表单表单 -->
    <div class="form-container">
      <div class="block">
        <el-form :model="categoryForm" :rules="rules" ref="categoryForm" label-width="100px">
          <el-form-item label="分类名称" prop="mobileName" :rules="rules.val">
            <el-input type="input" v-model="categoryForm.mobileName"></el-input>
          </el-form-item>
          <el-form-item label="上级分类" prop="parentId">
            <el-cascader
              v-model="categoryForm.categoryIds"
              :options="parentTreeData"
              :show-all-levels="true"
              :props="{value: 'id', label: 'name', checkStrictly : true, multiple: false, expandTrigger: 'hover'}"
              @change="handleCascaderChange"
              clearable
            ></el-cascader>
          </el-form-item>
          <el-form-item label="是否显示" prop="isShow" :rules="rules.val">
            <el-radio v-model="categoryForm.isShow" label="1" border>显示</el-radio>
            <el-radio v-model="categoryForm.isShow" label="0" border>不显示</el-radio>
          </el-form-item>
          <el-form-item label="是否热卖" prop="isHot" :rules="rules.val">
            <el-radio v-model="categoryForm.isHot" label="1" border>热卖</el-radio>
            <el-radio v-model="categoryForm.isHot" label="0" border>不热卖</el-radio>
          </el-form-item>
          <el-form-item label="分类ICON" prop="icon" :rules="rules.val">
            <el-upload
              :action="UpdateFileFast"
              list-type="picture-card"
              ref="my_upload"
              :on-preview="handlePictureCardPreview"
              :on-success="successImgIcon"
              :on-remove="handleRemove"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <el-dialog :visible.sync="uploadVisible" size="tiny">
              <img width="100%" :src="categoryForm.iconShowPath" alt />
            </el-dialog>
          </el-form-item>
          <el-form-item label="广告图" prop="image">
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
              <img width="100%" :src="categoryForm.imageShowPath" alt />
            </el-dialog>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="categoryFormCancel()">取 消</el-button>
          <el-button type="primary" @click="categoryFormSubmit()">确 定</el-button>
        </span>
      </div>
    </div>
  </section>
</template>
<script>
import {
  tree,
  parentTree,
  deleteCategory,
  detail,
  saveCategory
} from "@/api/good/category";
import rules from "@/comm/rules";
import { UpdateFileFast } from "../../../api/comm.js";
import util from "@/util/util";
import { Message, MessageBox } from "element-ui";

export default {
  components: {},
  data() {
    return {
      uploadVisible: false,
      UpdateFileFast,
      rules,
      categoryForm: {
        id: "",
        mobileName: "",
        parentId: "0",
        categoryIds: [],
        image: "",
        imageShowPath: "",
        icon: "",
        iconShowPath: "",
        isShow: "1",
        isHot: "0"
      },
      // 树形结构数据
      treeDate: [],
      parentTreeData: [],
      // 树形结构配置信息
      props: {
        label: "name",
        children: "children"
      }
    };
  },
  mounted() {
    let _this = this;
    parentTree().then(res => {
      _this.parentTreeData = res.data;
    });
    _this.getTree();
  },
  methods: {
    // 渲染按钮
    renderContent(h, { node, data, store }) {
      let customTreeNode = {
        "padding-right": "8px"
      };
      let customTreeNodeBtn = {
        float: "right"
      };
      return (
        <span>
          <span style={customTreeNode}>{node.label}</span>
          {node.isLeaf ? (
            <span style={customTreeNodeBtn}>
              <el-button
                size="mini"
                type="danger"
                on-click={() => this.remove(node, data)}
              >
                删除
              </el-button>
            </span>
          ) : (
            ""
          )}
        </span>
      );
    },
    handleNodeClick(data) {
      let _this = this;
      let id = data.id;
      detail(id).then(res => {
        _this.categoryForm = util.intToStr(res.data);
      });
    },
    categoryFormCancel() {
      if (this.$refs["categoryForm"] !== undefined) {
        this.$refs["categoryForm"].resetFields();
      }
    },
    categoryFormSubmit() {
      let form = this.categoryForm;
      delete form.categoryIds;

      this.$refs["categoryForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        saveCategory(form).then(res => {
          if (res.status === 0) {
            this.dialogVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getTree();
            this.categoryFormCancel();
          }
        });
      });
    },
    // 移除一个节点
    remove(node, data) {
      let id = data.id;
      deleteCategory(id).then(res => {
        if (res.status == 0) {
          Message({
            message: "删除成功",
            type: "success",
            duration: 3 * 1000
          });
          const parent = node.parent;
          const children = parent.data.children || parent.data;
          const index = children.findIndex(d => d.id === data.id);
          children.splice(index, 1);
        }
      });
    },
    // 分类级联
    handleCascaderChange(value) {
      let l = value.length;
      let cId = value[l - 1];
      this.categoryForm.parentId = cId;
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.response.data.showPath;
      this.uploadVisible = true;
    },
    successImgIcon(response, file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1);
      }
      if (response.status == 0) {
        this.categoryForm.icon = response.data.savePath;
        this.categoryForm.iconShowPath = response.data.showPath;
      } else {
        comm.message(res.msg, "warning", this);
      }
    },
    successImg(response, file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1);
      }
      if (response.status == 0) {
        this.categoryForm.image = response.data.savePath;
        this.categoryForm.imageShowPath = response.data.showPath;
      } else {
        comm.message(res.msg, "warning", this);
      }
    },
    handleRemove(file, fileList) {
      console.log(this.fileList);
    },
    // 获取分类树数据
    getTree() {
      let _this = this;
      tree().then(res => {
        _this.treeDate = res.data;
      });
    }
  }
};
</script>
<style lang="less" scoped>
.custom-tree-container {
  border: 1px solid #d1dbe5;
  width: 25%;
  float: left;
}
.form-container {
  margin-left: 5%;
  margin-right: 5%;
  padding: 3% 3% 10% 2%;
  border: 1px solid #d1dbe5;
  width: 50%;
  float: left;
}
</style>
