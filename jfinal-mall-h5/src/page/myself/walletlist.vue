<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar" @navRight="nav_btn"/>
    <!-- 头部 -->
    <div class="haeder"  v-if=" type == 3 ">
      <div class="icon" @click="Choice">
        <span>{{time}}</span>
        <van-icon name="arrow-down" />
      </div>
      <div class="right">
        <div>支出 ￥{{outTotal}}</div>
        <div>收入 ￥{{inTotal}}</div>
      </div>
    </div>
    <!-- 列表 -->
      <van-list
          v-model="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="typeHandle"
        >
        <div class="list">
            <div class="row" v-for="(item,key) in list" :key="key">
              <div class="row_left">
                <div>
                  {{
                    type == 1?'充值':
                    type == 2?'提现':
                    type == 3?
                    item.amountType == 1?'平台推广佣金':
                    item.amountType == 2?'店铺推广佣金':
                    item.amountType == 3?'商品优惠返现':
                    item.amountType == 4?'充值 ':
                    item.amountType == 5?'商品销售收入 ':
                    item.amountType == 6?'提现 ':
                    item.amountType == 7?'消费 ':'推广客户首单平台奖励'
                    : ''
                  }}
                  {{
                    type == 3?
                      item.frozenFlag==1?'(冻结)':
                      item.frozenFlag==3?'(取消)':''
                    :''
                  }}
                  </div>
                <div>{{item.createTime?item.createTime:item.applyTime}}</div>
              </div>
              <!-- 增加 -->
              <div  class="row_right">
                <div v-if="type == 1 || (type == 3 && item.inOutFlag == 1)">+ {{item.amount}}</div>
                <!-- 减少 -->
                <div  class="row_right reduce" v-if="type == 2 || (type == 3 && item.inOutFlag == 2)">- {{item.amount}}</div>
                <!-- 充值记录 -->
                <div class="bottom" v-if=" type == 1 || type == 2 ">
                  {{
                     type == 1? item.payStatus == 0 ? '待支付' : item.payStatus== 1 ? '充值成功' : '交易关闭'
                     :
                     type == 2? '':''
                  }}
                </div>
              </div>

            </div>
        </div>
      </van-list>
    <!-- 提现记录 -->

    <!-- 筛选弹窗 -->
    <van-actionsheet
      v-model="show"
      cancel-text="取消"
      :actions="actions"
      @select="onSelect"
    />

    <!-- 日期选择 -->
      <van-popup
        v-model="show2"
        position="bottom"
      >
        <van-datetime-picker
          v-model="currentDate"
          type="year-month"
          :min-date="minDate"
          :max-date="maxDate"
          :formatter="formatter"
          @cancel="cancel"
          @confirm="confirm"
        />
      </van-popup>
  </div>
</template>

<script>
import Navbar from '../../components/navbar';
import {
  // 收支明细
  BalanceList,
  // 充值记录
  ChargeList,
  //提现记录
  WithdrawalList
}from '../../../api/'
import comm_js from '../../comm/'
export default {
components:{
    Navbar,
  },
  data () {
    return {
      // 时间
      currentDate: new Date(),
      // 开始时间
      minDate: new Date(2015, 0, 1),
      maxDate:new Date(),
      show2:false,
      // 筛选弹窗
      show: false,
      time:'本月',
      // 筛选列表
      actions: [
        {
          name: '平台推广佣金',
          index:1
        },
        {
          name: '店铺推广佣金',
          index:2
        },
        {
          name: '特殊商品优惠返现',
          index:3
        },
        {
          name: '充值',
          index:4
        },
        {
          name: '商品销售收入',
          index:5
        },
        {
          name: '提现',
          index:6
        },
        {
          name: '消费',
          index:7
        },
        {
          name: '推广客户首单平台奖励',
          index:8
        }
      ],
      // 头部导航栏
      navbar:{
          height:'2.75rem',
          left:[
            {
            // 图标大小
            size:'25px',
            coment:'返回',
            to:'/wallet',
            color:'#333333',
            }
          ],
          // 右边
         right:[
           {
             // icon名字
             icon_name:'',
             // 内容 (选填)
              coment:'',
             // 颜色
             color:'#999999',
             to:''
           }
         ],
          // 标题
          title:{
            // 内容
            coment:'',
            //颜色
            color:'#333333',
          }
        },
      // 类型 1：充值 2：提现 3：明细
      type:0,
      // 当前页数
      pageNumber:1,
      // 每页显示条数
      pageSize:10,
      // 总页数
      totalPage:1,
       // 上拉加载状态
      loading: false,
      finished: false,
      // 列表
      list:[],
      // 年
      year:'',
      // 月
      month:'',
      // 类型 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费
      amountType:0,
      // 支出
      outTotal:0,
      // 收入
      inTotal:0
    }
  },
  methods: {

    // 筛选
    nav_btn(data){
      this.show = true;
    },

    // 筛选内容
    onSelect(item) {
      this.show = false;
      // console.log(item)
      this.list=[];
      this.pageNumber = 1;
      this.amountType = item.index;
      this.BalanceListFun();
      comm_js.Dialog2(item.name,'',1500,this);
    },

    // 时间
     formatter(type, value) {
      if (type === 'year') {
        return `${value}年`;
      } else if (type === 'month') {
        return `${value}月`
      }
      return value;
    },

    // 取消日期选择
    cancel(){
      this.show2 = false;
    },

    // 确定日期
    confirm(data){
      let Year = new Date(data).getFullYear();
      let Month = new Date(data).getMonth();
      this.data_judge(Year,Month);
      this.show2 = false;
    },

    // 日期判断
    data_judge(Year,Month){
        // 获取当前的年、月
        let newYear = new Date().getFullYear();
        let newMonth = new Date().getMonth();
        // 是否是本年
        if( newYear == Year ){
            // 是否是本月
            if(newMonth == Month){
                this.time = '本月';
            }else{
                this.time = `${Month+1}月`;
            }
        }else{
          this.time =`${Year} 年 ${Month+1} 月 `;
        }
        this.list=[];
        this.pageNumber = 1;
        this.year = Year;
        this.month = Month+1;
        this.BalanceListFun();
    },

    // 点击月份
    Choice(){
      this.show2 = true;
    },

    // 收支明细
    BalanceListFun(){
      let that =this;
      let params ={
        pageNumber:this.pageNumber,
        pageSize:this.pageSize,
        year:this.year,
        month:this.month,
        amountType:this.amountType,
        userId:sessionStorage.getItem('userId')
      }
      BalanceList(params).then(res=>{
        if(res.status == 0){
            that.handleData(res);
          }
      })
    },

    // 充值记录
    ChargeListFun(){
      let that =this;
      let params ={
        pageNumber:this.pageNumber,
        pageSize:this.pageSize,
        userId:sessionStorage.getItem('userId')
      }
      ChargeList(params).then(res=>{
           if(res.status == 0){
            that.handleData(res);
          }
      });
    },

    // 提现记录
    WithdrawalListFun(){
      let that =this;
      let params ={
        pageNumber:this.pageNumber,
        pageSize:this.pageSize,
        userId:sessionStorage.getItem('userId')
      }
      WithdrawalList(params).then(res=>{
          if(res.status == 0){
            that.handleData(res);
          }
      })
    },

    // 数据处理
    handleData(res){
      let that =this;
      // 总页数 > 当前页数
      setTimeout(() => {
          // 明细
         if(this.type == 3){
                  that.outTotal = comm_js.abs(res.data.outTotal,2);
                  that.inTotal = comm_js.abs(res.data.inTotal,2);
              if(res.data.page.totalPage >= that.pageNumber){
                  that.pageSize = res.data.page.pageSize;
                  that.totalPage = res.data.page.totalPage;
                  that.list.push(...res.data.page.list);
                  that.pageNumber = parseInt(that.pageNumber) + 1;
              }else{
                  that.finished = true;
              }
              // 加载状态结束
              that.loading = false;
          }else{
              if(res.data.totalPage >= that.pageNumber){
                that.pageSize = res.data.pageSize;
                that.totalPage = res.data.totalPage;
                that.list.push(...res.data.list);
                that.pageNumber = parseInt(that.pageNumber) + 1;
              }else{
                  that.finished = true;
              }
              // 加载状态结束
              that.loading = false;
          }



        }, 500);
      // console.log(that.list);
    },

    // 类型判断 && 上拉加载
    typeHandle(){
        // 余额明细
        if(this.type == 3){
          this.navbar.right[0].coment ='筛选';
          // 默认情况是本月
          this.year = new Date().getFullYear();
          this.month = new Date().getMonth() + 1;
          this.BalanceListFun();
        }
        // 充值
        else if(this.type == 1){
          this.ChargeListFun();
        }
        // 提现
        else if(this.type == 2){
          this.WithdrawalListFun();
        }
    }
  },
  mounted(){
    if(this.$route.query){
        this.type = this.$route.query.type;
        this.navbar.title.coment = this.$route.query.title;
        this.typeHandle();
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 头部
.haeder{
  background: #ffffff;
  height: 3.1875rem;
  width: 100%;
  display: flex;
  justify-content:space-between;
  align-items:center;
  padding: 0 .9375rem;
  margin-bottom: .625rem;
  span{
    margin-right: .2rem;
  }
  .icon{
    padding: .3rem .5rem;
    background: #F0F0F0;
    border-radius:1.4375rem;
    color: #333333;
    display: flex;
    justify-content:center;
    align-items:center;
    font-size: .75rem;
  }
  .right{
    color: #999999;
    font-size: .75rem;
     div:nth-child(1){
       margin-bottom: .1rem;
     }
  }
}
// 列表
.list{
  background: #ffffff;
  .row{
    display:flex;
    height: 4.5rem;
    padding: 0 .9375rem;
    align-items:center;
    justify-content:space-between;
    border-bottom: 1px solid #E5E5E5;
    .row_left{
      div:nth-child(1){
       color: #333333;
       font-size: 1rem;
       font-family:PingFang SC;
       margin-bottom: .5rem;
      }
      div:nth-child(2){
       color: #999999;
       font-size: .6875rem;
       font-family:PingFang SC;
      }
    }
    .row_right{
      color: #333333;
      font-size: 1rem;
      font-family:DIN;
      .bottom{
        font-size: .6875rem;
        color: #999999;
        text-align: right;
        margin-top: .5rem;
      }
    }
    .reduce{
      color: #D7000F;
    }
  }
  .row:nth-last-child(1){
    border: none;
  }
}

// 日期
/deep/ .van-picker__cancel{
  color: #666666;
}
/deep/ .van-picker__confirm{
  color: #D7000F;
}
</style>

