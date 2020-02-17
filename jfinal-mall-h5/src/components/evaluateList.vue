<template>
  <div class="evaluateList_box">
    <div class="evaluateList" v-for="(item,key) in list" :key="key">
      <!-- 头部 -->
      <div class="head">
        <div class="headUser">
          <img v-lazy="item.head_pic" alt="头像">
          <span class="username" @click="lookName(item.nickname)">{{item.nickname}}</span>
          <!-- 星级 -->
          <span>
            <van-rate
              v-model="item.goods_rank"
              color="#F7B848"
              allow-half
              size="1rem"
              void-icon="star"
              void-color="#eee"
              :readonly="true"
            />
          </span>
        </div>
        <!-- 评价时间 -->
        <div class="evaluateTime">
          <span>{{item.add_time}}</span>
          <span>{{item.spec_key_name}}</span>
        </div>
      </div>
      <!-- 内容 -->
      <div class="coment_box">
        <div class="coment">{{item.content}}</div>
        <div class="media">
          <!-- 图片和视频 -->
          <span class="img" v-for="(items,key) in item.imgs" :key="key">
            <img v-lazy="items" alt="评价图片" @click="viewImg(item.imgs,key)">
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
/**
 * joker
 * 评价组件
 * 说明：
 * 注意：
 */
import comm from "../comm/";
import { ImagePreview } from "vant";
export default {
  props: ["list"],
  data() {
    return {
      comm
    };
  },
  methods: {
    /**
     * 预览图片
     */
    viewImg(arrImg, key) {
      comm.viewImg(arrImg, key);
    },
    // 查看昵称
    lookName(name) {
      this.$dialog.toast({ mes: name, timeout: 1000 });
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.evaluateList_box {
  background: #ffffff;
}

.evaluateList {
  margin: 0 0.9375rem;
  padding-top: 0.5rem;
  .headUser {
    color: #333333;
    font-size: 0.9375rem;
    font-family: PingFang-SC-Medium;
    display: flex;
    align-items: center;
    img {
      width: 1.8125rem;
      height: 1.8125rem;
      border-radius: 50%;
    }
    .username {
      margin: 0 0.6rem;
      width: 3rem;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
  }
  .evaluateTime {
    color: #999999;
    font-size: 0.75rem;
    font-family: PingFang-SC-Medium;
    margin-top: 0.3rem;
    span:nth-child(1) {
      margin-right: 0.6875rem;
    }
  }
  // 内容
  .coment_box {
    padding: 0.9375rem 0;
    border-bottom: 1px solid #e6e6e6;
    .coment {
      color: #333333;
      font-size: 0.8125rem;
      line-height: 1.2rem;
    }
    .media {
      .img img {
        width: 5rem;
        height: 5rem;
        border-radius: 0.25rem;
        margin-top: 0.5625rem;
        margin-right: 0.625rem;
      }
    }
  }
}
.evaluateList:nth-last-child(1) .coment_box {
  border: none;
}
</style>

