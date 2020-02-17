<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 步骤条 -->
    <Step :step="step" :storeType="storeType"/>
    <!-- 身份证 -->
    <div class="cardBoxs">
      <div class="cardBox">
        <div class="card_title_1">请分别上传身份证正面和背面照片</div>
        <div class="card_box_1">
          <div class="card_just">
            <div>
              <van-uploader
                :after-read="upload"
                v-model="from.card_just.fileList"
                multiple
                name="card_just"
                :max-count="1"
                accept="image/*"
              >
                <img src="../../assets/card_1.png" alt="正面图片">
              </van-uploader>
            </div>
            <div class="h1">点击上传正面</div>
          </div>
          <div class="card_just">
            <div>
              <van-uploader
                :after-read="upload"
                v-model="from.card_back.fileList"
                multiple
                name="card_back"
                :max-count="1"
                accept="image/*"
              >
                <img src="../../assets/card_2.png" alt="反面图片">
              </van-uploader>
            </div>
            <div class="h1">点击上传反面</div>
          </div>
        </div>
      </div>

      <!-- 手持 -->
      <div class="hold">
        <div class="title">请上传手持身份证照片</div>
        <div class="hold_card">
          <div>
            <van-uploader
              :after-read="upload"
              v-model="from.hold_card.fileList"
              multiple
              name="hold_card"
              :max-count="1"
              accept="image/*"
            >
              <img src="../../assets/card_3.png" alt="正面图片">
            </van-uploader>
          </div>
          <div class="h1">点击上传照片</div>
        </div>
      </div>
    </div>
    <!-- 注意事项 -->
    <div class="ps">
      <div class="title">身份证照片要求</div>
      <div class="alert">
        拍摄时请确保身份证
        <span>边框完整，字体清晰，亮度均匀</span>
      </div>
      <div class="standardBox">
        <div class="standard_box" v-for="(item,key) in alertBox" :key="key">
          <div class="standard">
            <img :src="item.img" alt="照片">
            <van-icon :name="item.icon" :color="item.icon_color" class="icon" size="1rem"/>
          </div>
          <div class="title_font">{{item.font}}</div>
        </div>
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
    >下一步，认证{{storeType==1?'银行卡':'营业执照'}}</van-button>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import Step from "../../components/step";
import { mapActions, mapGetters } from "vuex";
import { uploadImgBase64 } from "../../../api/index";
import commJs from "../../comm";
export default {
  components: {
    Navbar,
    Step
  },
  data() {
    return {
      // from
      from: {
        // 正面
        card_just: {
          fileList: [],
          savePath: "",
          showPath: ""
        },
        // 反面
        card_back: {
          fileList: [],
          savePath: "",
          showPath: ""
        },
        // 手持
        hold_card: {
          fileList: [],
          savePath: "",
          showPath: ""
        }
      },
      disabled: true,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/step3",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "身份认证",
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
          active: false,
          number: 2
        },
        {
          id: 3,
          name: "银行卡认证",
          active: false,
          number: 3
        }
      ],
      // 提示
      alertBox: [
        {
          img: require("../../assets/card_4.png"),
          icon: "checked",
          icon_color: "#2d8bff",
          font: "标准拍摄"
        },
        {
          img: require("../../assets/card_5.png"),
          icon: "clear",
          icon_color: "#d7000f",
          font: "边框缺失"
        },
        {
          img: require("../../assets/card_6.png"),
          icon: "clear",
          icon_color: "#d7000f",
          font: "照片模糊"
        },
        {
          img: require("../../assets/card_7.png"),
          icon: "clear",
          icon_color: "#d7000f",
          font: "闪光强烈"
        }
      ]
    };
  },
  methods: {
    ...mapActions(["StoreStep4"]),
    // 提交
    submit() {
      if (!this.change()) {
        return;
      }
      let that = this;
      this.StoreStep4(this.from);
      // 1：是个人 2：企业
      this.$router.push({
        path: this.storeType == 1 ? "/step6?type=1" : "/step5"
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
              if (detail.name == "card_just") {
                that.from.card_just.savePath = data.data.savePath;
                that.from.card_just.showPath = data.data.showPath;
              }
              // 反面
              else if (detail.name == "card_back") {
                that.from.card_back.savePath = data.data.savePath;
                that.from.card_back.showPath = data.data.showPath;
              }
              // 手持
              else if (detail.name == "hold_card") {
                that.from.hold_card.savePath = data.data.savePath;
                that.from.hold_card.showPath = data.data.showPath;
              }
              // console.log(that.from);
              that.change();
            }
          });
        } else {
          that.$dialog.loading.close();
        }
      });
    },
    // 监听
    change() {
      if (
        this.from.card_just.fileList.length > 0 &&
        this.from.card_back.fileList.length > 0 &&
        this.from.hold_card.fileList.length > 0
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
      this.from = Object.assign(this.from, this.getStep.step4);
      this.storeType = this.$route.query.type;
      // 个人
      if (this.storeType == 1) {
        this.step = this.step.filter(item => item.id !== 2);
        this.step[1].number = 2;
      }
      this.change();
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
  .hold {
    background: #ffffff;
    padding-bottom: 0.9375rem;
    .title {
      color: #333333;
      font-size: 0.875rem;
      font-family: PingFang SC;
      margin-bottom: 1.25rem;
      text-align: center;
      padding-top: 0.9375rem;
    }
    .hold_card {
      margin: 0 auto;
      background: #ffffff;
      border-radius: 0.625rem;
      padding-top: 1rem;
      width: 10.5rem;
      height: 8.25rem;
      text-align: center;
      box-shadow: 0px 0px 4px #e6e6e6;
      img {
        width: 7.375rem;
        height: 4.8125rem;
      }
      /deep/ .van-image {
        width: 7.5rem;
        height: 4.8125rem;
      }
    }
    .h1 {
      color: #333333;
      font-size: 0.75rem;
      margin-top: 0.5625rem;
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
    margin: 0.5rem 0 0.6875rem 0;
    color: #666666;
    span {
      color: #f5ab10 !important;
    }
  }
  .standardBox {
    display: flex;
    justify-content: center;
    .standard_box {
      width: 24%;
      .standard {
        position: relative;
        width: 100%;
        height: 4rem;
        img {
          width: 100%;
          height: 4rem;
        }
      }
      .icon {
        position: absolute;
        bottom: -0.2rem;
        right: 40%;
      }
    }
  }
  .title_font {
    color: #333333;
    font-size: 0.75rem;
    text-align: center;
    padding: 0.3rem 0;
  }
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
/deep/ .van-uploader__preview {
  margin: 0;
}
</style>

