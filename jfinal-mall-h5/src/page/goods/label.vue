<template>
  <div class="video">
    <!-- 活动标签  -->
    <!-- 活动时间 -->
    <div class="group" v-if="type=='groupTime'">
      <div class="g1">
        <div>
          ￥
          <span v-if="activity.active">
            <span>
              <i v-if="activity.flashPrice">{{activity.flashPrice}}</i>
              <span style="font-size:1rem;" v-if="activity.point > 0"> + {{activity.point}}积分</span>
            </span>
            <span v-if="activity.lotteryPrice">{{activity.lotteryPrice}}</span>
          </span>
          <span v-else>{{activity.shop_price}}</span>
        </div>
        <div>
          <span>
            {{
            activity.activityType==2? activity.active?'原' : '秒杀':
            activity.activityType==3? activity.active?'原' : '单':
            '原'
            }}价
          </span>
          <span>
            <i v-if="activity.active">￥{{activity.shop_price}}</i>
            <i v-else>
              <i v-if="activity.flashPrice">
                ￥{{activity.flashPrice}}
                <i
                  style="font-size:0.1rem;"
                  v-if="activity.point > 0"
                >+ {{activity.point}}积分</i>
              </i>
              <i v-if="activity.lotteryPrice">{{activity.lotteryPrice}}</i>
            </i>
          </span>
        </div>
      </div>

      <div class="g2">
        <div>
          距{{
          activity.activityType==2?'秒杀':
          activity.activityType==3?'抢购':
          ''
          }}{{activity.active?'结束':'开始' }}时间
        </div>
        <div>
          <yd-countdown
            :time="activity.newTime"
            format="<i>{%d}</i> 天 <i>{%h}</i> : <i>{%m}</i> : <i>{%s}</i>"
          ></yd-countdown>
        </div>
      </div>
    </div>
    <!-- 活动人员列表 -->
    <div class="personnelist" v-if="type=='personnel'">
      <!-- 标题 -->
      <div class="title" v-if="titleShow">
        <span>第{{activity.activityNum}}场，{{personnelist.attendNum}}人正在参与，可直接参加</span>
        <span @click="more">
          查看更多
          <van-icon name="arrow" size=".9375rem" />
        </span>
      </div>
      <!-- 人员列表 -->
      <div
        class="list"
        v-for="(item,key) in personnelist.userList.length > 3 ? personnelist.userList.splice(3, personnelist.userList.length - 3) : personnelist.userList"
        :key="key"
      >
        <div class="userImg">
          <img v-lazy="item.headPic" alt="头像" />
          <i>{{item.mobile}}</i>
        </div>
        <div class="time">
          <div>
            还差
            <span>{{activity.absentNum}}人</span>开奖
          </div>
          <div>
            结束时间
            <yd-countdown
              :time="activity.newTime"
              format="<i>{%d}</i>天<i>{%h}</i>:<i>{%m}</i>:<i>{%s}</i>"
            ></yd-countdown>
          </div>
        </div>
      </div>
    </div>

    <!--参与按钮 -->
    <div class="join" v-if="type=='join'" @click="joinFun">
      <van-button size="large">立即抢购</van-button>
    </div>
  </div>
</template>

<script>
/**
 * 商品活动标签
 * 说明：主要是针对商品倒计时，商品在详情的一些小的提示功能
 * 注意：
 */
export default {
  props: ["type", "personnelist", "activity", "titleShow"],
  data() {
    return {};
  },
  methods: {
    joinFun() {
      this.$emit("joinFun");
    },
    more() {
      this.$emit("more");
    }
  },
  mounted() {
    console.log(this.type)
  }
};
</script>

<style lang="less" scoped>
// 活动时间
.group {
  display: flex;
  justify-content: space-between;
  height: 3.25rem;

  .g2 {
    height: 3.25rem;
    background: #f7cccf;
    width: 50%;
    text-align: right;
    padding-right: 0.9375rem;
  }
  .g2 {
    div:nth-child(1) {
      color: #d7000f;
      font-size: 0.75rem;
      margin: 0.3rem 0;
      text-align: center;
      padding-left: 1.5rem;
    }
    div:nth-child(2) {
      /deep/ i {
        padding: 0.2rem 0.3rem;
        background: #d7000f;
        color: #ffffff;
      }
      font-size: 0.75rem;
      color: #d7000f;
    }
  }
  .g1 {
    background: #d7000f;
    width: 50%;
    height: 3.25rem;
    padding-left: 0.9375rem;
  }
  .g1 div:nth-child(1) {
    font-size: 1.25rem;
    color: #ffffff;
    font-family: PingFang-SC-Bold;
    margin: 0.1rem 0;
  }
  .g1 div:nth-child(2) {
    span:nth-child(1) {
      padding: 0.1rem 0.5rem 0.05rem 0.5rem;
      border-radius: 0.5rem;
      color: #d7000f;
      background: #f7cccf;
    }
    span:nth-child(2) {
      // text-decoration:line-through;
      color: #ffffff;
    }
  }
}

// 活动人员列表
.personnelist {
  background: #ffffff;
  .title {
    display: flex;
    justify-content: space-between;
    height: 2.5rem;
    align-items: center;
    font-size: 0.9375rem;
    border-bottom: 1px solid #e6e6e6;
    padding-right: 0.9375rem;
    margin-left: 0.9375rem;
    color: #333333;
    span:nth-child(2) {
      color: #999999;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      width: 6rem;
    }
  }
  .list {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 3rem;
    padding: 0 0.9375rem;
    img {
      width: 2rem;
      height: 2rem;
      border-radius: 50%;
    }
  }
  .list .userImg {
    display: flex;
    align-items: center;
    color: #333333;
    i {
      font-size: 0.875rem;
      margin-left: 0.5rem;
    }
  }
  .list .time {
    text-align: right;
    line-height: 1.2rem;
    div:nth-child(1) {
      color: #333333;
      font-family: PingFang-SC-Medium;
      margin-top: 0.4rem;
      span {
        color: #d7000f;
        font-weight: bold;
      }
    }
    div:nth-child(2) {
      color: #666666;
      font-size: 0.625rem;
    }
  }
}
// 参加
.join {
  position: fixed;
  bottom: 0;
  height: 2.8125rem;
  width: 100%;
  background: #ffffff;
  button {
    color: #ffffff;
    font-size: 1rem;
    font-family: PingFang-SC-Medium;
    background: #d7000f;
    border: none;
    border-radius: 18px;
    height: 36px;
    margin: 0.4rem 0;
  }
}
</style>

