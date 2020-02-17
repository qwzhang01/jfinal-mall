<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 步骤条 -->
    <Step :step="step" :storeType="storeType"/>
    <!-- 银行卡号 -->
    <van-cell-group>
      <van-field
        v-model="from.code"
        clearable
        label="银行卡号"
        type="text"
        @input="change"
        class="field"
        placeholder="请输入银行卡号"
      />
    </van-cell-group>
    <!-- 银行卡 -->
    <div class="cardBoxs">
      <div class="cardBox">
        <div class="card_title_1">请分别上传银行卡正面和背面照片</div>
        <div class="card_box_1">
          <div class="card_just">
            <div>
              <van-uploader
                :after-read="upload"
                v-model="from.bank_just.fileList"
                multiple
                name="bank_just"
                :max-count="1"
                accept="image/*"
              >
                <img src="../../assets/bank_1.png" alt="正面图片">
              </van-uploader>
            </div>
            <div class="h1">点击上传正面</div>
          </div>
          <div class="card_just">
            <div>
              <van-uploader
                :after-read="upload"
                v-model="from.bank_back.fileList"
                multiple
                name="bank_back"
                :max-count="1"
                accept="image/*"
              >
                <img src="../../assets/bank_2.png" alt="反面图片">
              </van-uploader>
            </div>
            <div class="h1">点击上传反面</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 注意事项 -->
    <div class="ps">
      <div class="title">银行卡照片要求</div>
      <div class="alert">
        请准备一张本人的银行卡，
        <span>用于验证身份</span>
      </div>
    </div>
    <!-- 按钮 -->
    <van-button
      type="danger"
      size="large"
      :disabled="disabled"
      style="background:#D7000F;"
      @click.native="submit"
      class="submit"
    >确认</van-button>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import Step from "../../components/step";
import { mapActions, mapGetters } from "vuex";
import { uploadImgBase64 } from "../../../api/index";
import { apply } from "../../../api/store/";
import commJs from "../../comm";
export default {
  components: {
    Navbar,
    Step
  },
  data() {
    return {
      disabled: true,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/step5",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "银行卡认证",
          //颜色
          color: "#ffffff"
        },
        right: []
      },
      // 店铺类型
      storeType: "",
      // 步骤条
      step: [
        {
          id: 1,
          name: "上传身份证",
          active: true,
          number: 1
        },
        {
          id: 2,
          name: "营业执照",
          active: true,
          number: 2
        },
        {
          id: 3,
          name: "银行卡认证",
          active: true,
          number: 3
        }
      ],
      from: {
        // 银行卡号
        code: "",
        // 正面
        bank_just: {
          fileList: [],
          savePath: "",
          showPath: ""
        },
        // 反面
        bank_back: {
          fileList: [],
          savePath: "",
          showPath: ""
        }
      }
    };
  },
  methods: {
    ...mapActions(["StoreStep6"]),
    // 提交
    submit() {
      let that = this;
      if (!this.change()) {
        return;
      }
      that.StoreStep6(that.from);
      if (commJs.test(this.from.code, "银行卡号", [1, 2], this) == false) {
        return;
      }
      if (16 <= !this.from.code.length && !this.from.code.length <= 30) {
        commJs.Dialog2("银行卡号长度不应为16 - 30位", "error", 1500, this);
        return;
      }
      let data = {
        // 店铺名称
        storeName: this.getStep.step2.storeName,
        // 店铺信息
        storeDesc: this.getStep.step2.storeDescribe,
        // 店铺头像
        storeAvatar: this.getStep.step2.savePath,
        // 1 个人店铺 2 企业店铺
        type: this.getStep.step3.storeType,
        // 所在区域
        regionId: this.getStep.step3.districtId,
        // 店铺地址
        address: this.getStep.step3.detailedAddress,
        // 经营类目ID，字符串，拼接
        categoryId: this.getStep.step3.category,
        // 身份证正面
        idFront: this.getStep.step4.card_just.savePath,
        // 身份证反面
        idBack: this.getStep.step4.card_back.savePath,
        // 手持身份证
        idHold: this.getStep.step4.hold_card.savePath,
        // 营业执照照片
        businessLicense: this.getStep.step5.license.savePath,
        // 营业执照统一社会编号
        unifiedSocialReferenceCode: this.getStep.step5.code,
        // 1 长期有效 2 固定期限
        validityPeriodType: this.getStep.step5.selectCertificates,
        // 营业执照结束有效期
        endValidityPeriod: this.getStep.step5.time,
        // 银行卡号
        cardNum: this.getStep.step6.code,
        // 银行卡正面照片
        cardFront: this.getStep.step6.bank_just.savePath,
        // 银行卡反面照片
        cardBack: this.getStep.step6.bank_back.savePath
      };

      apply(data).then(res => {
        if (res.status == 0) {
          commJs.Dialog2(res.msg, "success", 1500, that);
          setTimeout(() => {
            that.$router.push({
              path: "/step7"
            });
          }, 1600);
        } else {
          commJs.Dialog2(res.msg, "error", 1500, that);
        }
      });
    },
    // 上传图片
    upload(file, detail) {
      // console.log(detail);
      let that = this;
      this.$dialog.loading.open("加载中....");
      commJs.compress(file, 3, 0, 0, 0.7, [], this).then(res => {
        if (res.length > 0) {
          uploadImgBase64(res[0]).then(data => {
            that.$dialog.loading.close();
            if (data.status == 0) {
              // 正面
              if (detail.name == "bank_just") {
                that.from.bank_just.savePath = data.data.savePath;
                that.from.bank_just.showPath = data.data.showPath;
              }
              // 反面
              else if (detail.name == "bank_back") {
                that.from.bank_back.savePath = data.data.savePath;
                that.from.bank_back.showPath = data.data.showPath;
              }
              that.change();
            }
          });
        } else {
          that.$dialog.loading.close();
        }
      });
    },
    change() {
      if (
        this.from.code &&
        this.from.bank_just.fileList.length > 0 &&
        this.from.bank_back.fileList.length > 0
      ) {
        this.disabled = false;
        return true;
      } else {
        this.disabled = true;
        return false;
      }
    }
  },
  computed: {
    ...mapGetters(["getStep"])
  },
  mounted() {
    // 获取申请类型
    if (this.$route.query.type) {
      this.storeType = this.$route.query.type;
      this.from = Object.assign(this.from, this.getStep.step6);
      // console.log(this.from);
      this.change();
      // 个人
      if (this.storeType == 1) {
        this.step = this.step.filter(item => item.id !== 2);
        this.step[1].number = 2;
      }
      // 更改路由
      this.navbar.left[0].to =
        this.storeType == 1 ? "/step4?type=1" : "/step5?type=2";
    } else {
      this.$router.push({
        path: "/step1"
      });
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 导航条
/deep/ .yd-navbar {
  background: #d7000f !important;
}
/deep/ .yd-navbar:after {
  content: none;
}

// 身份证
.cardBoxs {
  margin-bottom: 0.9375rem;
  .card_title_1 {
    color: #333333;
    font-size: 0.875rem;
    text-align: center;
    font-family: PingFang SC;
    padding: 0.9375rem 0 1.25rem 0;
  }
  .cardBox {
    background: #ffffff;
    padding-bottom: 0.9375rem;
    margin-bottom: 0.9375rem;
  }
  .card_box_1 {
    display: flex;
    justify-content: space-between;
    padding: 0 0.9375rem;
    background: #ffffff;
    .card_just {
      background: #ffffff;
      padding-top: 0.8rem;
      width: 48%;
      height: 8.25rem;
      text-align: center;
      box-shadow: 0px 0px 4px #e6e6e6;
      border-radius: 0.625rem;
      img {
        width: 7.5rem;
        height: 4.8125rem;
      }
      /deep/ .van-image {
        width: 7.5rem;
        height: 4.8125rem;
      }
      .h1 {
        color: #333333;
        font-size: 0.75rem;
        margin-top: 0.5625rem;
      }
    }
  }
}
// 注意事项
.ps {
  background: #ffffff;
  .title {
    color: #333333;
    font-size: 0.8rem;
    text-align: center;
    padding-top: 0.9375rem;
  }
  .alert {
    text-align: center;
    font-size: 0.75rem;
    padding: 0.5rem 0 0.9375rem 0;
    color: #666666;
    span {
      color: #f5ab10 !important;
    }
  }
}
.from .van-button,
.submit {
  position: fixed;
  bottom: 0;
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
/deep/ .van-field__control {
  padding-top: 0.2rem;
}
/deep/ .van-uploader__preview {
  margin: 0;
}
</style>

