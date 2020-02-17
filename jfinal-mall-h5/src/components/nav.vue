<template>
  <div>
    <!-- 底部导航 -->
  <yd-tabbar>
        <yd-tabbar-item title="首页"  :active="btn==1?true:false"  style="font-size:.8rem;" link="/" >
             <img slot="icon" style="height:1.5rem;width:1.5rem;" :src="btn==1? require('../assets/page_1.png') : require('../assets/page_0.png') " >
        </yd-tabbar-item>
        <!-- <yd-tabbar-item title="分类" :active="btn==2?true:false"  style="font-size:.6rem;" link="/categoryList">
             <img slot="icon" style="height:1.5rem;width:1.5rem;" :src=" btn==2? require('../assets/class_1.png'): require('../assets/class_0.png') " >
        </yd-tabbar-item> -->
        <yd-tabbar-item title="购物车" :active="btn==3?true:false" style="font-size:.8rem;" link="/cart">
            <yd-badge slot="badge" type="danger" v-if="cart_num">{{cart_num}}</yd-badge>
            <img slot="icon" style="height:1.5rem;width:1.5rem;" :src="btn==3? require('../assets/cart_1.png'):require('../assets/cart_0.png')" >
        </yd-tabbar-item>
        <yd-tabbar-item title="我的" :active="btn==4?true:false"  style="font-size:.8rem;" link="/myself">
            <img slot="icon" style="height:1.5rem;width:1.5rem;" :src="btn==4? require('../assets/my_1.png'):require('../assets/my_0.png')" >
        </yd-tabbar-item>
    </yd-tabbar>
  </div>
</template>

<script>

export default {
  props: ["btn",'cart_num'],
  data () {
    return {

    }
  },
  methods: {

  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
  /deep/ .yd-tabbar .yd-tabbar-active{
    color: #c9151d !important;
  }
  /deep/ .yd-tabbar{
    height: 3.0625rem;

  }
</style>

