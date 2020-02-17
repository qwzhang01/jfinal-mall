<template>
  <div>
    <!-- 搜索 -->
    <div class="search_box">
      <Search :search="search" :searchFrom="searchFrom">
        <div class="operation">
          <el-button type="success" @click="searchFun">搜索</el-button>
          <el-button type="primary" @click="handleClose" v-if="spike.type === 1">添加商品</el-button>
        </div>
      </Search>
    </div>
    <!-- 秒杀标题 -->
    <div v-if="spike" class="spike">
      <h5>{{spike.name}}</h5>
      <div>每人限购：{{spike.buyLimit}}</div>
      <div>秒杀时间：{{spike.beginTime}} — {{spike.endTime}}</div>
    </div>
    <!-- 表格 -->
    <Table
      :table="table"
      :tableKey="tableKey"
      columId="flashId"
      @SelectionChange="SelectionChange"
      @sortFun="sortFun"
    >
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <div v-if="spike.type!==3">
            <el-button
              :class="[scope.row.offSaleStatus == '上架'?'bottom_doods':'']"
              type="warning"
              size="mini"
              @click="operation(scope.row, scope.$index)"
            >{{ scope.row.offSaleStatus == '上架' ? '下架' : '上架' }}</el-button>
            <el-button
              type="danger"
              size="mini"
              v-if="spike.type==1"
              @click="delGoodFun(scope.row, scope.$index)"
            >删除</el-button>
            <el-button type="success" size="mini" @click="handleClick(scope.row, scope.$index)">虚拟抢购</el-button>
          </div>
          <div v-else style="color:red;">活动已结束</div>
        </template>
      </el-table-column>
    </Table>
    <!-- 分页 -->
    <Paging
      :page="page"
      @handleCurrentChange="handleCurrentChange"
      @handleSizeChange="handleSizeChange"
    >
      <el-button type="danger" @click="batchRemove" v-if="spike.type!==3">批量下架</el-button>
    </Paging>

    <!-- 弹窗 -->
    <el-dialog
      title="添加商品"
      :visible.sync="dialogVisible"
      width="80%"
      :before-close="handleClose"
      :close-on-click-modal="false"
    >
      <div class="box">
        <div style="width:50%">
          <From
            :form="form"
            :formKey="formKey"
            :states="states"
            :submit="'创建商品'"
            :formName="'form'"
            :disabled="submit_disabled"
            @submit="onSubmit"
            @changeInputFun="changeInputs"
            @goods="GoodsDetailsFun"
          >
            <!-- 商品封面 -->
            <el-form-item label="商品封面" prop="goodsImg" :rules="rules.val">
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
              <el-dialog :visible.sync="dialogVisible2" size="tiny">
                <img width="100%" :src="form.goodsImg.showPath" alt />
              </el-dialog>
            </el-form-item>
          </From>
        </div>
        <div class="goods">
          <h2>商品详情</h2>
          <div class="box_goods" v-if="goods.goodId">
            <div class="goods_title">
              <div class="label title">商品名称：</div>
              <div>{{goods.name}}</div>
            </div>
            <div class="goodsImg">
              <div class="label">商品图片：</div>
              <div>
                <img :src="goods.imgPath" alt="商品图片" />
              </div>
            </div>

            <div v-for=" (item , key) in goods.goodSpecList" :key="key">
              <div class="label">{{item.specName}}</div>
              <div class="row_specList">
                <div v-for=" (value , k) in item.specList" :key="k">
                  <el-radio
                    v-model="form.specGoodPrice[key]"
                    :label="value.itemId"
                    :border="true"
                    size="medium"
                    @change="btn_GoodPrice"
                  >{{value.itemName}}</el-radio>
                </div>
              </div>
            </div>

            <div>
              <div class="label">
                库存：{{
                form.choice_Price.specConstituteStoreCount?
                form.choice_Price.specConstituteStoreCount :
                goods.goodStoreCount
                }}
              </div>
              <div class="label">
                价格：{{
                form.choice_Price.specConstitutePrice?
                form.choice_Price.specConstitutePrice:
                goods.goodPrice
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 设置虚拟数量 -->
    <el-dialog
      title="设置虚假抢购数量"
      :visible.sync="dialogVisible3"
      width="40%"
      :close-on-click-modal="false"
      :before-close="handleClose2"
    >
      <el-form ref="modify" :model="modify" :rules="rules" label-width="120px">
        <el-form-item label="虚假抢购数量" prop="fakeBuyNum" :rules="rules.InterNum2">
          <el-input v-model="modify.fakeBuyNum"></el-input>
        </el-form-item>
        <div style="text-align: center;">
          <el-button type="primary" @click="onSubmit_2('modify')">提交</el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import config from "./config.js";
import rules from "../../../comm/rules";
import comm from "../../../comm/";
import Search from "../../../components/search";
import From from "../../../components/from";
import Table from "../../../components/table2";
import Paging from "../../../components/paging";
import {
  GoodList,
  AddGood,
  GoodsDetails,
  SearchGoods,
  OnSale,
  OffSale,
  SetFakeBuyNum,
  OffSaleBatch,
  delGood,
  sort
} from "../../../api/prom/seckill";
import { UpdateFileFast } from "../../../api/comm.js";

export default {
  components: {
    Search,
    Table,
    Paging,
    From
  },
  data() {
    return {
      UpdateFileFast,
      spike: {},
      dialogVisible: false,
      dialogVisible3: false,
      show_type: true,
      dialogImageUrl: "",
      dialogVisible2: false,
      rules,
      // 搜索key
      search: config.activitySearchKey,
      // 搜索列表
      searchFrom: {
        goods: "",
        goodsActive: ""
      },
      // 表格
      table: [],
      //  表格key
      tableKey: config.activityTableKey,
      // 表单 key
      formKey: config.activityformKey,
      options4: "",
      list: [],
      loading: false,
      states: [],
      // 活动表单
      form: {
        // 商品
        goodsName: "",
        // 商品规格
        specGoodPrice: [],
        // 商品规格选择
        choice_Price: {},
        // 抢购价格
        rob_price: "",
        // 商品封面 -
        goodsImg: {
          savePath: "",
          showPath: ""
        },
        // 参加抢购数量
        rob_number: "",
        // 虚拟抢购数量
        fictitious_rob_number: "",
        // 限购数量
        limit_number: "",
        // 抢购设置 (1：开抢时禁用，2：开抢不禁用)
        isFake: "1"
      },

      // 商品详情
      goods: {
        goodId: "",
        name: "",
        imgPath: "",
        specGoodPrice: [],
        goodSpecList: []
      },
      // 批量下架数据
      lower: "",
      // 设置虚拟抢购数量
      modify: {
        fakeBuyNum: "",
        flashGoodId: "",
        index: ""
      },
      page: {
        // 页码
        pageNumber: 1,
        // 每页显示条数
        pageSize: 10,
        // 总页数
        totalPage: 1,
        // 总条数
        totalRow: 0
      },
      // 提交按钮 禁用状态
      submit_disabled: false
    };
  },

  methods: {
    // 排序
    sortFun(List) {
      let that = this;
      // 获取排列的索引ID
      let strId = List.strId;
      console.log(strId);
      // 更改表格
      sort({ sortId: strId }).then(res => {
        if (res.status == 0) {
          that.table = [];
          that.GoodListFun();
          comm.message(res.msg, "success", that);
        }
      });
    },
    // 批量删除
    batchRemove() {
      let that = this;
      console.log(this.lower);
      // 数据处理
      OffSaleBatch({ flashGoodId: this.lower }).then(res => {
        if (res.status == 0) {
          that.table = [];
          that.GoodListFun();
          comm.message(res.msg, "success", that);
        }
      });
    },
    // 删除
    delGoodFun(item, index) {
      let that = this;
      comm.message2("你确定删除此商品？", "提示", "warning", that).then(res => {
        if (res == "confirm") {
          delGood({ flashGoodId: item.flashId }).then(res => {
            if (res.status == 0) {
              that.table = [];
              that.GoodListFun();
              comm.message(res.msg, "success", that);
            } else {
              comm.message(res.msg, "warning", that);
            }
          });
        }
      });
    },
    // 设置虚拟请购数量
    SetFakeBuyNumFun() {
      let that = this;
      let data = {
        flashGoodId: this.modify.flashGoodId,
        fakeBuyNum: this.modify.fakeBuyNum
      };
      SetFakeBuyNum(data).then(res => {
        if (res.status == 0) {
          that.table[that.modify.index].fakeBuyNum = that.modify.fakeBuyNum;
          that.dialogVisible3 = !that.dialogVisible3;
          comm.message(res.msg, "success", that);
        }
      });
    },
    // 批量处理
    SelectionChange(item) {
      this.lower = item
        .map(item => {
          if (item.offSaleStatus === "上架") {
            return item.flashId;
          } else {
            comm.message("该商品处于下架状态，无需下架！", "warning", this);
          }
        })
        .toString();
    },
    // 弹窗
    handleClose() {
      // console.log('关闭弹窗');
      this.form.goodsName = "";
      this.form.rob_price = "";
      this.form.goodsImg.savePath = "";
      this.form.goodsImg.showPath = "";
      this.form.rob_number = "";
      this.form.fictitious_rob_number = "";
      this.form.limit_number = "";
      this.dialogVisible = !this.dialogVisible;
      this.clert();
      this.clearFiles();
      // this.show_type = true;
      // console.log(this.formName);
    },
    // 设置虚拟数量 弹窗关闭
    handleClose2() {
      this.modify.fakeBuyNum = "";
      this.modify.flashGoodId = "";
      this.modify.index = "";
      this.dialogVisible3 = !this.dialogVisible3;
    },
    // 设置虚拟数量
    onSubmit_2(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.SetFakeBuyNumFun(
            this.modify.flashGoodId,
            this.modify.fakeBuyNum
          );
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    // 提交
    onSubmit(item) {
      let that = this;
      // 判读是否是单规格
      if (this.form.specGoodPrice.length > 0) {
        // 查看是否选择规格
        if (this.form.choice_Price.specConstituteId) {
          // 多规格id
          var specId = this.form.choice_Price.specConstituteId;
          // 库存
          var goodStoreCount = this.form.choice_Price.specConstituteStoreCount;
        } else {
          comm.message("请选择商品规格", "warning", that);
          return;
        }
      } else {
        // 单规格
        var specId = "";
        // 库存
        var goodStoreCount = this.goods.goodStoreCount;
      }
      // 库存判断
      if (this.form.rob_number > goodStoreCount) {
        comm.message("参加抢购数量不能超过实际库存数量！", "warning", that);
        return;
      }

      // 虚拟抢购数量
      if (this.form.fictitious_rob_number > goodStoreCount) {
        comm.message("虚拟抢购数量不能超过实际库存数量！", "warning", that);
        return;
      }
      if (!this.form.goodsImg.savePath) {
        comm.message("请上传图片", "warning", that);
        return;
      }
      this.submit_disabled = true;
      let params = {
        flashId: this.spike.id,
        goodId: this.form.goodsName.value,
        specId,
        title: this.form.goodsName.label,
        price: this.form.rob_price,
        goodNum: this.form.rob_number,
        buyLimit: this.form.limit_number,
        imgPath: this.form.goodsImg.savePath,
        fakeBuyNum: this.form.fictitious_rob_number,
        isFake: this.form.isFake,
        point: this.form.point,
        pointLimit: this.form.pointLimit
      };

      AddGood(params).then(res => {
        // console.log(res);
        if (res.status == 0) {
          that.table = [];
          that.GoodListFun();
          // 清理
          that.handleClose();
        } else {
          comm.message(res.msg, "warning", that);
        }
        this.submit_disabled = false;
      });
    },
    // 监听
    changeInputs(item) {
      if (!item) {
        this.form.goodsName = "";
      }
      // 请求
      this.SearchGoodsFun(item);
    },
    // 获取产品详情
    GoodsDetailsFun(item) {
      if (!item) {
        this.form.goodsName = "";
      }

      let that = this;
      if (item) {
        let params = {
          goodId: item.value
        };
        GoodsDetails(params).then(res => {
          if (res.status == 0) {
            that.goods = Object.assign(that.goods, res.data);
            that.form.specGoodPrice = [];
            // 添加规格
            if (res.data.specGoodPrice.length === 1) {
              let spec = res.data.specGoodPrice[0];
              that.goods.goodStoreCount = spec.specConstituteStoreCount;
              that.goods.goodPrice = spec.specConstitutePrice;
            }
            if (res.data.goodSpecList.length > 0) {
              that.form.specGoodPrice = res.data.goodSpecList.map(s => "");
            }
          } else {
            comm.message(res.msg, "warning", that);
          }
        });
      }
    },
    // 查看
    handleClick(item, index) {
      console.log(item);
      this.dialogVisible3 = !this.dialogVisible3;
      this.modify.fakeBuyNum = item.fakeBuyNum;
      this.modify.flashGoodId = item.flashId;
      this.modify.index = index;
    },
    // 上下架
    operation(item, index) {
      if (item.offSaleStatus == "上架") {
        // 下架
        this.OffSaleFun(item.flashId, index);
      } else {
        // 上架
        this.OnSaleFun(item.flashId, index);
      }
    },
    // 下架
    OffSaleFun(flashId, index = null) {
      let that = this;
      OffSale({ flashGoodId: flashId }).then(res => {
        if (res.status == 0) {
          that.modifySale(index);
          comm.message(res.msg, "success", that);
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 上下架更改
    modifySale(index) {
      let that = this;
      if (that.table[index].offSaleStatus == "上架") {
        that.table[index].offSaleStatus = "下架";
      } else {
        that.table[index].offSaleStatus = "上架";
      }
    },
    // 上架
    OnSaleFun(flashGoodId, index) {
      let that = this;
      OnSale({ flashGoodId }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", that);
          that.modifySale(index);
        }
      });
    },

    //上传图片
    handleRemove(file, fileList) {
      console.log(this.fileList);
    },
    handlePictureCardPreview(file) {
      console.log(file);
      this.dialogImageUrl = file.response.data.showPath;
      this.dialogVisible2 = true;
    },
    // 清除
    clearFiles() {
      if (this.$refs["my_upload"] !== undefined) {
        this.$refs["my_upload"].clearFiles();
      }
    },
    successImg(response, file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1);
      }
      if (response.status == 0) {
        this.form.goodsImg.savePath = response.data.savePath;
        this.form.goodsImg.showPath = response.data.showPath;
      } else {
        comm.message(res.msg, "warning", this);
      }
    },

    // 清理商品
    clert() {
      this.goods.goodId = "";
      this.goods.name = "";
      this.goods.imgPath = "";
      this.goods.specGoodPrice = [];
      this.goods.goodSpecList = [];
      this.form.specGoodPrice = [];
      this.form.choice_Price = {};
    },
    // 商品搜索
    SearchGoodsFun(goodName = "") {
      let that = this;
      let params = {
        goodName
      };
      // 清理
      this.clert();
      SearchGoods(params).then(res => {
        if (res.status == 0) {
          var arr_data = res.data;
          that.states = arr_data.map(item => {
            return { value: item.goodId.toString(), label: item.name };
          });
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },
    // 选择规格
    btn_GoodPrice(itemId) {
      // console.log(this.form);
      let itemIdArray = Object.assign([], this.form.specGoodPrice);
      itemIdArray.sort();
      let GoodPriceId =
        itemIdArray.length > 1 ? itemIdArray.join("_") : itemIdArray;
      // console.log(GoodPriceId);
      // 在规格中找到指定的数据
      let choicedSku = this.goods.specGoodPrice.filter(
        sku => sku.specItemIdConstitute === GoodPriceId
      )[0];
      if (choicedSku) {
        this.form.choice_Price = choicedSku;
      }
    },
    // 搜索
    searchFun() {
      this.table = [];
      this.GoodListFun();
    },
    // 获取秒杀活动的商品列表
    GoodListFun() {
      let that = this;
      let params = {
        flashId: this.spike.id,
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        goodsName: this.searchFrom.goods
      };
      GoodList(params).then(res => {
        if (res.status == 0) {
          if (res.data.list) {
            res.data.list.forEach((value, index, array) => {
              value.offSaleStatus = value.offSaleStatus === 1 ? "上架" : "下架";
              value.isFake = value.isFake === 1 ? "真实" : "虚假";
              that.table.push(value);
            });
          }
          that.page.pageNumber = res.data.pageNumber;
          that.page.totalPage = res.data.totalPage;
          that.page.totalRow = res.data.totalRow;
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 前往第几页
    handleCurrentChange(val) {
      this.page.pageNumber = val;
      this.table = [];
      this.GoodListFun();
    },
    // 每页显示条数
    handleSizeChange(val) {
      this.page.pageSize = val;
      this.table = [];
      this.GoodListFun();
    }
  },
  mounted() {
    if (this.$route.query.data) {
      this.spike = JSON.parse(this.$route.query.data);
      this.GoodListFun();
    }
  }
};
</script>

<style lang="less" scoped>
h5 {
  margin-bottom: 8px;
}
.spike div {
  margin-bottom: 5px;
}
.search_box {
  align-items: center;
  padding: 5px 0;
  display: flex;
  background: #eef1f6;
}
.table_box {
  margin-top: 20px;
}

/deep/ .el-dialog--small {
  width: 80%;
}
.operation {
  margin-left: 25px;
}
.box {
  display: flex;
  width: 100%;
}
.goods {
  width: 50%;
  border-left: 1px solid #999999;
  padding: 10px 10px 10px 20px;

  div:nth-child(1) {
    margin-bottom: 30px;
  }
  h2 {
    margin-top: 0;
  }
  img {
    width: 150px;
    width: 150px;
  }
  .label {
    font-size: 14px;
    font-weight: bold;
  }
  .goodsImg {
    display: flex;
    text-align: center;
    div:nth-child(1) {
      margin-top: 50px;
    }
  }
}
.foot {
  background: #f2f2f2;
  padding: 10px 20px;
  margin-top: 20px;
}
.goods_title {
  display: flex;
  .title {
    width: 86px;
  }
}
.row_specList {
  display: flex;
  margin: 2px 0;
  margin-bottom: 0px;
  div {
    margin-right: 20px;
  }
}
.bottom_doods {
  color: red;
}
</style>
