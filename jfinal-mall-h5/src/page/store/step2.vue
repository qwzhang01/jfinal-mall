<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 设置店铺头像 -->
    <div class="camera_box">
      <div class="camera">
        <div class="camera2">
          <img
            :src="from.showPath?from.showPath: require('../../assets/storeImg.png')"
            alt="店铺"
            class="Img"
            @click="lookImg"
          >
          <div class="cameraImg">
            <van-uploader
              :after-read="upload"
              name="uploader"
              accept="image/*"
              multiple
              :max-count="1"
            >
              <img src="../../assets/camera_1.png" alt="相机">
            </van-uploader>
          </div>
        </div>
        <div class="font">设置你的店铺头像</div>
      </div>
    </div>
    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 店铺名称 -->
      <van-cell-group>
        <van-field
          v-model="from.storeName"
          clearable
          label="店铺名称"
          type="text"
          @input="change"
          placeholder="请输店铺名称"
        />
      </van-cell-group>
      <!-- 店铺描述 -->
      <van-cell-group>
        <van-field
          v-model="from.storeDescribe"
          clearable
          label="店铺描述"
          type="text"
          @input="change"
          placeholder="请添加店铺描述"
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
  </div>
</template>
<script>
import Navbar from "../../components/navbar";
import comm from "../../comm/";
import { uploadImgBase64 } from "../../../api/";
import { mapActions, mapGetters } from "vuex";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 提交
      disabled: true,

      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/step1",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我要开店",
          //颜色
          color: "#333333"
        },
        right: []
      },
      from: {
        // 用于传输
        savePath: "",
        // 显示图片
        showPath: "",
        // 显示图片
        storeArr: [],
        // 店铺名称
        storeName: "",
        // 店铺描述
        storeDescribe: ""
      }
    };
  },
  methods: {
    ...mapActions(["addStore"]),
    // 提交
    submit() {
      let that = this;
      // 店铺名称
      if (comm.test(this.from.storeName, "店铺名称", [1], this) == false) {
        return;
      }
      // 判断店铺描述
      if (comm.test(this.from.storeDescribe, "店铺描述", [1], this) == false) {
        return;
      }
      // 店铺图片
      if (comm.test(this.from.showPath, "店铺图片", [], this) == false) {
        return;
      }
      // 存入缓存
      this.addStore(this.from);
      this.$router.push({
        path: "/step3"
      });
    },
    // 预览图片
    lookImg() {
      if (this.from.showPath) {
        comm.viewImg([this.from.showPath], 0);
      } else {
        comm.Dialog2("请上传图片", "error", 1500, this);
      }
    },
    // 上传图片
    upload(file) {
      let that = this;
      this.$dialog.loading.open("加载中....");
      comm.compress(file, 3, 0, 0, 0.7, this.from.storeArr, this).then(res => {
        if (res.length > 0) {
          uploadImgBase64(res[0]).then(data => {
            that.$dialog.loading.close();
            if (data.status == 0) {
              that.from.savePath = data.data.savePath;
              that.from.showPath = data.data.showPath;
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
        this.from.storeName &&
        this.from.storeDescribe &&
        this.from.showPath
      ) {
        this.disabled = false;
      } else {
        this.disabled = true;
      }
    }
  },
  computed: {
    ...mapGetters(["getStep"])
  },
  mounted() {
    this.from = Object.assign(this.from, this.getStep.step2);
    this.change();
    // router.beforeEach((to, from, next) => {

    // })
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
  padding: 0rem 0.9375rem 0 0.9375rem;
  background: #ffffff;
  height: -webkit-fill-available;
}
// 图片上传
.camera_box {
  height: 10.625rem;
  width: 100%;

  padding-top: 2.5rem;
  .camera {
    text-align: center;
    .camera2 {
      position: relative;
      width: 3.5rem;
      height: 3.5rem;
      margin: 0 auto 0.1rem auto;
      .Img {
        width: 3.5rem;
        height: 3.5rem;
        border-radius: 50%;
        box-shadow: 0px 0px 2px #a5a2a2;
      }
      .cameraImg {
        position: absolute;
        width: 1.75rem;
        height: 1.75rem;
        right: -1rem;
        bottom: 0.1rem;
        box-shadow: 0px 0px 2px #a5a2a2;
        border-radius: 50%;
        img {
          width: 1.75rem;
          height: 1.75rem;
        }
      }
    }

    .font {
      color: #000000;
      font-size: 0.875rem;
      font-family: PingFang SC;
    }
  }
}
/deep/ .van-field__label {
  max-width: 4.375rem;
}
</style>

