<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 列表 -->
    <div>
      <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="RecommendFun">
        <div class="store" v-for="(item,key) in list" :key="key">
          <!-- 店铺名 -->
          <router-link :to="'/storepage?store_id='+item.storeId+'&u=/recommendStore'">
            <div class="storeTitle">
              <div class="storeLeft">
                <div class="storeImg">
                  <img v-lazy="item.storeAvatar" alt="logo">
                </div>
                <div class="title_box">
                  <div class="title">{{item.storeName}}</div>
                  <div class="storeData">
                    <span>月销：{{item.storeSale}}件</span>
                  </div>
                </div>
              </div>
              <div class="score">评分：{{item.storeCredit}}分</div>
            </div>
          </router-link>
          <!-- 产品 -->
          <div class="projectList">
            <div class="shop" v-for="(data,i) in item.good" :key="i" @click="JumpGood(data)">
              <div class="img">
                <img v-lazy="data.originalImg" alt="商品图片">
              </div>
              <div class="moeny">
                <div class="newMoeny">￥{{data.shopPrice}}</div>
                <div class="OriginalMoeny">￥{{data.marketPrice}}</div>
              </div>
            </div>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import { Recommend } from "../../../api/store/";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      loading: false,
      finished: false,
      pageNumber: 1,
      pageSize: 10,
      totalPage: 1,
      list: [],
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "店铺推荐",
          //颜色
          color: "#333333"
        },
        right: []
      }
    };
  },
  methods: {
    RecommendFun() {
      let that = this;
      let data = {
        pageSize: that.pageSize,
        pageNumber: that.pageNumber
      };
      Recommend(data).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.pageNumber) {
              that.pageSize = res.data.pageSize;
              that.totalPage = res.data.totalPage;
              that.list.push(...res.data.list);
              that.pageNumber = parseInt(that.pageNumber) + 1;
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    //跳转
    JumpGood(data) {
      this.$router.push({
        path: `goodsinfo/?goodId=${data.goodId}&u=/recommendStore`
      });
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.store {
  margin: 0.9375rem;
  background: #ffffff;
  border-radius: 0.625rem;
  padding: 0.9375rem 0.625rem;
  box-shadow: 0.1875rem 0.1875rem 0.25rem #e6e6e6;
  .storeTitle {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.625rem;
    .storeImg {
      margin-right: 0.625rem;
      img {
        width: 2.125rem;
        height: 2.125rem;
      }
    }
    .storeLeft {
      display: flex;
    }
    .title_box {
      .title {
        color: #333333;
        font-size: 0.9375rem;
        font-family: PingFang SC;
      }
      .storeData {
        span {
          color: #999999;
          font-size: 0.6875rem;
        }
      }
    }
    .score {
      color: #f7b848;
      font-family: PingFang SC;
      font-size: 0.6875rem;
    }
  }
  .projectList {
    width: 100%;
    overflow-x: scroll;
    overflow-y: hidden;
    white-space: nowrap; /* ul中的内容不换行 */
    display: flex;

    .shop {
      width: 6.5625rem;
      height: 6.5625rem;
      position: relative;
      margin-right: 0.625rem;
      .img {
        width: 6.5625rem;
        height: 6.5625rem;
        img {
          width: 6.5625rem;
          height: 6.5625rem;
        }
      }
      .moeny {
        position: absolute;
        bottom: 0;
        display: flex;
        width: 100%;
        font-family: PingFang SC;
        background: rgba(255, 255, 255, 0.7);
        padding: 0.1rem 0.2rem;
        .newMoeny,
        .OriginalMoeny {
          width: 50%;
          text-align: center;
        }
        .newMoeny {
          color: #d7000f;
          font-size: 0.75rem;
        }
        .OriginalMoeny {
          color: #999999;
          font-size: 0.625rem;
          text-decoration: line-through;
        }
      }
    }
    .shop:nth-last-child(1) {
      margin-right: 0;
    }
  }
}
</style>
