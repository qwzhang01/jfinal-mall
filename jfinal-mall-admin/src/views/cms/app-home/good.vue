<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.goodName" placeholder="商品"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" v-on:click="getList">搜索</el-button>
            <el-button type="primary" v-on:click="addGood">添加商品</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </div>

    <div>
      <img :src="specShow.img">
    </div>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
        <el-table-column width="120" align="center">
          <template slot-scope="scope">
            <img style="height: 80px;" :src="scope.row.imgPath">
          </template>
        </el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
          :show-overflow-tooltip="false"
        ></el-table-column>
         <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button @click="delGood(scope.row)" type="danger" size="mini">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <Paging
        :page="page"
        @handleCurrentChange="handleCurrentChange"
        @handleSizeChange="handleSizeChange"
      ></Paging>

      <el-dialog
        title="添加商品"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :visible.sync="dialogVisible"
        width="30%"
      >
        <el-form :model="setForm" :rules="rules" ref="setForm" label-width="100px">
          <el-form-item label="商品" prop="goodId" :rules="rules.val">
            <el-select
              v-model="setForm.goodId"
              filterable
              remote
              :remote-method="getGood"
              placeholder="请选择商品"
            >
              <el-option
                v-for="item in goodList"
                :key="item.goodId"
                :label="item.name"
                :value="item.goodId"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="setFormSubmit()">确 定</el-button>
        </span>
      </el-dialog>
    </div>
  </section>
</template>
<script>
import util from "@/util/util";
import { goodList, goodSearch, saveGood, del } from "@/api/CMS/appSet";
import { dict } from "@/api/system/param";
import { Message, MessageBox } from "element-ui";
import Paging from "../../../components/paging";
import rules from "@/comm/rules";

export default {
  components: {
    Paging
  },
  data() {
    return {
      rules,
      listLoading: false,
      dialogVisible: false,
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
        goodName: ""
      },
      colomus: [
        {
          label: "商品名称",
          width: 300,
          props: "goodName"
        },
        {
          label: "商品编号",
          width: 100,
          props: "goodSn"
        },
        {
          label: "销售价格",
          width: 100,
          props: "price"
        }
      ],
      setForm: {
        goodId: ""
      },
      goodList: [],
      specShow: {}
    };
  },
  mounted() {
    if (this.$route.query.data) {
      this.specShow = JSON.parse(this.$route.query.data);
      this.getList();
    }
  },
  methods: {
    addGood() {
      this.dialogVisible = true;
      if (this.$refs["setForm"] !== undefined) {
        this.$refs["setForm"].resetFields();
      }
    },
    getGood(query) {
      let _this = this;
      _this.goodList = [];
      goodSearch({ goodName: query }).then(res => {
        if (res.status === 0) {
          let goodId = _this.setForm.goodId;
          if (goodId) {
            _this.goodList = res.data.filter(item => {
              return item.goodId != goodId;
            });
          } else {
            _this.goodList = res.data;
          }
        }
      });
    },
    setFormSubmit() {
      this.$refs["setForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        this.dialogVisible = false;
        this.setForm.parentId = this.specShow.id;
        this.setForm.type = 6;
        this.setForm.gotoType = 2;
        this.setForm.img = this.specShow.img;
        this.setForm.gotoId = this.setForm.goodId;
        saveGood(this.setForm).then(res => {
          if (res.status == 0) {
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
          } else {
            Message({
              message: res.msg ? res.msg : "保存失败",
              type: "error",
              duration: 3 * 1000
            });
          }
          this.getList();
        });
      });
    },
     // 删除
    delGood(row) {
      MessageBox.confirm("确定要删除吗", "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        del(row.id).then(res => {
          if (res.status === 0) {
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
    getList() {
      this.listLoading = true;
      let date = this.search.rangeDate;
      let para = {
        parentId: this.specShow.id,
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };
      //NProgress.start();
      goodList(para).then(res => {
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