<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 店铺类型 -->
      <van-cell-group>
        <van-field
          v-model="from.storeTypeName"
          clearable
          label="店铺类型"
          type="text"
          @input="change"
          readonly
          icon="arrow"
          placeholder="请选择店铺类型"
          @click="showPopup(1)"
        />
      </van-cell-group>
      <!-- 主营类目 -->
      <van-cell-group>
        <van-field
          v-model="from.categoryDetails"
          clearable
          readonly
          label="主营类目"
          type="text"
          @input="change"
          icon="arrow"
          placeholder="请添加主营类目"
          @click="showPopup(2)"
        />
      </van-cell-group>
      <!-- 经营地址 -->
      <van-cell-group>
        <van-field
          v-model="from.ausinessAddress"
          clearable
          readonly
          label="经营地址"
          type="text"
          @input="change"
          icon="arrow"
          placeholder="请选择经营地址"
          @click="showPopup(3)"
        />
      </van-cell-group>
      <!-- 详细地址 -->
      <van-cell-group>
        <van-field
          v-model="from.detailedAddress"
          clearable
          label="详细地址"
          type="text"
          @input="change"
          placeholder="请输入详细地址"
        />
      </van-cell-group>

      <!-- 按钮 -->
      <van-button
        type="danger"
        size="large"
        :disabled="disabled"
        style="background:#D7000F;"
        @click.native="submit"
        class="submit"
      >确定</van-button>
    </div>

    <!-- 弹窗 -->
    <van-popup v-model="show" position="bottom" :style="{ height: '60%' }" @closed="clean">
      <div class="PopupTitle">
        <div class="del" @click="clean">取消</div>
        <div class="title">{{mun==1?'请选择店铺类型':'请选择经营类目'}}</div>
      </div>
      <!-- 店铺类型 -->
      <div class="storeTypeBox" v-if="mun == 1">
        <van-radio-group v-model="selectStoreType">
          <van-radio
            :name="item.id"
            checked-color="#D7000F"
            v-for="(item,key) in storeType"
            :key="key"
            @click="from.storeTypeName = item.name;from.storeType = item.id"
          >
            <div class="storeType">
              <div class="typeName">{{item.name}}</div>
              <div class="ps">{{item.ps}}</div>
            </div>
          </van-radio>
        </van-radio-group>
      </div>
      <!-- 请选择经营类目 -->
      <div v-if="mun == 2" class="Category">
        <van-collapse v-model="selectCategory" accordion @change="changeCollapse">
          <van-collapse-item
            :title="items.name"
            :name="items.id"
            :icon="items.id === selectCategory ? require('../../assets/select.png'): require('../../assets/no_select.png')"
            size="1rem"
            v-for="(items,key) in categoryArr"
            :key="key"
          >
            <!-- 单选 -->
            <div
              :class="[selectCategoryChildren == item.id?'activeChildren':'','CategoryChildren']"
              v-for="(item,key) in items.children"
              :key="key"
              @click="selectRadio(key)"
            >
              <div>{{item.name}}</div>
              <div v-if="selectCategoryChildren == item.id">
                <img src="../../assets/gou.png" alt="选择" class="choice">
              </div>
            </div>
          </van-collapse-item>
        </van-collapse>
      </div>
    </van-popup>

    <!-- 弹窗地址 -->
    <yd-popup v-model="show2" position="right" width="70%" :close-on-masker="false">
      <!-- 关闭 -->
      <yd-cell-item>
        <div slot="right">
          <van-icon name="close" size="1rem" @click.native="show2 = false"/>
        </div>
      </yd-cell-item>
      <!-- 地址列表 -->
      <City :list_arr="list_arr" @addressFun="addressFun" class="City"/>
    </yd-popup>
  </div>
</template>
<script>
import Navbar from "../../components/navbar";
import comm from "../../comm/";
import City from "../../components/city";
import { Regin } from "../../../api/";
import { parentTreeFun } from "../../../api/store/";
import { mapActions, mapGetters } from "vuex";
// 暂存区
let detailed = "";
export default {
  components: {
    Navbar,
    City
  },
  data() {
    return {
      parentId: 0,
      // 提交
      disabled: true,
      // 弹窗
      show: false,
      show2: false,
      list_arr: [],
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/step2",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "提交认证",
          //颜色
          color: "#333333"
        },
        right: []
      },
      mun: "",
      // 店铺类型
      selectStoreType: "",
      storeType: [
        {
          id: 1,
          name: "个人店铺",
          ps: "店铺认证需要提供身份证信息"
        },
        {
          id: 2,
          name: "企业店铺",
          ps: "店铺认证需要提供身份证、营业执照等信息"
        }
      ],
      // 选中的主营分类
      selectCategory: "",
      // 选中的主营分类的子级
      selectCategoryChildren: "",
      // 主营类目
      categoryArr: [],
      from: {
        // 店铺类型
        storeType: "",
        storeTypeName: "",
        // 主营类目
        category: "",
        // 主营类目详情
        categoryDetails: "",
        // 经营地址
        ausinessAddress: "",
        //地址Id
        districtId: "",
        // 详细地址
        detailedAddress: ""
      }
    };
  },
  methods: {
    ...mapActions(["AttestationStore"]),
    // 提交
    submit() {
      let that = this;
      this.AttestationStore(this.from);
      // 1：是个人 2：企业
      this.$router.push({
        path: "/step4?type=" + this.from.storeType
      });
    },
    // 监听
    change() {
      if (
        this.from.storeType &&
        this.from.category &&
        this.from.ausinessAddress &&
        this.from.districtId &&
        this.from.detailedAddress
      ) {
        this.disabled = false;
      } else {
        this.disabled = true;
      }
    },
    // 弹窗
    showPopup(mun) {
      this.mun = mun;
      // 店铺类型
      if (mun == 1) {
        this.show = true;
      }
      // 经营类目
      else if (mun == 2) {
        this.show = true;
      } else if (mun == 3) {
        this.showFun();
      }
    },
    // 清除
    clean() {
      // 检查主营类目
      if (this.mun == 2) {
        if (!this.selectCategory) {
          this.show = true;
          comm.Dialog2("请选择一项经营类目", "error", 1500, this);
          return;
        }
      }

      this.show = false;
    },
    // 监听 折叠面板
    changeCollapse(key) {
      // 更改子级状态
      this.selectCategoryChildren = 0;
      if (this.categoryArr.length > 0) {
        this.categoryArr.forEach((item, i, arr) => {
          if (item.id == key) {
            this.modifyCollapse(item);
          }
        });
      }
    },
    // 单选
    selectRadio(key) {
      this.categoryArr.forEach((data, i, arr) => {
        if (data.id == this.selectCategory) {
          this.modifyCollapse(data, key);
        }
      });
    },
    // 改变经营类目
    modifyCollapse(item, key = 0) {
      // 父级
      this.selectCategory = item.id;
      // 子级
      this.selectCategoryChildren =
        item.children.length > 0 ? item.children[key].id : 0;
      this.from.category = `${this.selectCategory},${
        this.selectCategoryChildren
      }`;
      this.from.categoryDetails = `${item.name} ${
        item.children.length > 0 ? item.children[key].name : ""
      }`;
    },
    // 开启地址弹窗
    showFun() {
      this.show2 = true;
      this.list_arr = [];
      this.parentId = 0;
      this.ReginFun();
      detailed = "";
    },
    // 获取省市区
    ReginFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("user_id"),
        parentId: this.parentId
      };
      Regin(params).then(res => {
        if (res.status == 0) {
          that.list_arr = [];
          that.list_arr = res.data;
        } else {
          that.show2 = false;
        }
      });
    },
    // 选择地址
    addressFun(item) {
      // console.log(item);
      this.parentId = item.id;
      detailed += item.name + " ";
      this.ReginFun();
      setTimeout(() => {
        if (!this.show) {
          // 改变地址
          this.from.ausinessAddress = detailed;
          this.from.districtId = item.id;
        }
      }, 500);
      // console.log(detailed);
    },
    // 经营类目
    ParentTreeFun() {
      let that = this;
      parentTreeFun().then(res => {
        if (res.status == 0) {
          that.categoryArr = res.data;
        } else {
          console.log(res.msg);
        }
      });
    }
  },
  computed: {
    ...mapGetters(["getStep"])
  },
  mounted() {
    this.from = Object.assign(this.from, this.getStep.step3);
    this.ReginFun();
    this.change();
    this.ParentTreeFun();
  }
};
</script>

<style lang="less" scoped>
/deep/ .van-cell-group ::-webkit-input-placeholder {
  color: #999999;
  font-family: PingFang SC;
}

/deep/ .van-cell-group {
  color: #333333;
  font-family: PingFang SC;
  font-size: 1rem;
  border-bottom: 1px solid #ebedf0;
}
#group {
  background: #ffffff;
}
/deep/ .van-button::before {
  content: none;
}
/deep/ .van-button--default {
  border: none;
}
/deep/ .van-cell {
  padding: 15px 0px;
  line-height: inherit;
}

[class*="van-hairline"]::after {
  content: none;
}

.from .van-button,
.submit {
  height: 2.8125rem;
  font-size: 1rem;
  font-family: PingFang SC;
  border-radius: 0.25rem;
}
/deep/.van-button--small {
  height: 0;
}
.submit {
  margin-top: 2.375rem;
}
.fromSubmint {
  padding: 2.8125rem 0.9375rem 0 0.9375rem;
  background: #ffffff;
  height: -webkit-fill-available;
}
/deep/ .van-field__label {
  max-width: 4.375rem;
}
// 弹窗
.PopupBox {
  position: relative;
}
.PopupTitle {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 3.3125rem;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 0.9375rem;
  .title {
    color: #333333;
    font-size: 1rem;
    font-family: PingFang SC;
  }
  .del {
    position: absolute;
    left: 0.9375rem;
    color: #999999;
    font-size: 0.875rem;
    font-family: PingFang SC;
  }
}
.storeType {
  margin-left: 0.625rem;
  .typeName {
    color: #333333;
    font-size: 0.875rem;
    margin-bottom: 0.2rem;
  }
  .ps {
    color: #999999;
    font-size: 0.75rem;
  }
}
.storeTypeBox {
  /deep/ .van-radio {
    border-bottom: 1px solid #e6e6ee;
    margin-left: 0.9375rem;
    padding: 0.625rem 0;
  }
  /deep/ .van-radio__label {
    width: 88%;
  }
}
.Category {
  /deep/.van-cell {
    padding: 10px 15px;
  }
  /deep/ .van-cell__title {
    font-size: 1rem;
  }
  /deep/ .van-collapse-item__content {
    padding: 0;
  }

  .CategoryChildren {
    display: flex;
    justify-content: space-between;
    padding: 0 0.9375rem;
    height: 2rem;
    align-items: center;
    font-size: 0.75rem;
    .choice {
      width: 0.8rem;
      height: 0.5rem;
    }
  }
  .activeChildren {
    background: #fcf4f4;
    color: #d7000f;
  }
}
.City {
  /deep/ .van-cell__title {
    padding-left: 0.9375rem;
  }
}
</style>

