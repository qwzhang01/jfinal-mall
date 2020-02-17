<template>
  <div class="box">
    <div class="Search_box">
      <div class="Search">
        <van-search :readonly="true" :placeholder="placeholder" @click="jump" />
      </div>
      <div class="msg">
        <img src="../../assets/navbar_icon_news.png" alt="msg" />
      </div>
    </div>
    <!-- 分类 -->
    <div class="classList" :style="{ 'height':height+'rem' }">
      <!-- 左侧导航 -->
      <div class="left_nav">
        <div
          :class="['menu',items.id==activeId?'active':'']"
          v-for="(items,keys) in label"
          :key="keys"
          @click="classFun(items,keys)"
        >
          <i></i>
          <span>{{items.name}}</span>
        </div>
      </div>
      <!-- 右侧内容 -->
      <div class="right_coemnt">
        <!-- 广告 -->
        <div class="Advertisement_div" v-if="right_coemnt.image">
          <img v-lazy="right_coemnt.image" alt="广告" />
        </div>
        <div v-for="(item,key) in right_coemnt.children" :key="key">
          <!-- 二级 -->
          <h3 class="twoTitle">{{item.name}}</h3>
          <div class="class_list_box">
            <!-- 三级列表 -->
            <div class="class_block" v-for="(i,k) in item.children " :key="k" @click="jumpList(i)">
              <div class="class_block_top">
                <img v-lazy="i.icon" alt="分类图标" />
              </div>
              <div class="class_block_bottom">{{i.name}}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 底部导航 -->
    <Nav class="foot_nav" :btn="2" style="z-index:10" />
  </div>
</template>

<script>
import Nav from "../../components/nav";
import { firstCategory } from "../../../api/index.js";
import { searKey } from "../../../api/";
import commJs from "../../comm";
import { mapActions, mapGetters } from "vuex";
export default {
  components: {
    Nav
  },
  data() {
    return {
      label: [],
      placeholder: "请输入查询的商品",
      // 当前选中calss的id
      activeId: 0,
      //右侧内容
      right_coemnt: [],
      height: 0
    };
  },
  methods: {
    ...mapActions(["slelctClassFun"]),
    //跳转
    jump() {
      this.$router.push({
        path: "./search"
      });
    },
    jumpList(item) {
      this.$router.push({
        path: "./result?text=" + item.name + "&id=" + item.id
      });
    },
    // 分类信息
    firstCategoryFun() {
      let that = this;
      firstCategory().then(res => {
        if (res.status == 0) {
          that.label.push(...res.data);
          if (that.$route.query.id) {
            that.seek(that.$route.query.id);
          } else {
            that.activeId =
              that.getClassId > 0
                ? that.getClassId
                : res.data[0].id
                  ? res.data[0].id
                  : 0;
            that.right_coemnt = res.data[that.getClassKey]
              ? res.data[that.getClassKey]
              : [];
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 点击分类
    classFun(item, key) {
      this.activeId = item.id;
      this.right_coemnt = this.label[key] ? this.label[key] : [];
      this.slelctClassFun({ activeId: this.activeId, key });
    },
    // 热门搜索
    searKeyFun() {
      let that = this;
      searKey().then(res => {
        if (res.status == 0) {
          if (res.data.length > 0) {
            that.placeholder = res.data[0];
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 寻找对应的id
    seek(id) {
      this.label.map((item, key) => {
        if (item.id == id) {
          this.activeId = item.id;
          this.right_coemnt = this.label[key] ? this.label[key] : [];
        }
      });
    }
  },
  computed: {
    ...mapGetters(["getClassId", "getClassKey"])
  },
  mounted() {
    this.firstCategoryFun();
    this.searKeyFun();
    // 获取高度
    this.height = commJs.equipment().height_rem - 6.5;
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
//div
.box {
  background: #ffffff;
  position: relative;
  height: -webkit-fill-available;
}
// 搜索
.Search_box {
  display: flex;
  align-items: center;
  // position: fixed;
  // top:0;
  // z-index: 10;
  width: 100%;
  background: #ffffff;
  padding: 0.625rem 0.9375rem;
  /deep/ .van-search {
    padding: 0;
  }
  /deep/ .van-search__content {
    padding-left: 0;
    background: #ffffff;
  }
  /deep/ .van-search .van-cell {
    background: #f5f5f5;
    border-radius: 1rem;
  }
  /deep/ .van-field__left-icon {
    padding-left: 0.625rem;
  }
  /deep/ .van-field__control {
    padding-top: 0.2rem;
  }
  .Search {
    width: 90%;
  }
  .msg {
    img {
      margin-left: 0.8rem;
      width: 1.375rem;
      height: 1.375rem;
    }
  }
}

// 分类

// 广告
.Advertisement_div {
  // width: 100%;
  img {
    width: 100%;
    height: 5.625rem;
  }
}
.class_list_box {
  // width: 100%;
  display: flex;
  flex-wrap: wrap;
}
.twoTitle {
  padding: 0.9375rem 0;
}

.class_block {
  width: 33.33%;
  margin-top: 0.9375rem;
  text-align: center;
  .class_block_top {
    img {
      width: 2.375rem;
      height: 3.375rem;
    }
  }
  .class_block_bottom {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #333333;
    font-family: PingFang-SC-Medium;
    font-size: 0.75rem;
    padding: 0.3rem 0.3rem 0 0.3rem;
  }
}

.classList {
  display: flex;
  // position: fixed;
  width: 100%;
  // height:;
  // padding-bottom: 6.7rem;
  // top: 3.5rem;

  // 左边
  .left_nav {
    width: 23%;
    background: #f5f5f5;
    // height: -webkit-fill-available;
    overflow-x: hidden;
    .menu {
      width: 100%;
      height: 3.25rem;
      display: flex;
      align-items: center;
      color: #333333;
      font-family: PingFang-SC-Medium;
      font-size: 0.75rem;
      span {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        padding: 0 0.4rem;
        width: 100%;
        display: block;
        text-align: center;
      }
      i {
        display: block;
        height: 0.7rem;
        border-left: 0.1rem solid #f5f5f5;
      }
    }
    .active {
      color: #d7000f;
      background: #ffffff;
      i {
        border-left: 0.1rem solid #d7000f;
      }
    }
  }
  // 右边
  .right_coemnt {
    width: 77%;
    padding: 0 0.625rem 0.625rem 0.625rem;
    // height: -webkit-fill-available;
    overflow-x: hidden;
  }
}
</style>

