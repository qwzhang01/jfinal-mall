<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" />
    <!-- 列表 -->
    <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="userLotteryListFun" >
      <div class="robShoppingList" v-for="(item,key) in list" :key="key" >
        <div class="haeder">
          <div class="shopImg">
            <img :src="item.imgPath" alt="图片" />
          </div>
          <div class="shopComent">
            <div class="title"
            style="display: -webkit-box;
                  -webkit-box-orient: vertical;
                  overflow : hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;"
            >{{item.title}}</div>
            <div class="time">{{ item.active ? item.openTime.replace(/\//g,'-') : item.openTime}} 开奖</div>
          </div>
        </div>
        <div class="foot">
          <div class="timeAlert">
            <span v-if="item.active">
              距离开奖还剩 <yd-countdown :time="item.openTime" format="{%d}天{%h}时{%m}分{%s}秒"></yd-countdown>, 尽情期待
            </span>
            <span v-else>{{item.isWin == 1?'恭喜你，成为本轮的幸运儿~':'很遗憾，未能中奖'}}</span>
          </div>
          <div class="btn">
            <yd-button type="hollow" @click.native="check(item,1)" v-if="item.isWin == 2 && !item.active && item.hasUserWin == 1">中奖名单</yd-button>
            <yd-button type="hollow" @click.native="check(item,2)">查看详情</yd-button>
          </div>
        </div>
      </div>
    </van-list>
    <!-- 中奖名单 -->
    <yd-popup v-model="show1" position="center" width="90%">
      <div class="coment_lottery">
        <!-- 删除icon -->
        <div class="del">
          <img src="../../../../assets/del_1.png" alt="del" @click="show1= !show1" />
        </div>
        <!-- 活动人数 -->
          <div class="title">中奖名单</div>
          <div class="div_personnelist">
            <div class="img">
             <img :src="userInfo.headPic" alt="头像" />
            </div>
            <div class="coment">
              <div class="phone">{{userInfo.mobile}}</div>
              <div class="shop">中奖商品：价值¥1688.00的指纹锁一把</div>
            </div>
          </div>
      </div>
    </yd-popup>
  </div>
</template>

<script>
import Navbar from '../../../../components/navbar'
import commJs from '../../../../comm';
import {
  userLotteryList,
  whoWin
} from '../../../../../api/goods/'
export default {
  components:{
    Navbar
  },
  data () {
    return {
      show1:false,
      // 头部导航栏
      navbar:{
        height:'2.75rem',
        left:[
          {
            // 图标大小
            size:'25px',
            coment:'返回',
            to: '/myself',
            color:'#333333',
          }
        ],
        // 标题
        title:{
          // 内容
          coment:'抽奖列表',
          //颜色
            color:'#333333',
        },
        right:[]
      },
      pageSize:10,  // 每条显示页数
      pageNumber:1, // 当前页数
      list:[],      // 列表数据
      totalPage:1,  // 总页数
      // 滚动加载
      loading: false,
      finished: false,
      // 中奖人员
      userInfo:{
        headPic:''
      }
    }
  },
  methods: {
    userLotteryListFun(){
      let that =this;
      let data ={
        pageNumber:this.pageNumber,
        pageSize:this.pageSize
      }
      userLotteryList(data).then(res=>{
        if(res.status == 0){
              // 总页数 > 当前页数
            setTimeout(() => {
                  if(res.data.totalPage >= that.pageNumber){
                      that.pageSize = res.data.pageSize;
                      that.totalPage = res.data.totalPage;
                      that.list.push(...res.data.list.map(item=>{
                        // 现在的时间，精确到秒
                        let time = Date.parse(new Date())/1000;
                        // 开奖时间
                        let openTime =Date.parse(new Date( item.openTime)) /1000;
                        // 结束
                        if(time > openTime){
                          item.active =false;
                        }
                        // 等待开奖
                        else{
                          item.active =true;
                          item.openTime= commJs.dateHandle(openTime,'/');
                        }
                        return item;
                      }));

                      that.pageNumber = parseInt(that.pageNumber) + 1;
                  }else{
                     that.finished = true;
                  }
                      // 加载状态结束
                      that.loading = false;
              }, 500);
            }
      })
    },
    // 获取
    whoWinFun(lotteryGoodId,activityNum){
      let that =this;
      let data ={
        lotteryGoodId,
        activityNum
      }
      whoWin(data).then(res=>{
        // console.log(res);
         if(res.status == 0){
           that.show1 = !that.show1;
           that.userInfo =  Object.assign(that.userInfo, res.data);
        }else{
          commJs.Dialog2(res.msg,'error',1000,that);
        }
      })
    },
    // 查看
    check(item,type){
      if(type == 1){
        this.whoWinFun(item.lotteryGoodId,item.activityNum);
      }else{
        // 订单详情
        this.$router.push({
            path:'/robShoppingOrderDetails',
            query:{
              lotteryOrderId:item.lotteryOrderId,
              lotteryGoodId:item.lotteryGoodId,
              activityNum:item.activityNum
            }
          })
      }
    }
  },
  mounted(){
    // 判断是否登录
    if(commJs.testLogin(this,'/robShoppingorderList','/myself')==false){
      return;
    }

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.coment_lottery{
  height: 7.5rem;
}
/deep/ .yd-popup-content{
  overflow: initial;
}
.div_personnelist{
  display: flex;
  padding: .9375rem;
  .img{
    margin-right: .625rem;
    img{
      width: 2rem;
      height: 2rem;
      border-radius: 45%;
    }
  }
  .coment{
    .phone{
      font-size: 1rem;
      color: #333333;
    }
    .shop{
      margin-top: .1rem;
      color: #666666;
      font-size: .6875rem;
    }
  }
}
</style>

