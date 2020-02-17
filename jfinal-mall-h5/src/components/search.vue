<template>
  <div class="search">
    <van-search
      placeholder="请输入搜索内容"
      show-action
      shape="round"
      v-model="value"
      @input="valFun"
    >
      <div slot="action" @click="onSearch">{{show}}</div>
    </van-search>

  </div>
</template>

<script>
/**
 * 搜索
 */
export default {
  props: ['search'],
  data () {
    return {
      value:'',
      show:'搜索'
    }
  },
  methods: {
    valFun(e){
      // console.log(e);
      this.value = e
      // this.show = e?'搜索':'取消'
    },
    onSearch() {
      this.$emit('searchFun',this.value);
    }
  },
  mounted(){
    this.value = this.search ? this.search : '';
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
/deep/ .van-search{
  padding: .3rem
}
</style>

