<template>
  <div>
    <!-- 头部 -->
    <Navbar :navbar="navbar" @navRight="Preservation" />
    <!-- 表单 -->
    <yd-cell-group>
      <yd-cell-item arrow class="headIcon_div">
        <span slot="left">头像</span>
        <span slot="right">
          <van-uploader :after-read="upload" name="uploader" accept="image/*">
            <img :src="from.head_pic" alt="头像" class="headIcon" />
          </van-uploader>
        </span>
      </yd-cell-item>
      <!-- <yd-cell-item>
        <span slot="left">等级</span>
        <span slot="right">{{from.levelName}}</span>
      </yd-cell-item>-->
      <yd-cell-item>
        <span slot="left">昵称</span>
        <span slot="right">{{from.nickname}}</span>
      </yd-cell-item>
      <yd-cell-item>
        <span slot="left">性别</span>
        <span slot="right">
          {{
          from.sex == 0?'保密':
          from.sex == 1?'男':
          '女'
          }}
        </span>
      </yd-cell-item>
      <yd-cell-item>
        <span slot="left">出生日期</span>
        <span slot="right">{{from.birthday}}</span>
      </yd-cell-item>
      <yd-cell-item>
        <span slot="left">手机</span>
        <span slot="right">{{from.mobile}}</span>
      </yd-cell-item>
      <!-- <yd-cell-item arrow>
            <span slot="left">邮箱</span>
            <span slot="right">{{from.email?from.email:'无'}}</span>
      </yd-cell-item>-->
      <yd-cell-item arrow>
        <span slot="left">所在地区</span>
        <span slot="right" @click="showFun">{{from.detailed}}</span>
      </yd-cell-item>
    </yd-cell-group>

    <!-- 弹窗 -->
    <yd-popup v-model="show" position="right" width="70%">
      <!-- 关闭 -->
      <yd-cell-item>
        <div slot="right">
          <van-icon name="close" size="1rem" @click.native="show = false" />
        </div>
      </yd-cell-item>
      <!-- 地址列表 -->
      <City :list_arr="list_arr" @addressFun="addressFun" />
    </yd-popup>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import City from "../../components/city";
import comm_js from "../../comm/";
import { uploadImgBase64, UserDetail, UserDdit, Regin } from "../../../api/";
// 暂存区
let detailed = "";
export default {
  components: {
    Navbar,
    City
  },
  data() {
    return {
      show: false,
      parentId: 0,
      list_arr: [],
      // 表单
      from: {
        head_pic: "",
        savePath: "",
        detailed: "",
        districtId: ""
      },
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/set",
            color: "#333333"
          }
        ],
        right: [
          {
            // 图标大小
            size: "25px",
            coment: "保存",
            to: "",
            color: "#D7000F"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "个人信息",
          //颜色
          color: "#333333"
        }
      }
    };
  },
  methods: {
    // 上传图片
    upload(file) {
      this.$dialog.loading.open("加载中....");
      comm_js.compress(file, 1, 0, 0, 0.7, [], this).then(res => {
        this.uploadImgBase64Fun(res[0]);
      });
    },
    // 图片上传
    uploadImgBase64Fun(item) {
      let that = this;
      uploadImgBase64(item).then(res => {
        if (res.status == 0) {
          that.from.head_pic = res.data.showPath;
          that.from.savePath = res.data.savePath;
        } else {
          comm_js.Dialog2(res.msg, "success", 1000, that);
        }
        that.$dialog.loading.close();
      });
    },
    // 用户信息
    UserDetailFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : ""
      };
      UserDetail(params).then(res => {
        if (res.status == 0) {
          that.from = Object.assign(that.from, res.data);
          // 储存
          that.from.district_name = res.data.district_name;
          that.from.districtId = res.data.districtId;
          let twon_name = res.data.twon_name ? res.data.twon_name : "";
          that.from.detailed =
            res.data.province_name +
            " " +
            res.data.city_name +
            " " +
            twon_name +
            " " +
            res.data.district_name;
        }
      });
    },

    // 更改用户信息
    Preservation() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId")
          ? sessionStorage.getItem("userId")
          : "",
        headImg: this.from.savePath ? this.from.savePath : this.from.showPath,
        nickname: this.from.nickname,
        sex: this.from.sex,
        birthday: this.from.birthday,
        districtId: this.from.districtId
      };
      UserDdit(params).then(res => {
        comm_js.Dialog2("修改成功！", "success", 1000, that);
        if (res.status == 0) {
          that.UserDetailFun();
        }
      });
    },
    // 开启地址弹窗
    showFun() {
      this.show = true;
      this.list_arr = [];
      this.parentId = 0;
      this.ReginFun();
      detailed = "";
    },
    // 选择地址
    addressFun(item) {
      console.log(item);
      this.parentId = item.id;
      detailed += item.name + " ";
      this.ReginFun();
      setTimeout(() => {
        if (!this.show) {
          // 改变地址
          this.from.detailed = detailed;
          this.from.districtId = item.id;
        }
      }, 500);
      console.log(detailed);
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
          that.show = false;
        }
      });
    }
  },
  mounted() {
    this.UserDetailFun();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.headIcon_div {
  width: 100%;
  height: 5.1875rem;
  .headIcon {
    width: 3.9375rem;
    height: 3.9375rem;
    border-radius: 50%;
  }
}
</style>

