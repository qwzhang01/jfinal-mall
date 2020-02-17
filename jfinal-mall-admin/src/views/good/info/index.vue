<template>
  <el-card class="box-card">
    <el-col :span="24" class="toolbar" style="padding-bottom: 0px;">
      <el-form :inline="true" :model="search" @submit.native.prevent @keyup.enter.native="getList">
        <el-form-item>
          <el-input v-model="search.storeName" placeholder="店铺"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="search.goodName" placeholder="商品"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="search.brandName" placeholder="品牌"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="search.catName" placeholder="分类"></el-input>
        </el-form-item>

        <el-select v-model="search.saleStatus" placeholder="上架状态">
          <el-option
            v-for="item in saleStatus"
            :label="item.value"
            :value="item.key"
            :key="item.key"
          ></el-option>
        </el-select>

        <el-form-item>
          <el-button type="success" v-on:click="getList">搜索</el-button>
          <el-button type="primary" v-on:click="addGood">新增</el-button>
          <el-button type="warning" v-on:click="handlePutOffSale">批量下架</el-button>
          <el-button type="primary" v-on:click="downloadGood">导出</el-button>
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
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" min-width="20"></el-table-column>
        <el-table-column type="index" label="序号" width="50" align="center"></el-table-column>
        <template v-for="(val,i) in colomus">
          <el-table-column
            align="center"
            v-if="val.scope === 'img'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-image :src="scope.row.imgPath" fit="cover"></el-image>
            </template>
          </el-table-column>
          <el-table-column
            v-else-if="val.scope === 'cat'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>分类：{{scope.row.namePath}}</span>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>SPU：{{scope.row.goodSn}}</span>
                </el-col>
              </el-row>
            </template>
          </el-table-column>

          <el-table-column
            v-else-if="val.scope === 'price'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>市场价格：￥{{scope.row.marketPrice}}</span>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>销售价格：￥{{scope.row.shopPrice}}</span>
                </el-col>
              </el-row>
            </template>
          </el-table-column>

          <el-table-column
            v-else-if="val.scope === 'status'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-row :gutter="0">
                <el-col :span="24">
                  <!-- 0下架1上架2违规下架 -->
                  <span>上架：</span>
                  <el-switch
                    v-model="scope.row.saleStatus"
                    :active-value="1"
                    :nactive-value="0"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    @change="handleOnOffSale(scope.row)"
                  ></el-switch>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>推荐：</span>
                  <el-switch
                    v-model="scope.row.isRecommend"
                    :active-value="1"
                    :nactive-value="0"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    @change="handleRecommend(scope.row)"
                  ></el-switch>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>热卖：</span>
                  <el-switch
                    v-model="scope.row.is_hot"
                    :active-value="1"
                    :nactive-value="0"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    @change="handleHot(scope.row)"
                  ></el-switch>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <span>新品：</span>
                <el-switch
                  v-model="scope.row.is_new"
                  :active-value="1"
                  :nactive-value="0"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                  @change="handleNew(scope.row)"
                ></el-switch>
              </el-row>
            </template>
          </el-table-column>

          <el-table-column
            v-else-if="val.scope === 'point'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-row :gutter="0" v-if="scope.row.point_as_money > 0">
                <el-col :span="24">
                  <span>赠送：{{scope.row.point_as_money}}</span>
                </el-col>
              </el-row>
              <el-row :gutter="0">
                <span>奖励：</span>
                <el-switch
                  v-model="scope.row.is_earn_point"
                  :active-value="1"
                  :nactive-value="0"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                  @change="handleEarnPoint(scope.row)"
                ></el-switch>
              </el-row>
            </template>
          </el-table-column>
          <el-table-column
            v-else-if="val.scope === 'store'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-row :gutter="0">
                <el-col :span="24">
                  <span>店铺：{{scope.row.storeName}}</span>
                </el-col>
              </el-row>
              <el-row :gutter="0" v-if="scope.row.brandName">
                <el-col :span="24">
                  <span>品牌：{{scope.row.brandName}}</span>
                </el-col>
              </el-row>
            </template>
          </el-table-column>
          <el-table-column
            v-else-if="val.scope === 'sku'"
            :key="i"
            :width="val.width"
            :label="val.label"
          >
            <template slot-scope="scope">
              <el-badge :value="scope.row.skuCount" class="item">
                <el-button
                  type="primary"
                  icon="el-icon-edit"
                  circle
                  size="small"
                  @click="skuDetail(scope.$index, scope.row)"
                ></el-button>
              </el-badge>
            </template>
          </el-table-column>
          <el-table-column
            v-else
            :key="i"
            :prop="val.props"
            :label="val.label"
            :min-width="val.width"
            :formatter="val.formatter"
            :show-overflow-tooltip="false"
          ></el-table-column>
        </template>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
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
    <!-- sku list -->
    <el-dialog
      title="SKU列表"
      :visible.sync="skuListShow"
      width="60%"
      :modal="true"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :destroy-on-close="true"
    >
      <el-row :gutter="0">
        <el-col :span="24">
          <el-table
            :data="skuList"
            highlight-current-row
            v-loading="listLoading"
            style="width: 100%;"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="index" label="序号" width="50" align="center"></el-table-column>
            <el-table-column align="center" label="图片">
              <template slot-scope="scope">
                <img style="height: 80px;" :src="scope.row.spec_img" />
              </template>
            </el-table-column>
            <el-table-column
              v-for="(val, i) in skuColomus"
              :key="i"
              :prop="val.props"
              :label="val.label"
            ></el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-dialog>
  </el-card>
</template>
<script>
import util from "@/util/util";
import {
  goodList,
  skuPage,
  del,
  download,
  onOffSale,
  putOffSale,
  recommendSave,
  hotSave,
  newSave,
  earnPointSave
} from "@/api/good/good";
import { dict } from "@/api/system/param";
import Paging from "../../../components/paging";
import config from "./config.js";

export default {
  components: {
    Paging
  },
  data() {
    return {
      listLoading: false,
      page: config.page,
      list: [],
      selectedList: [],
      search: config.search,
      colomus: config.colomus,
      saleStatus: [],
      saleStatusArray: [],
      goodStateArray: ["待审核", "审核通过", "审核失败"],
      skuListShow: false,
      skuList: [],
      skuColomus: config.skuColomus
    };
  },
  mounted() {
    let _this = this;
    dict("sale_status").then(res => {
      _this.saleStatus = res.data;
      _this.saleStatusArray = res.data.map(r => r.value);
    });
    this.getList();
  },
  methods: {
    addGood() {
      this.$router.push("/good/form");
    },
    /**
     * 单个上架/下架
     */
    handleOnOffSale(row) {
      onOffSale({ id: row.goodId, isOnSale: row.saleStatus ? 1 : 0 }).then(
        res => {
          if (res.status === 0) {
            this.$message({
              message: "设置成功",
              type: "success",
              duration: 3 * 1000
            });
          }
        }
      );
    },
    handleRecommend(row) {
      recommendSave({
        id: row.goodId,
        isRecommend: row.isRecommend ? 1 : 0
      }).then(res => {
        if (res.status === 0) {
          this.$message({
            message: "设置成功",
            type: "success",
            duration: 3 * 1000
          });
        }
      });
    },
    handleHot(row) {
      hotSave({ id: row.goodId, is_hot: row.is_hot ? 1 : 0 }).then(res => {
        if (res.status === 0) {
          this.$message({
            message: "设置成功",
            type: "success",
            duration: 3 * 1000
          });
        }
      });
    },
    handleNew(row) {
      newSave({ id: row.goodId, is_new: row.is_new ? 1 : 0 }).then(res => {
        if (res.status === 0) {
          this.$message({
            message: "设置成功",
            type: "success",
            duration: 3 * 1000
          });
        }
      });
    },
    handleEarnPoint(row) {
      earnPointSave({
        id: row.goodId,
        is_earn_point: row.is_earn_point ? 1 : 0
      }).then(res => {
        if (res.status === 0) {
          this.$message({
            message: "设置成功",
            type: "success",
            duration: 3 * 1000
          });
        }
      });
    },
    /**
     * 批量下架
     */
    handlePutOffSale() {
      let _this = this;
      if (!this.selectedList || this.selectedList.length <= 0) {
        this.$message({
          message: "请至少选择一个操作",
          type: "error",
          duration: 3 * 1000
        });
        return;
      }
      this.$confirm("确定要批量下架勾选的商品信息吗？", "批量下架", {
        confirmButtonText: "确定下架",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        let goodIdArray = this.selectedList.map(g => g.goodId);
        putOffSale(goodIdArray).then(res => {
          if (res.status === 0) {
            this.$message({
              message: "下架成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          }
        });
      });
    },
    downloadGood() {
      let para = this.search;
      download(para);
    },
    skuDetail(index, row) {
      let _this = this;
      _this.skuList = [];
      skuPage(row.goodId).then(res => {
        if (res.status === 0) {
          _this.skuListShow = true;
          _this.skuList = res.data;
        }
      });
    },
    handleEdit(index, row) {
      this.$router.push({ path: "/good/form", query: { id: row.goodId } });
    },
    handleDelete(index, row) {
      let _this = this;
      this.$confirm('确定要删除"' + row.goodName + '"的商品信息吗', "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        del(row.goodId).then(res => {
          if (res.status === 0) {
            this.$message({
              message: "删除成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          }
        });
      });
    },
    handleSelectionChange(selection) {
      this.selectedList = selection;
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
<style scoped>
.item {
  margin-top: 10px;
  margin-right: 40px;
}
</style>