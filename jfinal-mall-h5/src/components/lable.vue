<template>
  <div>
     <!-- 筛选 -->
          <div :class="[distanceActive?'screen_fixed':'','screen']" >
            <div v-for="(item,key) in screen" :key="key" @click="screenFun(key)" :class="[screenIndex==key?'activeScreen':'','screenDiv']">
                <!-- 综合 -->
                <div class="sales" v-if="key == 0">
                  <div class="sales_text">
                    {{item}}
                  </div>
                </div>
                <!-- 价格 -->
                <div class="sales" v-else-if="key == 2">
                  <div class="sales_text">
                    {{item}}
                  </div>
                  <div class="sales_btn_icon">
                    <div><van-icon name="arrow-up" :color="arrowactive == 1?'#FB3154':'#999999'"  /></div>
                    <div><van-icon name="arrow-down" :color="arrowactive == 2?'#FB3154':'#999999'"/></div>
                  </div>
                </div>
                <!-- 筛选 -->
                <div class="screen_1" v-else-if="key == 3">
                  {{item}}<img :src="require(screenIndex == key? '../assets/screen_2.png' : '../assets/screen.png')" class="screen_icon" />
                </div>
                <div v-else>{{item}}</div>
            </div>
          </div>
  </div>
</template>

<script>
// 标签
export default {
  props: ['distanceActive','screen','screenIndex','arrowactive'],
  data () {
    return {}
  },
  methods: {
    screenFun(key){
      this.$emit('screenFun',key);
    }
  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
 // 筛选
  .screen_fixed{
    position: fixed;
    width: 100%;
    top: 0;
    z-index: 100;
    background: #ffffff;
    opacity: .8;
  }
  .screen{
    background: #ffffff;
    display: flex;
    height: 2.5rem;
    align-items:center;
    .screenDiv{
      text-align:center;
      width: 25%;
      font-family:PingFang SC;
      font-size:.75rem;
    }
    .screen_icon{
      width:.875rem;
      height: .875rem;
    }
    .sales{
      display: flex;
      align-items:center;
      justify-content:center;
      .sales_text{
        margin-right: .2rem;
      }
      .sales_btn_icon{
        div:nth-child(1){
          margin-top: .2rem;
          height: .5rem;
        }
      }
    }
    .screen_1{
      display: flex;
      align-items:center;
      justify-content:center;
      img{
        margin-left: .2rem;
      }
    }
  }
   .activeScreen{
    color: #D7000F;
  }
</style>

