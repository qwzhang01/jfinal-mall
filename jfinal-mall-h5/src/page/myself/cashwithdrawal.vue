<template>
  <div>
    <!-- 头部导航 -->
    <Navbar :navbar="navbar"/>

    <div>
      <!-- 银行 -->
      <div class="bank">
        <div class="bank_name">
          <div>{{userCard.username}}</div>
          <div>{{userCard.opening_bank}}</div>
        </div>
        <div class="bank_number">{{userCard.bank_card}}</div>
      </div>
      <!-- 提现 -->
      <div class="withdrawal">
        <div class="title">提现金额</div>
        <div class="input">
          <span>￥</span>
          <yd-input
            type="number"
            :show-clear-icon="true"
            :show-error-icon="false"
            :show-success-icon="false"
            :show-required-icon="false"
            v-model="money"
            max="10"
            placeholder="请输入金额"
          >
          </yd-input>

        </div>
        <div class="foot">
          <div>可用余额：{{z_money}}元</div>
          <div @click=" money = z_money ">全部提现</div>
        </div>
      </div>
    </div>
    <!-- 确认提现 -->
    <div class="bottom">
      <yd-button size="large" type="danger" style="background: #D7000F;"  @click.native="moneyFun">确认提现</yd-button>
    </div>

  </div>
</template>

<script>
import Navbar from '../../components/navbar';
import { Dialog } from 'vant';
import comm_js from '../../comm/'
import {
  BalanceDetail
}from '../../../api/'
export default {
components:{
    Navbar,
  },
  data () {
    return {
       // 是否开启
      disabled:true,
      // 总金额
      z_money:500,
      // 钱
      money:'',
      // 头部导航栏
      navbar:{
          height:'2.75rem',
          left:[
            {
              // 图标大小
              size:'25px',
              coment:'返回',
              to:'/authentication',
              color:'#333333',
            }
          ],
          // 标题
          title:{
            // 内容
            coment:'提现',
            //颜色
            color:'#333333',
          },

        },
        // 用户信息
        userCard:{
          // 用户名
          username:'',
          // 银行卡号
          bank_card:'',
          // 开户支行
          opening_bank:''
        }
    }
  },
  methods: {
    // 提现
    moneyFun(){
      if(!this.money || this.money <= 0){
        comm_js.Dialog2('请输入提现金额','',1500,this);
        return;
      }
      // 大于提现金额
      if(this.z_money < this.money){
        comm_js.Dialog2('提现金额不能大于可用金额','',1500,this);
        return;
      }
      // 存储
      let  user_card = JSON.parse(sessionStorage.getItem('user_card'));
      user_card.amount = this.money;
      sessionStorage.setItem("user_card",JSON.stringify(user_card));
      this.$router.push("/verification");
    },
   // 获取钱包详情
    BalanceDetailFun(){
      let that = this;
      let params = {
        userId:sessionStorage.getItem('userId')
      }
      BalanceDetail(params).then(res=>{
       if(res.status == 0){
          // 余额
          that.z_money = res.data.availableAmount;
       }
      })
    }


  },
  mounted(){
    // 获取存储
    let  user_card = sessionStorage.getItem('user_card');
    if(user_card){
        this.userCard = JSON.parse(user_card);
        this.userCard.bank_card = comm_js.manglingFormatCardNumber(this.userCard.bank_card);
        this.BalanceDetailFun();
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 银行
.bank{
  display: flex;
  height: 4.5625rem;
  width: 100%;
  background: #ffffff;
  justify-content:space-between;
  align-items:center;
  padding: 0 .9375rem;
  .bank_name{
    div:nth-child(1){
      font-size:1rem;
      color: #333333;
    }
    div:nth-child(2){
      margin-top: .3125rem;
      font-size:.75rem;
      color: #999999;
    }
  }
  .bank_number{
      font-size:.75rem;
      color: #999999;
  }
}

// 提现
.withdrawal{
  margin-top: .625rem;
  height: 6.0625rem;
  background: #ffffff;

  padding: 0 .9375rem;
  .title{
    color: #333333;
    font-size: .6875rem;
    font-family:PingFang SC;
    padding-top: .9375rem;
  }
  .input{
    display: flex;
    justify-content:space-between;
    align-items:center;
    margin-top: .2rem;
    margin-bottom: .2rem;
    span{
      width: .6875rem;
      margin-right: .2rem;
      margin-top: .1rem;
    }
    height: 2rem;
    /deep/ input{
      height: 2rem;
      font-size: 1rem;
      font-family:DIN;
    }
  }
  .foot{
    display: flex;
    justify-content:space-between;
    font-size: .75rem;
    div:nth-child(1){
      color: #999999;
    }
    div:nth-child(2){
      color: #33A3F9;
    }
  }
}

// 提交
.bottom{
  padding: 0 .9375rem;
  font-family:PingFang SC;
  font-size: 1rem;
}

</style>

