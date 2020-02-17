<template>
  <div>
    <div class="like" id="like">
      <!-- 标题 -->
      <div class="title">
        <span>猜你喜欢</span>
      </div>
      <!-- 列表 -->
      <van-list
        v-model="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="PageFavouriteFun"
      >
        <List :list_type="1" :list="like.list" :theme="1" @introduce="introduceSelf" />
      </van-list>
    </div>
  </div>
</template>

<script>
import { PageFavourite } from "../../api/";
import List from "../components/list";
import commJs from "../comm";
import { ImagePreview } from "vant";
export default {
  props: [],
  components: {
    List
  },
  data() {
    return {
      // 猜你喜欢
      like: {
        pageSize: 10, // 每条显示页数
        pageNumber: 1, // 当前页数
        list: [], // 列表数据
        totalPage: 1 // 总页数
      },
      // 滚动加载
      loading: false,
      finished: false
    };
  },
  methods: {
    /**
     * joker
     * 猜你喜欢
     * @param pageSize number   当前条数
     * @param pageNumber number 当前页数
     */
    PageFavouriteFun() {
      let that = this;
      let params = {
        pageSize: that.like.pageSize,
        pageNumber: that.like.pageNumber
      };
      PageFavourite(params).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.like.pageNumber) {
              that.like.pageSize = res.data.pageSize;
              that.like.totalPage = res.data.totalPage;
              that.like.list.push(...res.data.list);
              that.like.pageNumber = parseInt(that.like.pageNumber) + 1;
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    introduceSelf(data) {
      this.$emit("introduceSelf", data);
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 猜你喜欢
.like {
  margin-top: 0.625rem;
  background: #ffffff;
  padding: 0 0.5rem;
  .title {
    background: url("../assets/lick.png") no-repeat center;
    background-size: 100% 100%;
    font-family: PingFang-SC-Bold;
    color: #333333;
    font-size: 1rem;
    padding-top: 0.9375rem;
    padding-bottom: 1.25rem;
    text-align: center;
  }
  /deep/ .yd-list-other {
    height: 1.5rem;
  }
  /deep/ .demo-list-price {
    font-size: 1rem;
    color: #e80002;
  }
}
/deep/ .yd-list-title {
  -webkit-line-clamp: 2;
  font-weight: inherit;
  color: #000000;
}
</style>

