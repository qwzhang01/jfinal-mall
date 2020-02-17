<template>
  <div>
    <!-- 搜索 -->
    <div class="search_box">
      <Search :search="search" :searchFrom="searchFrom">
        <div class="operation">
          <el-button type="success" @click="searchFun">搜索</el-button>
          <el-button type="primary" @click="handleClose" :disabled="spike.type==3">添加商品</el-button>
        </div>
      </Search>
    </div>
    <!-- 秒杀标题 -->
    <div v-if="spike" class="spike">
      <h5>{{spike.title}}</h5>
      <div>抢购时间：{{spike.startTime}} — {{spike.endTime}}</div>
    </div>
    <!-- 表格 -->
    <Table
      :table="table"
      :tableKey="tableKey"
      columId="lotteryGoodId"
      @SelectionChange="SelectionChange"
      @sortFun="sortFun"
    >
      <el-table-column :fixed="false" label="操作" width="250">
        <template slot-scope="scope">
          <div v-if="spike.type === 1">
            <el-button
              :class="[scope.row.saleStatus == '上架中'?'bottom_doods':'']"
              type="warning"
              size="mini"
              @click="operation(scope.row, scope.$index)"
            >{{ scope.row.saleStatus == '上架中' ? '下架' : '上架' }}</el-button>
            <el-button
              type="danger"
              size="mini"
              v-if="spike.type==1"
              @click="del(scope.row, scope.$index)"
            >删除</el-button>
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
      :close-on-click-modal="false"
      :before-close="handleClose"
    >
      <div class="box">
        <div style="width:50%">
          <From
            :form="form"
            :formKey="formKey"
            :states="states"
            :submit="'创建商品'"
            :formName="'formAddGoods'"
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
                    @click.native="btn_GoodPrice"
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
                form.choice_Price.specConstituteSupplyPrice?
                form.choice_Price.specConstituteSupplyPric:
                goods.goodPrice
                }}
              </div>
            </div>
          </div>
        </div>
      </div>
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
import { UpdateFileFast } from "../../../api/comm.js";
import {
  GoodList,
  AddGood,
  GoodsDetails,
  SearchGoods,
  OnSale,
  OffSale,
  OffSaleBatch,
  delGood,
  sort
} from "../../../api/prom/crowdRob.js";
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
      formKey: config.fromaddkey,
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
        lotteryGoodId: "",
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
    // 删除
    del(item, index) {
      let that = this;
      comm.message2("确定要删除此商品？", "提示", "warning", that).then(res => {
        if (res == "confirm") {
          delGood({ lotteryGoodId: item.lotteryGoodId }).then(res => {
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
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 批量删除
    batchRemove() {
      let that = this;
      console.log(this.lower);
      // 数据处理
      OffSaleBatch({ lotteryGoodId: this.lower }).then(res => {
        if (res.status == 0) {
          that.table = [];
          that.GoodListFun();
          comm.message(res.msg, "success", that);
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 批量处理
    SelectionChange(item) {
      this.lower = item
        .map(item => {
          if (item.sale == 2) {
            return item.lotteryGoodId;
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
    // 提交
    onSubmit(item) {
      console.log(this.form);
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
        lotteryId: this.spike.id,
        goodId: this.form.goodsName.value,
        specId,
        title: this.form.goodsName.label,
        price: this.form.rob_price,
        goodNum: this.form.rob_number,
        buyNum: this.form.limit_number,
        imgPath: this.form.goodsImg.savePath,
        fakeAttendCount: this.form.fictitious_rob_number
        // isFake:             this.form.isFake
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
    // 上下架
    operation(item, index) {
      if (item.sale == 2) {
        // 下架
        this.OffSaleFun(item.lotteryGoodId, index);
      } else {
        // 上架
        this.OnSaleFun(item.lotteryGoodId, index);
      }
    },
    // 下架
    OffSaleFun(lotteryGoodId, index = null) {
      let that = this;
      OffSale({ lotteryGoodId }).then(res => {
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
      if (that.table[index].sale == 4) {
        that.table[index].sale = 2;
        that.table[index].saleStatus = "上架中";
      } else {
        that.table[index].sale = 4;
        that.table[index].saleStatus = "下架中";
      }
    },
    // 上架
    OnSaleFun(lotteryGoodId, index) {
      let that = this;
      OnSale({ lotteryGoodId }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", that);
          that.modifySale(index);
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 搜索
    searchFun() {
      this.table = [];
      this.GoodListFun();
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
    // 获取秒杀活动的商品列表
    GoodListFun() {
      let that = this;
      let params = {
        lotteryId: this.spike.id,
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        goodsName: this.searchFrom.goods
      };
      GoodList(params).then(res => {
        //   console.log(res.data);
        if (res.status == 0) {
          if (res.data.list) {
            res.data.list.forEach((value, index, array) => {
              value.sale = value.status;
              value.saleStatus = value.status === 2 ? "上架中" : "下架中";
              value.goodsId = value.goodId;
              value.fakeBuyCount = value.fakeAttendCount;
              // 排序必加
              value.sortId = value.lotteryGoodId;
              value.goodNum = value.num;
              // value.isFake = value.isFake === 1?'非虚假':'虚假';
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
    btn_GoodPrice() {
      setTimeout(() => {
        let that = this;
        let GoodPriceId =
          this.form.specGoodPrice.length > 1
            ? this.form.specGoodPrice.join("_")
            : this.form.specGoodPrice;
        console.log(GoodPriceId);
        // 在规格中找到指定的数据
        this.goods.specGoodPrice.forEach((value, index, array) => {
          if (
            that.goods.specGoodPrice[index].specItemIdConstitute.toString() ===
            GoodPriceId.toString()
          ) {
            that.form.choice_Price = Object.assign(
              that.goods.specGoodPrice[index],
              that.form.choice_Price
            );
          }
        });
      }, 100);
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
      // console.log(this.spike);
      this.GoodListFun();
      this.SearchGoodsFun();
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
