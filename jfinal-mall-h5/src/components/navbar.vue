<template>
  <!-- 头部导航栏 -->
  <div>
      <yd-navbar
        :bgcolor="navbar.background"
        :height="navbar.height"
        fontsize="1.1rem"
        style="z-index:20"
      >
        <!-- 左侧 -->
        <div slot="left" v-for="(item,key) in navbar.left" :key="key" @click="navbtn(item)">
            <router-link :to="item.to" v-if="item.icon_name||item.coment" >
              <yd-icon :name="item.icon_name" :size="item.size" :color="item.color" v-if="item.icon_name"></yd-icon>
              <yd-navbar-back-icon v-else :style="{ 'color':item.color }" >{{item.coment}}</yd-navbar-back-icon>
            </router-link>
        </div>
        <!-- 标题 -->
        <div slot="center" v-if="navbar.title.coment|| navbar.title.coment_img">
         <span :style="{ 'color':navbar.title.color  }" v-if="navbar.title.coment" class="title">{{navbar.title.coment}}</span>
         <img :src="navbar.title.coment_img" alt="icon图片"  v-if="navbar.title.coment_img" />
        </div>
        <!-- 右侧 -->
        <div slot="right" v-for="(item,key) in navbar.right" :key="key">

            <!-- 跳转 -->
            <router-link :to="item.to" class="right" v-if=" item.to">
              <!-- icon -->
              <yd-icon :name="item.icon_name" :size="item.size" :color="item.color" v-if="item.icon_name"></yd-icon>
              <!-- img -->
              <img :src="item.icon_img" alt="icon图片" :style="{ 'width':item.size }" v-if="item.icon_img" />
              <div :style="{ 'color':item.color }" class="font" v-if="item.coment">{{item.coment}}</div>
            </router-link>
            <!-- 无跳转 -->
            <div v-if=" (item.icon_img||item.icon_name|| item.coment) && !item.to" @click="navbtn(item)" class="imgBtn">
                <!-- icon -->
                <yd-icon :name="item.icon_name" :size="item.size" :color="item.color" v-if="item.icon_name"></yd-icon>
                <!-- img -->
                <img :src="item.icon_img" alt="icon图片" :style="{ 'width':item.size }" v-if="item.icon_img" />
                <div :style="{ 'color':item.color }" class="font" v-if="item.coment">{{item.coment}}</div>
            </div>
        </div>
      </yd-navbar>
  </div>
</template>

<script>
/**
 * ============================
 * 头部导航栏
 * ============================
 *
 *  joker
 *
 * 字段说明：
 * @navbar object {
 *        // 背景色
 *        background:'',
 *        // 高度
 *         height:'',
 *
 *        // 左边
 *        left:[
 *          {
 *            // icon名字
 *            icon_name:'',
 *            // 内容 (选填)
 *             coment:''，
 *            // 颜色
 *            color:'',
 *            // 目标地址
 *            to:'',
 *            // 图标大小
 *            size:'',
 *            // icon图片
 *            icon_img:'',
 *          }
 *        ],
 *
 *        // 右边
 *        right:[
 *          {
 *            // icon名字
 *            icon_name:'',
 *            // 内容 (选填)
 *             coment:''，
 *            // 颜色
 *            color:'',
 *            // 目标地址
 *            to:'',
 *            // 图标大小
 *            size:'',
 *            // 点击起的方法
 *            fn: '',
 *            // icon图片
 *            icon_img:'',
 *          }
 *        ],
 *         // 标题
 *        title:{
 *          // 内容
 *          coment:'',
 *          // 颜色
 *          color:'',
 *          coment_img:'',
 *        }
 * }
 *
 * PS:
 * 左侧：
 * 首先判断icon_name，如果值为空，则判断coment
 * 标题：
 * 首先判断coment，如果值为空，则判断coment_img
 * 右侧：
 * 判断coment，如果值为空，则不显示
 * 判断icon_img，如果值为空，则不显示
 * 判断coment，如果值为空，则不显示
 *
 */
export default {
  props: ['navbar'],
  data () {
    return {

    }
  },
  methods: {
    // 点击
    navbtn(data){
     this.$emit('navRight',data);
    }
  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
  /deep/.yd-navbar{
    font-size: .8rem;

  }
  /deep/ .right{
    display: flex;
    align-items:center;
    margin-left: .8rem;
  }
  .imgBtn{
    margin-left: .8rem;
  }

</style>

