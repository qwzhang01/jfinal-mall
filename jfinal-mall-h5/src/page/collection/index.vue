<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" @navRight="navRight"/>

    <!-- 列表 -->
    <van-tabs
      v-model="active"
      animated
      @click="btnNav"
      :style="{'margin-bottom':select ? '3rem' : '0'}"
    >
      <van-tab v-for="(item,key) in lable" :title="item" :key="key">
        <!-- 商品 -->
        <div v-if="key==0">
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="goodscollectPageFun"
          >
            <List
              :list_type="6"
              :list="shopList"
              class="list"
              :select="select"
              @selectFun="selectFun"
              @joinActivity="check"
            />
          </van-list>
        </div>
        <!-- 店铺 -->
        <div v-if="key==1">
          <van-list
            v-model="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="storecollectPageFun"
          >
            <List
              :list_type="6"
              :list="storeList"
              class="list storeList"
              :select="select"
              @selectFun="selectFun"
              @joinActivity="check"
            />
          </van-list>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 底部 -->
    <van-submit-bar button-text="删除" @submit="onSubmit" v-if="select">
      <van-checkbox
        v-model="checked"
        checked-color="#D7000F"
        class="select"
        @click="selectFunCall"
      >全选</van-checkbox>
    </van-submit-bar>
  </div>
</template>

<script>
// import { Dialog } from 'vant';
import Navbar from "../../components/navbar";
import List from "../../components/list";
import commJs from "../../comm";
import {
  storecollectPage,
  goodscollectPage,
  storecancelCollectBatch,
  goodscancelCollectBatch
} from "../../../api/collection/";
export default {
  components: {
    Navbar,
    List
  },
  data() {
    return {
      active: 0,
      // 是否全选
      checked: false,
      // 是否选择商品
      checkedActive: false,
      lable: ["商品", "店铺"],
      select: false,
      // 商品列表
      shopList: [],
      // 删除的Id
      delId: "",
      // 店铺列表
      storeList: [],
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/myself",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "我的收藏",
          //颜色
          color: "#333333"
        },
        right: [
          {
            icon_img: "",
            // 目标地址
            to: "",
            coment: "编辑",
            color: "#333333",
            // 图标大小
            size: "1rem"
          }
        ]
      },
      pageSize: 10, // 每条显示页数
      pageNumber: 1, // 当前页数
      totalPage: 1, // 总页数
      // 滚动加载
      loading: false,
      finished: false
    };
  },
  methods: {
    // 店铺收藏列表
    storecollectPageFun(active = false) {
      let that = this;
      let data = {
        pageSize: this.pageSize,
        pageNumber: this.pageNumber
      };
      storecollectPage(data).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            // console.log(res);
            if (res.data.totalPage >= that.pageNumber) {
              that.pageSize = res.data.pageSize;
              that.totalPage = res.data.totalPage;
              if (active) {
                that.storeList = [];
              }

              if (res.data.list.length > 0) {
                that.storeList.push(
                  ...res.data.list.map(item => {
                    item.text = `${item.collectNum}人收藏`;
                    item.image = item.storeAvatar;
                    item.delId = item.storeId;
                    item.active = false;
                    return item;
                  })
                );
                that.pageNumber = parseInt(that.pageNumber) + 1;
              }
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    // 商品收藏
    goodscollectPageFun(active = false) {
      let that = this;
      let data = {
        pageSize: this.pageSize,
        pageNumber: this.pageNumber
      };
      goodscollectPage(data).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.pageNumber) {
              that.pageSize = res.data.pageSize;
              that.totalPage = res.data.totalPage;
              if (active) {
                that.shopList = [];
              }
              if (res.data.list.length > 0) {
                that.shopList.push(
                  ...res.data.list.map(item => {
                    item.text = `${item.collectNum}人收藏`;
                    item.active = false;
                    item.delId = item.goodId;
                    return item;
                  })
                );
                that.pageNumber = parseInt(that.pageNumber) + 1;
              }
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    // 查看
    check(item) {
      console.log(item);
      let path;
      let suuper = "/collection";
      // 商品
      if (item.hasOwnProperty("goodId")) {
        path = `/goodsinfo?goodId=${item.goodId}&u=${suuper}`;
      }
      // 店铺
      else if (item.hasOwnProperty("storeId")) {
        path = `/storepage?store_id=${item.storeId}&u=${suuper}`;
      } else {
        return;
      }
      this.$router.push({ path });
    },
    // 点击编辑
    navRight(item) {
      if (item.coment == "编辑") {
        this.select = !this.select;
      }
    },
    onSubmit() {
      let ajax;
      let data;
      let that = this;
      if (!this.checkedActive) {
        commJs.Dialog2("请选择商品", "error", 1000, this);
        return;
      }
      if (this.delId.charAt(this.delId.length - 1) === ",") {
        this.delId = this.delId.slice(0, this.delId.length - 1);
      }
      commJs
        .Dialog("温馨提示", `确定要移除${this.active == 0 ? "商品" : "店铺"}`)
        .then(res => {
          console.log(res);
          if (res === "confirm") {
            this.$dialog.loading.open("请求中...");
            if (this.active == 0) {
              data = { goodIds: this.delId };
              goodscancelCollectBatch(data).then(res => {
                if (res.status == 0) {
                  // that.del();
                  this.pageNumber = 1;
                  that.goodscollectPageFun(true);
                } else {
                  commJs.Dialog2(res.msg, "error", 1000, that);
                }
                that.$dialog.loading.close();
              });
            } else {
              data = { storeIds: this.delId };
              storecancelCollectBatch(data).then(res => {
                if (res.status == 0) {
                  this.pageNumber = 1;
                  // that.del();
                  that.storecollectPageFun(true);
                } else {
                  commJs.Dialog2(res.msg, "error", 1000, that);
                }
                that.$dialog.loading.close();
              });
            }
          }
        });
    },

    // 选择按钮
    selectFun(item) {
      let list;
      // 商品
      if (this.active == 0) {
        list = this.shopList;
      } else {
        list = this.storeList;
      }
      let len = list.length;

      let activeTrue = 0;
      this.checkedActive = false;
      this.delId = "";
      list.forEach((items, key) => {
        if (
          this.active == 0
            ? this.shopList[key].active
            : this.storeList[key].active
        ) {
          this.checkedActive = true;
          // 获取删除id
          this.delId += list[key].delId + ",";
        }
        if (list[key].active) {
          activeTrue++;
        }

        if (len == activeTrue) {
          this.checked = true;
        } else {
          this.checked = false;
        }
      });
      console.log(this.delId);
    },

    // 全选
    selectFunCall() {
      let list;
      // 商品
      if (this.active == 0) {
        list = this.shopList;
      } else {
        list = this.storeList;
      }
      this[this.active == 0 ? "shopList" : "storeList"] = list.map(
        (item, key) => {
          item.active = this.checked;
          return item;
        }
      );
      this.delId = this.checked ? list.map(item => item.delId).join(",") : "";
      this.checkedActive = this.checked;
    },
    //切换导航
    btnNav(index, item) {
      this.select = false;
      this.checkedActive = false;
      this.checked = false;
      this.pageNumber = 1;
      this.finished = false;
      this.loading = false;
      if (index == 0) {
        this.goodscollectPageFun(true);
      } else {
        this.storecollectPageFun(true);
      }
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.list {
  padding: 0.875rem 0.9375rem;
  background: #ffffff;
  /deep/ .collection_coment {
    margin-top: 0.625rem;
  }
  /deep/.shopInfo {
    border-bottom: 1px solid #e6e6e6;
    /deep/ .bottom {
      margin-bottom: 0.75rem;
    }
  }
}
.select {
  margin-left: 0.9375rem;
  font-size: 1rem;
  display: flex;
  align-items: center;
  i {
    width: 1.375rem;
  }
}
// 店铺
.storeList {
  /deep/ .collection_box {
    border-bottom: 1px solid #e6e6e6;
    padding: 0.625rem 0;
  }
  /deep/ .shopInfo {
    border: none;
  }
  /deep/ .collectionImg {
    /deep/ img {
      width: 4.2rem !important;
      height: 4.2rem !important;
    }
  }
  /deep/ .comentFot {
  }
}
/deep/ .van-tab__pane {
  height: inherit;
}
</style>

