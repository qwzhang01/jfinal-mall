<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 通知 -->
    <div class="Notify" v-if="infoData.hasUserWin == 1">
      <img :src="userInfo.headPic" alt="头像" />
      <span>抽中了{{userInfo.title}}</span>
    </div>
    <!-- 商品 -->
    <div class="robShoppingList" >
      <div class="haeder">
        <div class="shopImg">
          <img :src="infoData.imgPath" alt="图片" />
        </div>
        <div class="shopComent">
          <div class="title"
          style="display: -webkit-box;
                -webkit-box-orient: vertical;
                overflow : hidden;
                display: -webkit-box;
                -webkit-line-clamp: 2;"
          >{{infoData.title}}</div>
          <div class="time">{{ infoData.active ? infoData.openTime.replace(/\//g,'-') : infoData.openTime}} 开奖</div>
        </div>
      </div>
    </div>
    <!-- 参团人员 -->
    <div class="jionUsers">
      <div class="title" v-if="infoData.active">距离开奖还剩 <yd-countdown :time="infoData.openTime" class="openTime" format="{%d}天{%h}时{%m}分{%s}秒"></yd-countdown>, 尽情期待</div>
      <div class="title" v-else>
        <span v-if="infoData.isWin ==2"><van-icon name="clear" color="#D7000F" size="1.1rem" class="icon" /> 很遗憾，抽奖失败</span>
        <span v-if="infoData.isWin ==1"><van-icon name="checked" color="#D7000F" size="1.1rem" class="icon" /> 抽奖成功，恭喜你成为本轮的幸运儿~</span>
      </div>
      <div class="headPic">
           <img v-lazy="item.headPic" alt="user" v-for="(item,key) in infoData.userImgList" :key="key" :class="[key > 0?'headPic_1':'']" :style="{'left': '-'+key/2+'rem' }" />
          <img src="../../../../assets/haeder_icon.png" alt="user" class="headPic_1" :style="{'left': '-'+ (infoData.userImgList ? infoData.userImgList.length : 0) / 2+'rem' }"/>
      </div>
      <!-- 提示 -->
      <div v-if="infoData.isWin ==1">
         <div class="Notify Notify2">
          <span>正在为你的商品安排发货，请耐心等待</span>
        </div>
        <div class="btn">
          <yd-button type="hollow" @click.native="check(infoData)" >查看订单</yd-button>
        </div>
      </div>
       
    </div>
    <!-- 更多抽奖 -->
    <div class="list">
      <div class="title">
        <img src="../../../../assets/shop_1.png" alt="shop_icon" />
        <span>更多抽奖商品</span>
      </div>
      <!-- 列表 -->
       <List :list_type="4" :list="infoData.moreLotteryGood"  @joinActivity="joinActivity" />
    </div>
    <!-- 消息通知 -->
    
  </div>
</template>

<script>
import { Notify } from 'vant';
import Navbar from '../../../../components/navbar'
import List from '../../../../components/list'
import commJs from '../../../../comm';
import {
  lotteryDetail,
  whoWin
} from '../../../../../api/goods/'
export default {
   components:{
    Navbar,
    List
  },
  data () {
    return {
        // 头部导航栏
      navbar:{
        height:'2.75rem',
        left:[
          {
            // 图标大小
            size:'25px',
            coment:'返回',
            to: '/robShoppingorderList',
            color:'#333333',
          }
        ],
        // 标题
        title:{
          // 内容
          coment:'抽奖详情',
          //颜色
            color:'#333333',
        },
        right:[]
      },
      lotteryOrderId:'',
      activityNum:'',
      lotteryGoodId:'',
      infoData:{
        activityNum:'',
        imgPath:'',
        moreLotteryGood:[]
      },
      // 中奖人员信息
      userInfo:{
        headPic:''
      }
    }
  },
  methods: {
    //获取抽奖详情
    lotteryDetailFun(){
      let that =this;
      let data ={
        lotteryOrderId:this.lotteryOrderId,
        activityNum:this.activityNum,
        lotteryGoodId:this.lotteryGoodId
      }
      lotteryDetail(data).then(res=>{
          if(res.status == 0){
            // 现在的时间，精确到秒
            let time = Date.parse(new Date())/1000;
            // 开奖时间
            let openTime =Date.parse(new Date( res.data.openTime)) /1000;
            // 结束
            if(time > openTime){
              res.data.active =false;
            }
            // 等待开奖
            else{
              res.data.active =true;
              res.data.openTime= commJs.dateHandle(openTime,'/');
            }
            if(res.data.hasUserWin == 1){
              that.whoWinFun(res.data.lotteryGoodId,res.data.activityNum);
            }
            
            that.infoData = Object.assign(that.infoData, res.data);
          }else{
            commJs.Dialog2(res.msg,'error',1000,that);
          }
      })
    },
    // 参加抽奖
    joinActivity(item){
      console.log(item);
      if(item.isMax == 1){
          commJs.Dialog2('参与人数已满','error',1000,this);
          return;
      }
      if(item.isSellOut == 1){
          commJs.Dialog2('商品已售罄','error',1000,this);
          return;
      }
      this.$router.push({
        path: `/goodsinfo?goodId=${item.goodId}&lotteryGoodId=${item.lotteryGoodId}`
      });
      sessionStorage.setItem('upper','/robShoppingorderList');
    },
    // 查看订单
    check(item){
        this.$router.push({
            path:'/orderinfo',
            query:{
              orderId:item.orderId,
              tabl:0
            }
          })
    },
    // 获取中奖名单
    whoWinFun(lotteryGoodId,activityNum){
      let that =this;
      let data ={
        lotteryGoodId,
        activityNum
      }
      whoWin(data).then(res=>{
        if(res.status == 0){
          console.log(res);
          that.userInfo = Object.assign(that.userInfo, res.data);
        }else{
          commJs.Dialog2(res.msg,'error',1000,that);
        }
      })
    }
  },
  mounted(){
      
    if(Object.keys(this.$route.query).length >0){
      let query = this.$route.query;
      this.lotteryOrderId =   query.lotteryOrderId;
      this.activityNum    =   query.activityNum;
      this.lotteryGoodId  =   query.lotteryGoodId;
      // 请求
      this.lotteryDetailFun();
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
/deep/ .shopImg{
  width: 6.875rem;
  height: 6.875rem;
  /deep/ img{
    width: 100% !important;
    height: 100% !important;
  }
}
/deep/ .robShoppingList{
  margin-bottom: 0;
}
.jionUsers{
  padding: .9375rem 0;
  background: #ffffff;
  margin-bottom: .625rem;
  .title{
    // text-align: center;
    font-size: 1rem;
    color: #333333;
    display: flex;
    align-items:center;
    justify-content:center;
    .openTime{
      margin-left: .3rem;
    }
    span{
      display: flex;
      align-items:center;
      justify-content:center;
      font-size: 1rem;
      .icon{
        margin-right: .3rem;
      }
    }
    
  }
  .headPic{
      display: flex;
      align-items:center;
      justify-content:center;
      margin-top: .9375rem;
      margin-left: 3rem;
    }
  img{
      display: block;
      width: 2rem;
      height: 2rem;
      border-radius: 45%;
      border: 1px solid #ffffff;
    }
    .headPic_1{
      position: relative;
    }
}
// 更多抽奖
.list{
  background: #ffffff;
  .title{
    display: flex;
    align-items:center;
    justify-content:center; 
    color: #D7000F;
    font-size: .875rem;
    font-family:PingFang SC;
    padding: .9375rem 0;
    img{
      width: .875rem;
      height: .875rem;
      margin-right: .625rem;
    }
     
  }
}
// 通知
.Notify{
  width: 100%;
  background: #B7B7B7;
  padding: .3rem .9375rem;
  display: flex;
  align-items:center;
  color: #ffffff;
  line-height: 1rem;
  img{
    width: 1.375rem;
    height: 1.375rem;
    margin-right: .3rem;
    border-radius: 50%;
  }
}
.Notify2{
  background: #FEF9F5;
  color: #D7000F;
  margin-top: 1rem;
}
.btn{
  width: 100%;
  display: flex;
  justify-content:flex-end; 
  align-items:center;
  margin-top: 1rem;
  /deep/ .yd-btn{
    font-family:PingFang SC;
    margin-right: 1rem;
    width: 4rem;
    height: 1.6875rem;
    color: #D7000F;
    font-size: .8125rem;
    border-radius: .9375rem;
    border: 1px solid  #D7000F;
  }
}
</style>

