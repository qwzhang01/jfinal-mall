<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" @navRight="nav_right"/>
    <!-- 评论 -->
    <div  class="evaluate">
        <div v-for="(items,index) in arrList.list" :key="index">
          <div class="header">
            <!-- 商品 -->
            <div class="goods">
                <img :src="items.goodImg" alt="商品" @click="introduceSelf(items)" />
              <!-- 商品描述 -->
              <div class="describe">
                <div>商品描述：</div>
                <yd-cell-item>
                  <yd-rate
                      slot="left"
                      v-model="items.descScore"
                      :show-text="shopDescribe"
                      size="1rem"
                      color="#999999"
                      active-color="#D7000F"
                      padding=".5rem"
                      ></yd-rate>
                  </yd-cell-item>
              </div>
            </div>
            <!-- 评论内容 -->
            <yd-cell-item class="textarea">
                <yd-textarea slot="right" placeholder="宝贝怎么样？ 请写下您的评价" maxlength="100" v-model="items.comment" ></yd-textarea>
            </yd-cell-item>
            <!-- 图片 -->
            <div class="image">
              <!-- 上传的图片 -->
              <div class="shop" v-for="(item,key) in items.showImg" :key="key">
                <span class="icon">
                  <img src="../../assets/del.png" alt="icon" @click="del(index,key)" />
                </span>
                <img :src="item" alt="商品图片" class="shop_img" @click="look_shop(items.imgarr,key)" />
              </div>
              <!-- 上传图片iocn -->

              <div class="upload"  v-if="items.imgarr.length > 2? false : true"  @change="uploadIndex = index">
                <van-uploader :after-read="upload" name="uploader" accept="image/*" multiple>
                  <div><img src="../../assets/camera.png" alt="相机" /></div>
                  <div>上传凭证<br/>（最多3张）</div>
                </van-uploader>
              </div>

            </div>
            <!-- 匿名 -->
            <div class="anonymous">
              <van-checkbox v-model="items.anonymous" checked-color="#D7000F">匿名</van-checkbox>
              <div>你写的评价以匿名的形式展现</div>
            </div>
          </div>
        </div>
        <!-- 店铺评分 -->
        <div class="score">
          <!-- 标题 -->
          <div class="title">
            <van-icon name="shop-o"  size="15px" class="icon" />
            <span>店铺评分</span>
          </div>
          <div class="operation" v-for="(item,k) in score" :key="k">
            <span>{{item}}</span>
            <yd-cell-item>
                <yd-rate
                v-if="k==0"
                slot="left"
                v-model="arrList.logisticsScore"
                :show-text="['很差','还行','一般','挺好','非常好']"
                size="1.2rem"
                color="#999999"
                active-color="#D7000F"
                padding=".7rem"
                ></yd-rate>
                <yd-rate
                v-else
                slot="left"
                v-model="arrList.sellerScore"
                :show-text="['很差','还行','一般','挺好','非常好']"
                size="1.2rem"
                color="#999999"
                active-color="#D7000F"
                padding=".7rem"
                ></yd-rate>
            </yd-cell-item>
          </div>
        </div>
    </div>
  </div>
</template>

<script>
import {Uploader} from 'vant'
import comm_js from '../../comm/'
import Navbar from '../../components/navbar'
import {
 GetGood,
 Comment,
 uploadImgBase64
} from '../../../api/index.js'
export default {
  components:{
    Navbar
  },
  data () {
    return {
      // 选择上传商品图片的索引
      uploadIndex:null,
      // 头部导航栏
      navbar:{
          height:'2.75rem',
           left:[
             {
              // 图标大小
              size:'25px',
              coment:'返回',
              to:'/orderlist?tabl='+this.$route.query.tab1,
              color:'#333333',
             }
           ],
           right:[{
              // icon名字
              icon_name:'',
              // 内容 (选填)
              coment:'发布',
              // 颜色
              color:'#D7000F',
             }],
          // 标题
          title:{
            // 内容
            coment:'发表评价',
            //颜色
             color:'#333333',
          }
        },
      // 复选框
      checked: true,
      // 评价菜单
      score:['物流服务','服务态度'],
      // 评价数据
      arrList:[],
      // 商品描述
      shopDescribe:['很差','还行','一般','挺好','非常好']
    }
  },
  methods: {
    // 发布
    nav_right(){
      console.log(JSON.stringify(this.arrList));
      let that =this;
      let data = JSON.stringify(this.arrList);
      Comment(data).then(res=>{
          if(res.status == 0){
            comm_js.Dialog2("发布成功！",'success',1000,that);
            setTimeout(()=>{
              this.$router.push({
                path:'/orderlist?tabl='+this.$route.query.tab1
              })
            },1200)
          }else{
            comm_js.Dialog2(res.msg,'error',1000,that);
          }
      })
    },
    // 上传
    upload(file){
      this.$dialog.loading.open('加载中....');
      comm_js.compress(file,3,0,0,0.7,this.arrList.list[this.uploadIndex].showImg,this).then(res=>{
         // 添加图片
        res.forEach((item,index)=>{
          this.uploadImgBase64Fun(item,this.uploadIndex);
        });
      })
    },
    // 图片上传
    uploadImgBase64Fun(item,uploadIndex){
        let that =this;
        uploadImgBase64(item).then(res=>{
          if(res.status == 0){
              that.arrList.list[uploadIndex].showImg.push(res.data.showPath)
              that.arrList.list[uploadIndex].imgarr.push(res.data.savePath)
          }
          that.$dialog.loading.close();
        })
    },
    // 预览图片
    look_shop(imgarr,key){
      comm_js.viewImg(imgarr,key);
    },
    /**
     * 点击跳转
     */
    introduceSelf(data){
      let that = this;
      comm_js.jump({item:data},'',that);
    },
    // 删除图片
    del(index,key){
      let that =this;
      comm_js.Dialog('温馨提示','你确定删除？').then(res=>{
        if( res === 'confirm'){
          this.arrList.list[index].imgarr.splice(key,1);
          comm_js.Dialog2('删除成功！','success',1000,that)
        }
      })
    },
    // 获取商品
    GetGoodFun(orderId){
      let that =this;
      let params ={
          userId: sessionStorage.getItem('userId'),
          orderId
        }
      GetGood(params).then(res=>{
        if(res.status == 0){
          var arr = []
          // 数据处理
          res.data.forEach((item,index)=>{
              arr.push({
                ...item,
                // 描述符合事实评分
                descScore:5,
                // 评语
                comment:'',
                // 匿名 1：是 2：否
                anonymous:1,
                // 显示的图片
                showImg:[],
                // 传的图片
                imgarr:[],
                // 评语
                comment:''
              })
          })
          that.arrList = {
            list:arr,
            orderId,
            // 服务评分
            sellerScore:0,
            // 配送评分
            logisticsScore:0,
          }
          // console.log(that.arrList);
        }
      })
    }
  },
  mounted(){
    if(this.$route.query.orderId){
      this.GetGoodFun(this.$route.query.orderId);
    }
   if(comm_js.equipment().width < 360){
      this.shopDescribe = [];
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.goods {
  border-bottom: 1px solid #E6E6E6;
   display: flex;
  img{
    border: .0625rem solid #E6E6E6;
    width: 2.5rem;
    height: 2.5rem;
    margin:.3rem 0;
  }
}
.describe{
  width: 100%;
  display: flex;
  align-items:center;
  div:nth-child(1){
    width: 7rem;
    font-size: .8rem;
    margin: 0 1rem;
    text-align: center;
  }
}
.evaluate{
   margin-bottom: .625rem;
}

.header{
  background: #ffffff;
  padding: 0 .9375rem;
  /deep/ .yd-cell-left{
    width: 100%;
  }

  // 输入框
  /deep/ .yd-cell-item{
    width: 100%;
    padding-left:0;
    /deep/ .yd-cell-right{
      padding-right:0;
    }
  }

  /deep/ .yd-textarea>textarea{
    font-size: .875rem;
    font-family:PingFang-SC-Medium;
  }
  // 商品图片
  .image{
    display: flex;
    .shop{
      width: 5.1875rem;
      height: 5.1875rem;
      position: relative;
      border:1px solid #E6E6E6;
      margin-right: .625rem;
      .icon {
        position: absolute;
        top: -.5rem;
        right: -.5rem;;
        img{
          width: 1.375rem;
          height: 1.375rem;
        }
      }
      .shop_img{
      width: 100%;
      height: 100%;
      }
    }
    // 上传图片icon
    .upload{
      width: 5.1875rem;
      height: 5.1875rem;
      text-align: center;
      border:1px solid #E6E6E6;
      color: #999999;
      font-size: .6875rem;
      div:nth-child(1){
        margin-top: .45rem;
        margin-bottom: .2rem;
        img{
          width: 1.4375rem;
          height: 1.1875rem;
        }
      }
    }
  }
  // 匿名
  .anonymous{
    display: flex;
    justify-content:space-between;
    height: 3.125rem;
    align-items:center;
    color: #333333;
    font-size: .9375rem;
    // border-top:1px solid #E6E6E6;
    div:nth-last-child(1){
      font-size: .875rem;
      color: #999999;
    }
  }
}

// 店铺评分
.score{
  padding: 0 .9375rem;
  // margin-top: .9375rem;
  background: #ffffff;
  .title{
    font-size: .9375rem;
    padding-top: .9375rem;
    border-top:1px solid #E6E6E6;
    .icon{
      position:relative;
      top: -.05rem;
      margin-right: .625rem;
    }
    display: flex;
    align-items:center;
    color: #333333;
    font-family:PingFang-SC-Medium;
  }
  .operation{
    display: flex;
    font-size: .9375rem;
    align-items:center;
    font-size: #333333;
  }
  /deep/ .yd-cell-item{
    padding-left: .9375rem;
  }
  /deep/ .yd-rate-text{
    color: #999999;
  }
}
/deep/ .yd-cell-item:not(:last-child):after{
  display: none;
}
</style>

