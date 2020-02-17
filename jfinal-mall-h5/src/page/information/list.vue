<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 列表 -->
    <div>
      <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="infoListFun">
        <div class="list" v-for="(item,key) in list" :key="key" @click="Jump(item)">
          <div class="listLfet">
            <div
              class="title"
              style="
                  display: -webkit-box;
                  -webkit-box-orient: vertical;
                  overflow : hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;"
            >{{item.title}}</div>
            <div class="titleFont">
              <span class="time">{{item.publishTime}}</span>
              <span class="eye">
                <van-icon name="eye-o"/>
                <i>{{item.clickCount}}</i>
              </span>
            </div>
          </div>
          <div class="listRight">
            <img v-lazy="item.thumb" alt="icon">
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script>
import { infoList } from "../../../api/info/";
import Navbar from "../../components/navbar";
export default {
  components: {
    Navbar
  },
  data() {
    return {
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
          coment: "管家资讯",
          //颜色
          color: "#333333"
        },
        right: []
      },
      loading: false,
      finished: false,
      pageNumber: 1,
      pageSize: 10,
      totalPage: 1,
      list: []
    };
  },
  methods: {
    infoListFun() {
      let that = this;
      let data = {
        pageNumber: this.pageNumber,
        pageSize: this.pageSize
      };
      infoList().then(res => {
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
    Jump(item) {
      console.log(item);
      this.$router.push({
        path: `/info?id=${item.id}`
      });
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 列表
.list {
  display: flex;
  background: #ffffff;
  padding: 0.875rem 0.9375rem;
  border-bottom: 1px solid #e6e6e6;
  position: relative;
  .listLfet {
    width: 100%;
    margin-right: 0.625rem;
    .title {
      color: #333333;
      font-size: 1rem;
      line-height: 1.4rem;
      margin-bottom: 0.3rem;
    }
    .titleFont {
      color: #999999;
      font-size: 0.625rem;
      display: flex;
      align-items: center;
      position: absolute;
      bottom: 1.125rem;
      .time {
        margin-right: 0.625rem;
      }
      .eye {
        display: flex;
        align-items: center;
      }
    }
  }
  .listRight {
    img {
      width: 6.5625rem;
      height: 6.5625rem;
    }
  }
}
</style>

