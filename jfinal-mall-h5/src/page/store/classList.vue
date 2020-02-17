<template>
  <div>
    <Navbar :navbar="navbar" />
    <!-- 列表 -->
     <van-list v-model="loading" :finished="finished" finished-text="没有更多了" @load="goodListFun"  id="screen">
      <List :list_type="1" :theme="3" :list="list"  @introduce="goodFun" class="like"  />
    </van-list>
  </div>
</template>

<script>
import Navbar from '../../components/navbar';
import List   from '../../components/list';
import {
  goodList
} from '../../../api/store'
import commJs from '../../comm';
export default {
  components:{
    Navbar,
    List
  },
  data () {
    return {
      store_id:'',
      categoryId:'',
      loading: false,
      finished: false,
      pageNumber:1,
      pageSize:10,
      totalPage:1,
      list:[],
      // 头部导航栏
      navbar:{
        height:'2.75rem',
        left:[
          {
            // 图标大小
            size:'25px',
            coment:'返回店铺',
            to: '/storepage',
            color:'#333333',
          }
        ],
        // 标题
        title:{
          // 内容
          coment:'店铺商品列表',
          //颜色
            color:'#333333',
        },
        right:[]
      },
    }
  },
  methods: {
    goodListFun(){
       let that = this;
      let data ={
        storeId: this.store_id,
        type: 4,
        categoryId:this.categoryId,
        pageNumber:this.pageNumber,
        pageSize:this.pageSize
      }
      goodList(data).then(res=>{
        if(res.status == 0){
            // 总页数 > 当前页数
            setTimeout(() => {
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
               }, 500);
             }
      })
    },
    goodFun(item){
      // console.log(item)
       let url = '/storepage?store_id='+this.store_id;
      commJs.jump(item,this,url);
    }
  },
  mounted(){
      if(this.$route.query.storeId && this.$route.query.categoryId){
        this.store_id = this.$route.query.storeId;
        this.categoryId = this.$route.query.categoryId;
        this.navbar.left[0].to = '/storepage?store_id='+this.store_id+'&u='+this.$route.query.u;
        // console.log(this.navbar.left[0].to)
        this.navbar.title.coment = this.$route.query.title;
      }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 商品列表
.like{
    background: #ffffff;
    .title{
        font-family: PingFang-SC-Bold;
        color: #333333;
        font-size: 1rem;
        padding-top: 0.9375rem;
        padding-bottom: 1.25rem;
    }
   /deep/ .yd-list-title{
      font-weight:inherit;
      color: #333333;
      font-size: .875rem;
      margin: .625rem 0;
  }
  /deep/ .demo-list-price{
      color: #D7000F;
      font-size: 1rem;
  }
}

</style>

