<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 步骤条 -->
    <Step :step="step" :storeType="2"/>
    <!-- 营业执照 -->
    <div class="licenseBox">
      <div class="title">请上传有效的营业执照</div>
      <div class="license">
        <van-uploader
          :after-read="upload"
          v-model="from.license.fileList"
          multiple
          name="license"
          :max-count="1"
          accept="image/*"
        >
          <img src="../../assets/license.png" alt="营业执照">
        </van-uploader>
        <div class="h1">点击上传营业执照</div>
      </div>
    </div>
    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 统一社会引用代码 -->
      <van-cell-group>
        <van-field
          v-model="from.code"
          clearable
          label="统一社会引用代码"
          type="text"
          @input="change"
          class="field"
          placeholder="请输入统一社会引用代码"
        />
      </van-cell-group>

      <!-- 营业期限 -->
      <van-cell-group>
        <van-field
          v-model="from.years"
          clearable
          label="营业期限"
          type="text"
          @click="show =true"
          readonly
          icon="arrow"
          placeholder="请输入营业期限"
        />
      </van-cell-group>

      <!-- 有效期至 -->
      <van-cell-group v-if="from.selectCertificates == 2">
        <yd-cell-item arrow>
          <div slot="left" class="time">有效期至</div>
          <yd-datetime
            type="date"
            v-model="from.time"
            @input="change"
            slot="right"
            :start-year="new Date().getFullYear()"
            :end-year="new Date().getFullYear() + 80"
          ></yd-datetime>
        </yd-cell-item>
      </van-cell-group>
    </div>
    <!-- 按钮 -->
    <van-button
      type="danger"
      size="large"
      :disabled="disabled"
      style="background:#D7000F;"
      @click.native="submit"
      class="submit"
    >下一步，认证银行卡</van-button>
    <!-- 弹窗 -->
    <yd-popup
      v-model="show"
      position="bottom"
      height="40%"
      class="PopupBox"
      :close-on-masker="false"
    >
      <div class="PopupTitle">
        <div class="del" @click="clean">取消</div>
        <div class="title">请选择证件期限</div>
      </div>
      <!-- 列表 -->
      <div class="storeTypeBox">
        <van-radio-group v-model="from.selectCertificates">
          <van-radio
            :name="item.id"
            checked-color="#D7000F"
            v-for="(item,key) in certificates"
            :key="key"
            @click="from.selectCertificates = item.id"
          >
            <div>{{item.name}}</div>
          </van-radio>
        </van-radio-group>
      </div>
    </yd-popup>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import { uploadImgBase64 } from "../../../api/index";
import Step from "../../components/step";
import { mapActions, mapGetters } from "vuex";
import commJs from "../../comm";
import vue from "Vue";
export default {
  components: {
    Navbar,
    Step
  },
  data() {
    return {
      disabled: true,
      show: false,
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
          coment: "营业执照",
          //颜色
          color: "#ffffff"
        },
        right: []
      },
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
          active: false,
          number: 3
        }
      ],
      // 证件期限
      certificates: [
        {
          name: "长期有效",
          id: 1
        },
        {
          name: "固定期限有效",
          id: 2
        }
      ],
      // 表单
      from: {
        code: "",
        license: {
          fileList: [],
          savePath: "",
          showPath: ""
        },
        selectCertificates: 1, // 默认为固定期限有效
        years: "",
        time: ""
      }
    };
  },
  methods: {
    ...mapActions(["StoreStep5"]),
    change() {
      if (
        this.from.code &&
        this.from.years &&
        this.from.license.fileList.length > 0
      ) {
        this.disabled = false;
        return true;
      } else {
        this.disabled = true;
        return false;
      }
    },
    // 弹窗取消
    clean() {
      this.show = false;
      // 获取选中的营业期限
      this.from.years = this.certificates[
        this.from.selectCertificates - 1
      ].name;
      this.change();
    },
    // 提交
    submit() {
      let that = this;
      if (!this.change()) {
        return;
      }
      if (commJs.test(this.from.code, "统一社会引用代码", [4], this) == false) {
        return;
      }
      this.StoreStep5(that.from);
      this.$router.push({
        path: "/step6?type=2"
      });
    },
    // 上传图片
    upload(file, detail) {
      let that = this;
      this.$dialog.loading.open("加载中....");
      commJs.compress(file, 3, 0, 0, 0.7, [], this).then(res => {
        if (res.length > 0) {
          uploadImgBase64(res[0]).then(data => {
            that.$dialog.loading.close();
            if (data.status == 0) {
              // 营业执照
              if (detail.name == "license") {
                that.from.license.savePath = data.data.savePath;
                that.from.license.showPath = data.data.showPath;
              }
              that.change();
            }
          });
        } else {
          that.$dialog.loading.close();
        }
      });
    },
  },
  computed: {
    ...mapGetters(["getStep"])
  },
  mounted() {
    this.from = Object.assign(this.from, this.getStep.step5);
    this.change();
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
.title {
  color: #333333;
  font-size: 0.875rem;
  font-family: PingFang SC;
  margin-bottom: 1.25rem;
  text-align: center;
  padding-top: 0.9375rem;
}
.licenseBox {
  background: #ffffff;
  padding-bottom: 0.9375rem;
  .license {
    width: 10.5rem;
    text-align: center;
    box-shadow: 0px 0px 3px #e6e6e6;
    border-radius: 0.3rem;
    padding: 0.9375rem 0;
    margin: 0 auto;
    img {
      width: 7.5rem;
      height: 4.75rem;
    }
    /deep/ .van-image {
      width: 7.5rem;
      height: 4.75rem;
    }
  }
  .h1 {
    margin-top: 0.625rem;
    font-family: PingFang SC;
  }
}
/deep/ .van-field__label {
  width: 8rem;
}
.field {
  /deep/ .van-field__control {
    padding-top: 0.2rem;
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
  margin-top: 5rem;
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
.time {
  width: 8.125rem;
  font-size: 0.875rem;
  color: #333333;
}
/deep/ .yd-cell-arrow:after {
  color: #999999;
  margin-right: 0.125rem;
}
/deep/ .van-uploader__preview {
  margin: 0;
}
/deep/ .yd-datetime-input {
  color: #333333;
}
</style>

