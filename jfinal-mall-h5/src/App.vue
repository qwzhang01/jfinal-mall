<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: "App"
};
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.yd-dialog-white-mask {
  z-index: 3000 !important;
}
.yd-datetime-head > a:last-child {
  color: #d7000f;
}
</style>
