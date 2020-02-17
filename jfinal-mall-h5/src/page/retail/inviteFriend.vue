<template>
  <div class="smain">
    <Navbar :navbar="navbar" />
    <div class="share-main">
      <div class="share-mcont">
        <div class="share-more">
          <div class="sha-phone">{{userInfo.mobile}}</div>
          <div class="sha-code">
            <img alt :src="userInfo.qr" />
          </div>
        </div>
        <div class="share-btn">
          <a class="weixin" @click="toggleTip">
            <div class="img">
              <img src="../../assets/invite/wechat.png" style="width: 2.5rem; height: 2.5rem;" />
            </div>
            <div class="title">微信</div>
          </a>
          <a class="friends" @click="toggleTip">
            <div class="img">
              <img src="../../assets/invite/friends.png" style="width: 2.5rem; height: 2.5rem;" />
            </div>
            <div class="title">朋友圈</div>
          </a>
        </div>
      </div>
    </div>
    <div v-show="showTip" class="showtip">
      <img src="../../assets/invite/top.png" />
      <div class="sclose">点击右上角，发送给好友</div>
    </div>
  </div>
</template>
<script>
import comm from "../../comm/index";

import Axios from "axios";
import { UserDetail as getUserinfo } from "../../../api/";
import { WxJsConfig } from "../../../api/";
import Navbar from "../../components/navbar";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      userInfo: {},
      showTip: false,
      // 头部导航�
      navbar: {
        height: "2.75rem",
        background: "#D7000F",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/retail",
            color: "#ffffff"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "邀请好友",
          // 颜色
          color: "#ffffff"
        }
      }
    };
  },
  methods: {
    toggleTip() {
      this.showTip = true;
    },
    hideTip() {
      this.showTip = false;
    },
    /**
     * [wxRegister 微信Api初始化]
     * @param  {Function} callback [ready回调函数]
     */
    wxRegister(title, desc, link, imgUrl) {
      var url = location.href.split("#")[0];
      // 这边的接口请换成你们自己
      WxJsConfig({ url }).then(res => {
        if (res.status === 0) {
          let data = res.data; // PS: 这里根据你接口的返回值来使用
          wx.config({
            debug: false, // 开启调试模
            appId: data.appId, // 必填，公众号的唯一标识
            timestamp: data.timestamp, // 必填，生成签名的时间
            nonceStr: data.nonceStr, // 必填，生成签名的随机
            signature: data.signature, // 必填，签名，见附
            jsApiList: [
              "updateAppMessageShareData",
              "updateTimelineShareData",
              "onMenuShareTimeline",
              "onMenuShareAppMessage"
            ] // 必填，需要使用的JS接口列表，所有JS接口列表见附件
          });
        }
      });

      // 处理验证成功的信
      wx.ready(function() {
        // 新版本的微信接口
        // “分享给朋友”及“分享到QQ”
        wx.updateAppMessageShareData({
          title: title, // 分享标题
          desc: desc,
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享到朋友圈成功返回的信息为:", res);
            console.log("分享成功!");
          }
        });
        // “分享到朋友圈”及“分享到QQ空间”
        wx.updateTimelineShareData({
          title: title, // 分享标题
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一�
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享给朋友成功返回的信息：", res);
          }
        });
        // 分享到朋友圈
        wx.onMenuShareTimeline({
          title: title, // 分享标题
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一
          imgUrl: imgUrl, // 分享图标
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享到朋友圈成功返回的信息为:", res);
            console.log("分享成功!");
          },
          cancel: function(res) {
            // 用户取消分享后执行的回调函数
            console.log("取消分享到朋友圈返回的信息为:", res);
          }
        });
        // 分享给朋友
        wx.onMenuShareAppMessage({
          title: title, // 分享标题
          desc: desc, // 分享描述
          link: link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一
          imgUrl: imgUrl, // 分享图标
          type: "", // 分享类型,music、video或link，不填默认为link
          dataUrl: "", // 如果type是music或video，则要提供数据链接，默认为空
          success: function(res) {
            // 用户确认分享后执行的回调函数
            console.log("分享给朋友成功返回的信息�", res);
          },
          cancel: function(res) {
            // 用户取消分享后执行的回调函数
            console.log("取消分享给朋友返回的信息�", res);
          }
        });
      });
    }
  },
  created() {
    getUserinfo({ userId: sessionStorage.getItem("userId") })
      .then(res => {
        this.userInfo = res.data;
        if (comm.isWeiXin()) {
          // 微信浏览器里面执行微信分享的逻辑
          var mobile = this.userInfo.mobile;
          // 分享
          var link = `http://h5.sdbjgj.com/?#/register?mobile=${mobile}`;
          // 分享图标
          var imgUrl = `http://${window.location.host}/static/icon_2.png`;
          // 分享的话
          var comment =
            "【白金管家】积分商城发钱啦！即日起只要您邀请好友注册成为积分商城会员，选择商品下单并支付，就可赚返现";
          this.wxRegister("注册送积分，邀请返现金", comment, link, imgUrl);
        }
      })
      .catch(e => {
        this.$toast("获取用户信息失败");
        console.log(e);
      });
  },
  mounted() {}
};
</script>
<style lang="less" scoped>
.share-main {
  position: relative;
  height: calc(~"100% - 2.75rem");
  padding: 1rem 0;
  overflow: scroll;
  overflow-y: hidden;
  background: url(../../assets/invite/back.png) no-repeat left top;
  background-size: 100% 100%;
  .share-mcont {
    width: 95%;
    height: 29rem;
    background: url(../../assets/invite/subheading.png) 50% no-repeat;
    background-size: 100% 100%;
    margin: 0 auto 1.25rem;
    padding-top: 5.9375rem;
    .share-more {
      width: 13.125rem;
      height: 15rem;
      background: url(../../assets/invite/qr_back.png) 50% no-repeat;
      background-size: 100% 100%;
      margin: 1.25rem auto;
      text-align: center;
      .sha-phone {
        font-size: 1.3rem;
        height: 4.7rem;
        line-height: 4.7rem;
        padding-top: 0.5rem;
        color: #af1f17;
      }
      .sha-code {
        width: 8rem;
        text-align: center;
        height: 8rem;
        margin-left: 2.5rem;
        margin-top: 1rem;
        img {
          display: inline-block;
          width: 100%;
          height: 100%;
        }
      }
    }
    .share-btn {
      display: flex;
      justify-content: space-between;
      margin-top: 0.2rem;
      > a {
        display: block;
        width: 50%;
        .img {
          width: 2.5rem;
          margin: 0 auto;
        }
        .title {
          text-align: center;
          margin: 0 auto;
          margin-top: 0.2rem;
          font-size: 0.875rem;
          color: #fff;
        }
      }
    }
  }
}
/deep/ .yd-navbar:after {
  content: none;
}
.showtip {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
  background: rgba(0, 0, 0, 0.5);
  height: -webkit-fill-available;
  text-align: center;
  color: #ffffff;
  text-align: right;
  img {
    width: 10rem;
    height: 10rem;
    margin-right: 1.5rem;
    margin-top: 1rem;
  }
  .sclose {
    text-align: center;
    margin-top: 2rem;
    color: #ffffff;
    font-size: 1.5rem;
  }
}
</style>
<style>
.smain,
#app {
  height: 100%;
}
</style>
