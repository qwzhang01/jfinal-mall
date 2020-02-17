<template>
  <div class="video">
    <!-- 视频  -->
    <video :src="src" :autoplay="true" controls="controls"  id="video"></video>
  </div>
</template>

<script>

export default {
  props: ['src'],
  data () {
    return {}
  },
  methods: {

  },
  mounted(){

  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.video{
  height: 13.5rem;
  width: 100%;
  position: relative;

  video{
    display: block;
    width: 100%;
    height: 100%;
  }
}


</style>

