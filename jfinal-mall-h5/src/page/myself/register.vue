<template>
  <div>
     <!-- 导航 -->
    <Navbar :navbar="navbar" />

    <!-- 表单 -->
    <div class="fromSubmint">
      <!-- 手机号 -->
      <van-cell-group>
        <van-field  v-model="from.mobile"  clearable type="number" @input="change" placeholder="请输入手机号码" />
      </van-cell-group>
      <!-- 验证码 -->
      <van-cell-group>
        <van-field
          v-model="from.smsCode"
          @input="change"
          center
          clearable
          placeholder="请输入短信验证码"
        >
          <van-button slot="button" size="small" :disabled="smsactive" type="default" @click="smsFun" >{{comentSms}}</van-button>
        </van-field>
      </van-cell-group>

      <!-- 输入密码 -->
      <van-cell-group>
      <van-field
        v-model="from.password"
        :type="password"
        placeholder="请输入密码"
        :right-icon="password == 'password'?'closed-eye':'eye-o'"
        clearable
        @input="change"
        @click-right-icon="transformation"
      />
      </van-cell-group>

      <!-- 推荐人电话 -->
      <van-cell-group v-if="false">
        <van-field  v-model="from.firstLeaderMobile"  clearable type="number" @input="change" placeholder="请输入推荐人手机号" />
      </van-cell-group>

      <!-- 按钮 -->
      <van-button type="danger" size="large" :disabled="disabled" style="background:#D7000F;" @click.native="submit" class="submit">注册</van-button>
    </div>

  </div>
</template>
<script>
import Navbar from '../../components/navbar';
import comm from '../../comm/';
import {
  sms,
  signUp
} from '../../../api/';
import { setInterval, setTimeout } from 'timers';
export default {
components:{
    Navbar
  },
   data () {
    return {
      // 验证码状态
      smsactive:false,
      comentSms:'发送验证码',
      second:60,
      // 提交
      disabled:true,

      password:'password',
      // 头部导航栏
      navbar:{
        height:'2.75rem',
        left:[
          {
            // 图标大小
            size:'25px',
            coment:'返回',
            to: '/login',
            color:'#333333',
          }
        ],
        // 标题
        title:{
          // 内容
          coment:'注册',
          //颜色
            color:'#333333',
        },
        right:[
          {
            coment:'登录',
            to: '/login',
            color:'#666666',
          }
        ]
      },
      from:{
        mobile:'',
        smsCode:'',
        password:'',
        firstLeaderMobile:''
      }
    }
  },
  methods: {
    // 提交
    submit(){
      let that =this;
      signUp(this.from).then(res=>{
        if(res.status == 0){
          setTimeout(()=>{
              that.$router.push({
                path:'/login',
                query:{}
              })
          },1000)
          comm.Dialog2(res.msg,'success',1000,that);
        }else{
          comm.Dialog2(res.msg,'error',1000,that);
        }
      })
    },
    // 密码显示和转换
    transformation(){
      if( this.password == 'password' ){
          this.password = 'text';
      }else{
        this.password = 'password';
      }
    },
    // 发送验证码
    smsFun(){
      let that =this;
      // 判断电话号码
      if( comm.test(this.from.mobile, '电话号码',[1,3],this) == false ){
        return;
      }
      sms({phone:this.from.mobile}).then( res=> {
        if(res.status == 0){
          that.second = 60;
          that.setTime();
        }
        comm.Dialog2(res.msg,'success',1000,that);
      })
    },
    // 监听
    change(){
      if(this.from.mobile && this.from.smsCode && this.from.password){
          this.disabled = false;
      }else{
         this.disabled = true;
      }
    },
  // 验证码
  setTime(){
      var interId = setInterval(()=>{
        // 大于1
        if(this.second > 1){
          this.second --;
          this.smsactive = true;
          this.comentSms =`${this.second}s 后再发送`;
        }else{
          // 清除定时器
          clearInterval(interId);
          this.smsactive = false;
          this.comentSms ='发送验证码';
        }
      },1000);
  }

  },
  mounted(){

  }
}
</script>

<style lang="less" scoped>
/deep/ .van-cell-group ::-webkit-input-placeholder{
  color: #999999;
  font-family:PingFang SC;

}

/deep/ .van-cell-group{
  color: #333333;
  font-family:PingFang SC;
  font-size: 1rem;
  border-bottom: 1px solid #ebedf0;
}
#group{
  background: #ffffff;
}
/deep/ .van-button::before{
  content: none;
}
/deep/ .van-button--default{
  border: none;
}
/deep/ .van-cell{
  padding: 15px 0px;
  line-height: inherit;
}

[class*=van-hairline]::after{
  content:none;
}

.from .van-button, .submit{
  height: 2.8125rem;
  font-size:1rem;
  font-family:PingFang SC;
  border-radius: .25rem;
}
// /deep/.van-button--small{
//   height: 0;
// }
.submit{
  margin-top:2.375rem;
}
.fromSubmint{
  padding: 2.8125rem .9375rem 0 .9375rem;
  background: #ffffff;
  height: -webkit-fill-available;
}

</style>

