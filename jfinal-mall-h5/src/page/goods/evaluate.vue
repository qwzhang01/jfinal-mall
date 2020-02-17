<template>
  <div class="evaluate">
    <Navbar :navbar="navbar" />
    <div class="navBtn" v-if="buttomData.length > 0">
      <div class="btn_box" :style="{'height':Switch?'3.8rem':'100%'}">
          <yd-button :class="[key==active?'activeStyle':'','buttomStyle']"  type="hollow" v-for="(item,key) in buttomData" :key="key" @click.native="btn(key)">{{item}}</yd-button>
      </div>
      <div class="nav_foot_box" >
        <!-- arrow-up  -->
        <van-icon :name="!Switch?'arrow-up':'arrow-down'" @click="Switch = !Switch" size="1rem" color="#999999" />
      </div>
    </div>
     <!-- 列表 -->
      <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="PageFavouriteFun" >
        <EvaluateList  :list="list"  />
      </van-list>
  </div>
</template>

<script>
/**
 * 买家评价
 */
import EvaluateList from '../../components/evaluateList'
import Navbar from '../../components/navbar'
import {
  commentPage
} from '../../../api/goods/'
import commJs from '../../comm';
export default {
  components:{
    EvaluateList,
    Navbar
  },
  data () {
    return {
      // buttomData:['全部','最新','晒图(459)','好评(459)','中评(0)','差评(0)','质量不错','价格适中'],
      buttomData:[],
      active:0,
      Switch:false,
      footHeight:2,
      evaluateList:[],
      // 滚动加载
      loading: false,
      finished: false,
      list:[],      // 列表数据
      from:{
        goodId:'',
        pageSize:10,  // 每条显示页数
        pageNumber:1, // 当前页数
        type:1,       // 1 全部 2好评 3 中评 4差评 5晒图
        totalPage:1,  // 总页数
      },// 头部导航栏
      navbar:{
        height:'2.75rem',
        left:[
          {
            // 图标大小
            size:'25px',
            coment:'返回',
            to: '/goodsinfo',
            color:'#333333',
          }
        ],
        // 标题
        title:{
          // 内容
          coment:'评价列表',
          //颜色
          color:'#333333',
        },
        right:[]
      },

    }
  },
  methods: {
    // 点击菜单
    btn(key){
      this.active = key;
      this.from.type = parseInt(key) + 1;
      this.loading = true;
      this.finished= false;
      this.from.pageNumber = 1;
      this.list = [];
      this.PageFavouriteFun();
    },
    // 列表
    PageFavouriteFun(){
      let that =this;
      // return;
      commentPage(this.from).then(res=>{
           if(res.status == 0){
              // 总页数 > 当前页数
            setTimeout(() => {
                  if(res.data.totalPage >= that.from.pageNumber){
                      that.from.pageSize = res.data.pageSize;
                      that.from.totalPage = res.data.totalPage;
                      that.from.pageNumber = parseInt(that.from.pageNumber) + 1;
                      if(res.data.list.length > 0 ){
                          that.list.push(
                            ...res.data.list.map((item)=>{
                              item.add_time = commJs.dateHandle(item.add_time);
                              return item;
                            })
                          )
                      }

                  }else{
                     that.finished = true;
                  }
                      // 加载状态结束
                      that.loading = false;
              }, 500);
           }else{
              // 加载状态结束
              that.loading = false;
              that.finished = true;
              commJs.Dialog2(res.msg,'error',1000,that)
           }
      })
    }
  },
  mounted(){
    if(this.$route.query.goodId){
      this.from.goodId = this.$route.query.goodId;
      this.navbar.left[0].to = '/goodsinfo?goodId='+this.from.goodId;
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.navBtn{
  background: #ffffff;
  padding: 0rem .9375rem .625rem .9375rem;
  .buttomStyle{
    font-family:PingFang-SC-Medium;
    font-size:.8125rem;
    border-radius: .9375rem;
    margin-right:.3125rem;
    margin-top: 1.0625rem;
    border:  1px solid #ffffff;
    background:#f5f5f5;
    color: #666666;
  }
  .nav_foot_box{
    display: flex;
    justify-content:center;
    width: 100%;
  }
  .btn_box{
    width: 100%;
    overflow: hidden;
  }
}
.nav_foot_box{
  margin-top: 1rem;
}
.activeStyle{
  color: #D7000F !important;
  border: 1px solid #D7000F !important;
  background:rgba(247,233,234,1) !important;
}
</style>

