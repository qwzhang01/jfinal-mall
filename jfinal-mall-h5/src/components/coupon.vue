<template>
  <div>
    <!-- type =1 -->
    <div class="slide-box" v-if="type==1">
        <div
          class="slide-item"
          :style="{'width':item.width? item.width+'rem' : 5+'rem', height:item.height? item.height+'rem' : 3+'rem'}"
          v-for="(item,key) in coupon"
          :key="key"
        >
          <div class="money"
            :style="{'width':item.width? item.width+'rem' : 5+'rem', height:item.height? item.height+'rem' : 3+'rem'}"
          >
            <span>￥</span>
            <span>{{item.money}}</span>
          </div>
        </div>
    </div>
    <!-- type = 2 -->
    <div v-if="type==2" id="van-coupon-list">
     <!-- 优惠券列表 -->
      <van-popup v-model="showList" position="bottom">
        <van-coupon-list
          :coupons="coupons"
          :chosen-coupon="chosenCoupon"
          :disabled-coupons="disabledCoupons"
          @change="onChange"
          @exchange="onExchange"
        />
      </van-popup>
    </div>

    <!-- type = 3  商家优惠券-->
    <div class="slide-box" v-if="type==3">
        <!-- <div  v-for="(item,key) in coupon" :key="key" > -->
        <div class="store-item"  v-for="(item,key) in coupon" :key="key">
          <div :class="[item.active?'store-item-left':'store-item-left2']">
            <div class="money">
              <span>￥</span>
              <span>{{item.money}}</span>
            </div>
            <div class="coment">{{item.ps}}</div>
            <!-- <div class="coment_1">有效期：2018.05.25-2018.02.28</div> -->
          </div>
          <div :class="[item.active?'store-item-right':'store-item-right2']">
            <div class="coupon_name">优惠券</div>
            <div class="coupon_btn">点击领取</div>
            <img src="../assets/overdue_icon.png" class="overdue_icon" alt="脚标" v-if="item.active == false" />
          </div>
        </div>
    </div>

  </div>

</template>

<script>
// 优惠券
const coupon = {
  available: 1,
  condition: '无使用门槛\n最多优惠12元',
  reason: '',
  value: 150,
  name: '优惠券名称',
  startAt: 1489104000,
  endAt: 1514592000,
  valueDesc: '1.5',
  unitDesc: '元'
};
import { CouponCell, CouponList } from 'vant';
export default {
  props: ['coupon','type'],
  data () {
    return {
      showList:true,
      // 当前选中优惠券的索引
      chosenCoupon: 1,
      // 可以使用优惠券
      coupons: [coupon],
      // 不能使用优惠券
      disabledCoupons: [coupon]
    }
  },
  methods: {
     onChange(index) {
      console.log(12);
      this.showList = false;
      this.chosenCoupon = index;
    },
    onExchange(code) {
      console.log(333);
      this.coupons.push(coupon);
    }
  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.slide-box{
    display: -webkit-box;
    overflow-x: scroll;
    -webkit-overflow-scrolling:touch;
}
.slide-item{
    margin-right:.5rem;
    background: url('../assets/coupon_back.png') no-repeat center;
    background-size: 100% 100%;
}
.money{
  color: #ffffff;
  display: flex;
  align-items:center;
  justify-content:center;
  font-family:PingFang-SC-Medium;
  span:nth-child(1){
    font-size: .75rem;
  }
   span:nth-child(2){
    font-size: 1.2rem;
  }
}
.store-item{
  display: flex;
  align-items:center;
  justify-content:center;
  font-family:PingFang SC;
  background: #ffffff;
  margin-left: .625rem;
  .store-item-left{
    padding: .5rem .9375rem .5rem .9375rem;
    background: url('../assets/store_coupon.png') no-repeat center;
    background-size: 100% 100%;
  }
  .store-item-left2{
    padding: .5rem .9375rem .5rem .9375rem;
    background: url('../assets/overdue.png') no-repeat center;
    background-size: 100% 100%;
  }
  .coment{
    text-align: center;
    font-size:.625rem;
    font-family:PingFang SC;
    color: #ffffff;
    margin-top: .1rem;
  }
  .coment_1{
  text-align: center;
    font-size:.625rem;
    margin-top: .1rem;
    font-family:PingFang SC;
    color: #ffffff;
  }
  .store-item-right2{
    width: 4rem;
    text-align: center;
    position: relative;
    .coupon_name{
      color:#999999;
      font-size: .8125rem;
      opacity: .3;
    }
    .overdue_icon{
      width: 4rem;
      position: absolute;
      bottom: -0.5rem;
      right: 0;
      overflow: hidden;
    }
    .coupon_btn{
      width: 3.5rem;
      margin: .2rem auto 0 auto;
      padding: .1rem .2rem;
      background:#999999;
      color: #ffffff;
      font-size: .5rem;
      border-radius: .625rem;
      opacity: .3;
    }
  }
  .store-item-right{
    width: 4rem;
    text-align: center;
    .coupon_name{
      color:#FC2355;
      font-size: .8125rem;
    }
    .coupon_btn{
      width: 3.5rem;
      margin: .2rem auto 0 auto;
      padding: .1rem .2rem;
      background:#FC2355;
      color: #ffffff;
      font-size: .5rem;
      border-radius: .625rem;
    }
  }
}

.store-item:nth-child(1){
  margin-left: 0
}
</style>

