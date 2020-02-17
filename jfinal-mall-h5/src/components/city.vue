<template>
  <div>
    <!-- 地址列表 -->
    <van-cell
    v-for="(item,key) in list_arr"
    :key="key"
    :title="item.name"
    @click.native="btn_address(item)"
  />
  </div>
</template>

<script>
export default {
  props:['list_arr'],
  data () {
    return {
    }
  },
  methods: {
    // 选择地址
    btn_address(item){
      this.$emit('addressFun',item);
    },
  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.list_box{
  padding-bottom: 3.25rem;
  .list{
    /deep/ .van-cell{
        align-items:center;
        font-family:PingFang-SC-Medium;
    }
  }
}


</style>

