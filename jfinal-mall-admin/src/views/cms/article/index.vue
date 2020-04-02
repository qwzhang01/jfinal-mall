<template>
  <section class="app-container">
    <div class="filter-container">
      <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
        <el-form :inline="true" :model="search">
          <el-form-item>
            <el-input v-model="search.title" placeholder="资讯标题"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="success" v-on:click="getList">搜索</el-button>
            <el-button type="primary" v-on:click="handleAddArticle">新增资讯</el-button>
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
        <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
        <el-table-column width="120" align="center">
          <template slot-scope="scope">
            <el-image :src="scope.row.thumb" fit="cover"></el-image>
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
              type="success"
              @click="handleEditArticle(scope.$index, scope.row)"
              v-if="checkPermission('资讯管理-查看')"
            >编辑</el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
              v-if="checkPermission('资讯管理-查看')"
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
  </section>
</template>
<script>
import util from "@/util/util";
import { articleList, saveArticle, deleteArticle } from "@/api/article";
import Paging from "../../../components/paging";
import { Message, MessageBox } from "element-ui";
import { checkPermission } from "@/util/operator";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      page: {
        pageNumber: 1,
        pageSize: 10,
        totalPage: 1,
        totalRow: 0
      },
      list: [],
      search: {
        title: ""
      },
      saleStatus: [],
      colomus: [
        {
          label: "标题",
          width: 300,
          props: "title"
        },
        {
          label: "分类",
          width: 100,
          props: "catName"
        },
        {
          label: "作者",
          width: 100,
          props: "author"
        },
        {
          label: "新增时间",
          width: 200,
          props: "addTime"
        },
        {
          label: "关键字",
          width: 200,
          props: "keyword"
        },
        {
          label: "浏览量",
          width: 200,
          props: "click"
        }
      ]
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    // 新增资讯
    handleAddArticle() {
      this.$router.push("/cms/article/form");
    },
    handleEditArticle(index, row) {
      this.$router.push({
        path: "/cms/article/form",
        query: {
          articleId: row.id
        }
      });
    },
    checkPermission,
    // 删除资讯
    handleDelete(index, row) {
      MessageBox.confirm('确定要删除资讯"' + row.title + '"吗', "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        deleteArticle(row.id).then(res => {
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
    getList() {
      this.listLoading = true;
      let date = this.search.rangeDate;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };
      articleList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
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