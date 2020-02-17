<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="search">
        <el-form-item>
          <el-input v-model="search.storeName" placeholder="店铺名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="search.storeStatus" placeholder="状态">
            <el-option label="待审核" value="2" key="2"></el-option>
            <el-option label="正常" value="1" key="1"></el-option>
            <el-option label="关闭" value="0" key="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="success" v-on:click="getList">查询</el-button>
        </el-form-item>
      </el-form>
    </el-col>

    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column width="120" align="center" label="Logo">
          <template slot-scope="scope">
            <el-image :src="scope.row.storeLogo" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column width="120" align="center" label="Banner">
          <template slot-scope="scope">
            <el-image :src="scope.row.store_banner" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :min-width="val.width"
          :formatter="val.fmtFn"
        ></el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button
              @click="handleAuth(scope.row)"
              type="warning"
              size="mini"
              v-if="scope.row.storeState == 2"
            >审核通过</el-button>
            <el-button
              @click="handleClose(scope.row)"
              type="danger"
              size="mini"
              v-if="scope.row.storeState == 1"
            >关闭</el-button>
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
  </el-card>
</template>
<script>
import util from "@/util/util";
import { pageAuth, authStore, closeStore } from "@/api/store/store";
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
        storeName: "",
        storeStatus: ""
      },
      storeStatus: ["关闭", "正常", "待审核"],
      colomus: [
        {
          label: "店铺名称",
          width: 100,
          props: "storeName"
        },
        {
          label: "店主",
          width: 100,
          props: "nickname"
        },
        {
          label: "店铺状态",
          width: 100,
          props: "storeState",
          fmtFn: column => {
            return this.storeStatus[column.storeState];
          }
        },
        {
          label: "开店时间",
          width: 100,
          props: "storeTime"
        }
      ]
    };
  },
  methods: {
    // 审核店铺
    handleAuth(row) {
      MessageBox.confirm(
        '确定要审核通过"' + row.storeName + '"店铺吗',
        "审核通过",
        {
          confirmButtonText: "审核通过",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        authStore(row.storeId).then(res => {
          Message({
            message: "审核通过",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    // 关闭店铺
    handleClose(row) {
      MessageBox.confirm(
        '确定要关闭"' + row.storeName + '"店铺吗',
        "关闭店铺",
        {
          confirmButtonText: "关闭店铺",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        closeStore(row.storeId).then(res => {
          Message({
            message: "关闭成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    // 获取分页列表数据
    getList() {
      this.listLoading = true;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        ...this.search
      };
      pageAuth(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
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