<template>
  <el-card class="box-card">
    <el-form :model="goodForm" :rules="rules" ref="goodForm" label-width="150px" size="medium">
      <div class="formTitle">
        <span>&nbsp;</span>
        <span>基本信息</span>
      </div>
      <el-form-item label="所属店铺：" prop="storeId" :rules="rules.val">
        <el-select v-model="goodForm.storeId" placeholder="请选择商品所属店铺">
          <el-option v-for="item in storeList" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="商品名称：" prop="goodsName" :rules="rules.val">
        <el-input
          type="input"
          v-model="goodForm.goodsName"
          placeholder="请输入商品名称"
          style="width: 220px;"
        ></el-input>
      </el-form-item>
      <el-form-item label="商品分类：" prop="catId" :rules="rules.val">
        <el-cascader
          v-model="goodForm.catId"
          :options="goodCat"
          :props="goodCatProp"
          placeholder="请选择商品分类"
          @change="handleCategorySelected"
        ></el-cascader>
      </el-form-item>
      <!-- <el-form-item label="商品品牌：" prop="brandId">
        <el-select v-model="goodForm.brandId" placeholder="请选择商品品牌" filterable>
          <el-option v-for="item in brandSelect" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
      </el-form-item>-->
      <el-form-item label="简单描述：" prop="goodsRemark" :rules="rules.val">
        <el-input type="input" v-model="goodForm.goodsRemark" placeholder="请输入简单描述"></el-input>
      </el-form-item>
      <el-form-item label="关键字：" prop="keywords" :rules="rules.val">
        <el-input type="input" v-model="goodForm.keywords" placeholder="请输入关键字，并用‘，’隔开"></el-input>
      </el-form-item>
      <el-form-item label="上门服务：" prop="doorServiceStatus" :rules="rules.val">
        <el-radio-group v-model="goodForm.doorServiceStatus">
          <el-radio
            v-for="item in doorServiceStatusDict"
            :label="parseInt(item.key)"
            :key="parseInt(item.key)"
          >{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="排序：" prop="sort" :rules="rules.val">
        <el-input type="input" v-model="goodForm.sort" placeholder="请输入排序"></el-input>
      </el-form-item>

      <div class="formTitle">
        <span>&nbsp;</span>
        <span>商品图片</span>
      </div>
      <el-form-item label="商品主图：" prop="originalImg" :rules="rules.val">
        <el-upload
          class="good-uploader"
          :action="UpdateFileFast"
          :show-file-list="false"
          :on-success="handleGoodImgSuccess"
          :before-upload="beforeGoodImgUpload"
        >
          <img v-if="goodForm.originalImgShow" :src="goodForm.originalImgShow" class="good" />
          <i v-else class="el-icon-plus good-uploader-icon good"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="商品画廊：" prop="imgAlbum" :rules="rules.val">
        <el-upload
          :action="UpdateFileFast"
          :on-success="handleAlbumSuccess"
          :file-list="goodForm.imgShowAlbum"
          list-type="picture-card"
        >
          <i class="el-icon-plus"></i>
        </el-upload>
      </el-form-item>
      <div class="formTitle">
        <span>&nbsp;</span>
        <span>促销信息</span>
      </div>
      <el-form-item label="上架时间：" prop="isOnSale" :rules="rules.val">
        <el-radio-group v-model="goodForm.isOnSale">
          <el-radio :label="1">立即上架</el-radio>
          <el-radio :label="-1">
            自定义上架时间
            <el-date-picker v-model="goodForm.onSaleTime" type="datetime" placeholder="请选择上架时间"></el-date-picker>
          </el-radio>
          <el-radio :label="0">暂不售卖，放入仓库</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="推荐商品：" prop="isRecommend" :rules="rules.val">
        <el-radio-group v-model="goodForm.isRecommend">
          <el-radio
            v-for="item in isOrNotdict"
            :label="parseInt(item.key)"
            :key="parseInt(item.key)"
          >{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="热卖商品：" prop="isHot" :rules="rules.val">
        <el-radio-group v-model="goodForm.isHot">
          <el-radio
            v-for="item in isOrNotdict"
            :label="parseInt(item.key)"
            :key="parseInt(item.key)"
          >{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="是否包邮" prop="isFreeShipping" :rules="rules.val">
        <el-radio-group v-model="goodForm.isFreeShipping">
          <el-radio
            v-for="item in isOrNotdict"
            :label="parseInt(item.key)"
            :key="parseInt(item.key)"
          >{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="积分抵扣金额：" prop="pointAsMoney" :rules="rules.val">
        <el-input
          type="number"
          v-model="goodForm.pointAsMoney"
          placeholder="积分抵扣金额"
          style="width: 220px;"
        ></el-input>
      </el-form-item>
      <el-form-item label="是否可以赚取积分：" prop="isEarnPoint" :rules="rules.val">
        <el-radio-group v-model="goodForm.isEarnPoint">
          <el-radio
            v-for="item in isOrNotdict"
            :label="parseInt(item.key)"
            :key="parseInt(item.key)"
          >{{item.value}}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="购买赠送积分：" prop="giveIntegral" :rules="rules.val">
        <el-input
          type="number"
          v-model="goodForm.giveIntegral"
          placeholder="购买赠送积分"
          style="width: 220px;"
        ></el-input>
      </el-form-item>

      <div class="formTitle">
        <span>&nbsp;</span>
        <span>属性信息</span>
      </div>

      <el-form-item label="商品规格：">
        <el-row v-for="(spec, index) in specList" :key="index">
          <el-col :span="4">
            <label>{{spec.name}}</label>
          </el-col>
          <el-col :span="20">
            <el-checkbox-group v-model="goodForm.skuItemList" @change="checkSkuItem">
              <el-checkbox
                v-for="(specItem, index) in spec.item"
                :label="specItem.id"
                :key="index"
              >{{specItem.item}}</el-checkbox>
            </el-checkbox-group>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item label="规格明细：">
        <el-table :data="goodForm.skuData" style="width: 100%">
          <el-table-column type="index" label="序号" width="80" align="center"></el-table-column>
          <el-table-column
            v-for="(c, i) in goodForm.skuColunm"
            :key="i"
            :prop="c.props"
            :label="c.label"
          />
          <el-table-column label="商品图片">
            <template slot-scope="scope">
              <el-upload
                class="good-uploader"
                :action="UpdateFileFast"
                :show-file-list="false"
                :on-success="handleSkuImgSuccess"
                :before-upload="beforeGoodImgUpload"
                :disabled="!scope.row.disAbled"
                :data="{key: scope.row.skuComputerCode}"
              >
                <img v-if="scope.row.showImgPath" :src="scope.row.showImgPath" class="good" />
                <i v-else class="el-icon-plus good-uploader-icon good"></i>
              </el-upload>
            </template>
          </el-table-column>
          <el-table-column label="销售价格">
            <template slot-scope="scope">
              <el-input type="input" v-model="scope.row.shopPrice" :disabled="!scope.row.disAbled"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="市场价格">
            <template slot-scope="scope">
              <el-input
                type="input"
                v-model="scope.row.marketPrice"
                :disabled="!scope.row.disAbled"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column label="进货价格">
            <template slot-scope="scope">
              <el-input
                type="input"
                v-model="scope.row.supplyPrice"
                :disabled="!scope.row.disAbled"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column label="库存">
            <template slot-scope="scope">
              <el-input type="input" v-model="scope.row.storeCount" :disabled="!scope.row.disAbled"></el-input>
            </template>
          </el-table-column>
          <el-table-column label="是否启用">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.disAbled"
                active-color="#13ce66"
                inactive-color="#ff4949"
              ></el-switch>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
      <el-form-item label="库存警告：" prop="storeCountWorn" :rules="rules.val">
        <el-input type="input" v-model="goodForm.storeCountWorn" style="width: 220px;"></el-input>
      </el-form-item>
      <div class="formTitle">
        <span>&nbsp;</span>
        <span>商品详情</span>
      </div>
      <el-form-item label="商品详情：" prop="detailSaveImgList" :rules="rules.val">
        <el-upload
          class="good-uploader"
          :action="UpdateFileFast"
          :file-list="goodForm.detailShowImgList"
          :on-success="handleGoodDetailSuccess"
          list-type="picture"
        >
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
        </el-upload>
      </el-form-item>
    </el-form>
    <div>
      <el-button @click="handleCancel()">重 置</el-button>
      <el-button type="primary" @click="handleSubmit()" :disabled="subDisabled">保 存</el-button>
    </div>
  </el-card>
</template>
<script>
import util from "@/util/util";
import config from "./formConfig.js";
import { save, detail } from "@/api/good/good";
import { select } from "@/api/store/store";
import { tree as goodCategory } from "@/api/good/category";
import { UpdateFileFast } from "@/api/comm.js";
import { dict, basic } from "@/api/system/param.js";
import { brandSelect } from "@/api/good/brand";
import { select as specSelect } from "@/api/good/spec.js";
import { Message, MessageBox } from "element-ui";

import rules from "@/comm/rules";

export default {
  data() {
    return {
      subDisabled: false, // 保存按钮是否可用
      rules,
      goodForm: config.goodForm,
      goodCat: [],
      storeList: [],
      goodCatProp: config.goodCatProp,
      brandSelect: [],
      UpdateFileFast,
      doorServiceStatusDict: [],
      isOrNotdict: [],
      specList: [
        {
          id: "",
          name: "",
          item: [
            {
              id: "",
              item: ""
            }
          ]
        }
      ]
    };
  },
  mounted() {
    this.getCategory();
    this.getStoreList();
    this.getDoorServiceStatus();
    this.getIsOrNotdict();
    this.specList = [];
    this.goodForm.skuData = [config.skuDataKey];
    let _this = this;
    this.$store.dispatch("initVue").then(() => {
      _this.getDetail();
    });
  },
  methods: {
    getStoreList() {
      let _this = this;
      select().then(res => {
        _this.storeList = res.data;
      });
    },
    /**
     * 获取分类树形数据
     */
    getCategory() {
      let _this = this;
      goodCategory().then(res => {
        _this.goodCat = res.data;
      });
    },
    /**
     * 获取上门服务状态配置信息
     */
    getDoorServiceStatus() {
      let _this = this;
      dict("doorServiceStatus").then(res => {
        _this.doorServiceStatusDict = res.data;
      });
    },
    /**
     * 获取是否状态配置信息
     */
    getIsOrNotdict() {
      let _this = this;
      dict("isOrNot").then(res => {
        _this.isOrNotdict = res.data;
      });
    },
    getDetail() {
      let _this = this;
      let id = _this.$route.query.id;
      if (id) {
        detail(id).then(res => {
          _this.goodForm = res.data;
          _this.echoDetail();
        });
      }
    },
    /**
     * 回显表单
     */
    echoDetail() {
      let _this = this;
      // 1.计算所有的规格项ID
      let skuData = _this.goodForm.skuData;
      let skuItemIdAll = skuData.map(s => s.skuComputerCode);
      _this.goodForm.skuItemList = [];
      skuItemIdAll.forEach(s => {
        let tmpItemIdArray = s.split("_");
        if (tmpItemIdArray && tmpItemIdArray.length > 0) {
          tmpItemIdArray.forEach(i => {
            i = parseInt(i);
            if (i && _this.goodForm.skuItemList.indexOf(i) === -1) {
              _this.goodForm.skuItemList.push(i);
            }
          });
        }
      });
      let catIdArray = _this.goodForm.catId;
      // 2.获取分类对应的规格数据
      _this.handleCategorySelected(catIdArray, () => {
        // 回显规格
        // 计算所有的列字段
        _this.goodForm.skuColunm = [];
        let checkedItem = _this.goodForm.skuItemList;
        let spec = _this.specList;
        let itemArray = new Array();
        spec.forEach(s => {
          itemArray = itemArray.concat(s.item);
        });
        _this.goodForm.skuItemObjList = itemArray.filter(
          i => checkedItem.indexOf(i.id) > -1
        );
        // 数组分组，转换为对象，健是规格的id，值是属性的数组
        _this.goodForm.skuItemObjList.forEach(sku => {
          // 构建列表的列
          let colunm = {
            label: sku.specName,
            width: 100,
            props: sku.specId + ""
          };
          // 添加列
          _this.goodForm.skuColunm.push(colunm);
          _this.goodForm.skuData
            .filter(
              d =>
                d.skuComputerCode.indexOf(sku.id + "_") !== -1 ||
                d.skuComputerCode.indexOf("_" + sku.id) !== -1 ||
                parseInt(d.skuComputerCode) === parseInt(sku.id)
            )
            .forEach(d => {
              d[sku.specId + ""] = sku.item;
            });
          // 列去重
          let obj = {};
          _this.goodForm.skuColunm = _this.goodForm.skuColunm.reduce(
            (item, next) => {
              obj[next.props]
                ? ""
                : (obj[next.props] = true && item.push(next));
              return item;
            },
            []
          );
        });
      });

      // 3.回显图片详情
      // 画廊回显
      let fileHost = this.$store.getters.getParams.fileHost;

      _this.goodForm.imgShowAlbum = [];
      _this.goodForm.imgAlbum.forEach(url => {
        _this.goodForm.imgShowAlbum.push({
          url: fileHost + url,
          response: {
            data: {
              savePath: url
            }
          }
        });
      });
      // 详情回显
      _this.goodForm.detailShowImgList = [];
      _this.goodForm.detailSaveImgList.forEach(url => {
        _this.goodForm.detailShowImgList.push({
          url: fileHost + url,
          response: {
            data: {
              savePath: url
            }
          }
        });
      });
    },
    /**
     * 选择商品分类后对应的逻辑
     */
    handleCategorySelected(value, fn = null) {
      if (!Array.isArray(value)) {
        value = [value];
      }
      let _this = this;
      // 1根据商品分类 获取商品品牌
      brandSelect(value[0]).then(res => {
        _this.brandSelect = res.data;
      });
      // 2根据商品分类，获取商品规格
      let l = value.length;
      let cId = value[l - 1];
      specSelect(cId).then(res => {
        _this.specList = res.data.filter(
          res => res.item && res.item.length > 0
        );
        // 编辑回显的时候用
        if (fn) {
          fn();
        }
      });
      // 3清空已经选中的规格
      if (!fn) {
        this.goodForm.skuColunm = [];
        this.goodForm.skuItemList = [];
        this.goodForm.skuItemObjList = [];
        this.goodForm.cartesianProduct = [];
      }
    },
    /**
     * 商品主图上传前的校验.
     */
    beforeGoodImgUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传商品图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传商品图片大小不能超过 2MB!");
      }
      return isJPG && isLt2M;
    },
    /**
     * 商品主图上传成功后的逻辑
     */
    handleGoodImgSuccess(res, file) {
      this.goodForm.originalImgShow = URL.createObjectURL(file.raw);
      this.goodForm.originalImg = res.data.savePath;
    },
    // 商品画廊上传
    handleAlbumSuccess(res, file, fileList) {
      this.goodForm.imgShowAlbum = fileList;
      this.goodForm.imgAlbum = fileList.map(f => f.response.data.savePath);
    },
    /**
     * 选中商品规格项后触发的事件-规格对应笛卡尔的sku -商品管理里面最复杂的部分哦
     */
    checkSkuItem() {
      let _this = this;
      _this.goodForm.skuColunm = [];
      let checkedItem = this.goodForm.skuItemList;
      let spec = this.specList;
      let itemArray = new Array();
      spec.forEach(s => {
        itemArray = itemArray.concat(s.item);
      });
      _this.goodForm.skuItemObjList = itemArray.filter(
        i => checkedItem.indexOf(i.id) > -1
      );
      // 数组分组，转换为对象，健是规格的id，值是属性的数组
      let mapObj = {};
      _this.goodForm.skuItemObjList.forEach(sku => {
        if (mapObj[sku.specId]) {
          mapObj[sku.specId].push(sku);
        } else {
          // 构建列表的列
          let colunm = {
            label: sku.specName,
            width: 300,
            props: sku.specId + ""
          };
          // 添加列
          _this.goodForm.skuColunm.push(colunm);
          // 添加对应的数据
          mapObj[sku.specId] = new Array();
          mapObj[sku.specId].push(sku);
        }
      });
      // 转换成二维数组,计算笛卡尔积
      let mapArray = [];
      let index = 0;
      for (var key in mapObj) {
        mapArray[index] = mapObj[key];
        index++;
      }
      // 清空不用的数据
      config.skuDataKey.showImgPath = "";
      config.skuDataKey.saveImgPath = "";

      _this.goodForm.cartesianProduct = util.cartesian(mapArray);
      // 转换成数据列，显示规格笛卡尔积以后的列表
      _this.goodForm.skuData = this.goodForm.cartesianProduct.map(c => {
        c = Array.isArray(c) ? c : [c];
        let map = JSON.parse(JSON.stringify(config.skuDataKey));
        // 获取显示用的，取规格名字以及规格名字对应的值
        c.forEach(i => {
          map[i.specId + ""] = i.item;
        });
        // 获取规格item对应的iD，全部取出来
        let itemIdArray = c.map(i => i.id);
        itemIdArray = itemIdArray.sort();
        map.skuComputerCode = itemIdArray.join("_");
        return map;
      });
    },
    handleSkuImgSuccess(res, file) {
      let showPath = res.data.showPath;
      let savePath = res.data.savePath;
      let skuComputerCode = res.data.key;
      if (skuComputerCode) {
        this.goodForm.skuData.forEach(s => {
          if (skuComputerCode == s.skuComputerCode) {
            s.saveImgPath = savePath;
            s.showImgPath = showPath;
          }
        });
      } else {
        this.goodForm.skuData[0].saveImgPath = savePath;
        this.goodForm.skuData[0].showImgPath = showPath;
      }
    },
    handleGoodDetailSuccess(res, file, fileList) {
      this.goodForm.detailShowImgList = fileList;
      this.goodForm.detailSaveImgList = fileList.map(
        f => f.response.data.savePath
      );
    },
    handleCancel() {
      if (this.$refs["goodForm"] !== undefined) {
        this.$refs["goodForm"].resetFields();
      }
    },
    handleSubmit() {
      this.$refs["goodForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        this.subDisabled = false;
        let form = this.goodForm;
        delete form.skuColunm;
        delete form.cartesianProduct;
        delete form.skuItemList;
        delete form.skuItemObjList;
        save(form).then(res => {
          if (res.status === 0) {
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.$router.push("/good/info");
          }
        });
      });
    }
  }
};
</script>
<style scoped lang="scss">
.formTitle {
  display: block;
  background-color: #f4f5f6;
  height: 30px;
  margin-bottom: 1%;
  padding-left: 1%;
  padding-top: 10px;
  span:nth-child(2) {
    color: #313233;
  }
  span:nth-child(1) {
    background-color: aqua;
  }
}
.good-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.good-uploader .el-upload:hover {
  border-color: #409eff;
}
.good-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  line-height: 148px;
  text-align: center;
}
.good {
  border: 1px dashed #c0ccda !important;
  width: 148px;
  height: 148px;
  display: block;
  background-color: #fbfdff;
}
</style>