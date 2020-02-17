<template>
  <div>
    <!-- 轮播图  -->
    <van-swipe :autoplay="3000" :show-indicators="false" class="banner" @change="onChange">
      <van-swipe-item v-for="(item, index) in images" :key="index" @click="btn(item,index,images)">
        <img v-lazy="item.img?item.img:item.image" v-if="images">
      </van-swipe-item>
    </van-swipe>
  </div>
</template>

<script>
export default {
  props: ["images", "vertical", "indicators", "info"],
  data() {
    return {};
  },
  methods: {
    /**
     * joker
     * 获取图片相关参数
     * @param item 点击图片的参数
     * @param key  点击图片的参数的索引
     * @param arr  全部图片
     */
    btn(item, key, arr) {
      this.$emit("introduce", { item, key, arr });
    },
    // 监听
    onChange(index) {
      this.$emit("onChange", this.images[index]);
    }
  },
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.banner {
  height: 11.8125rem;
  width: 100%;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>

