<template>
  <!-- 步骤条 -->
  <div class="step_box">
    <div class="step_box_1">
      <div class="step_box_2" v-for="(item,key) in step" :key="key">
        <div :class="['step',item.active?'':'active']">
          <div :class="['step_number',item.active?'':'active2']">{{item.number}}</div>
          <div class="step_name">{{item.name}}</div>
        </div>
        <!-- 线 -->
        <div
          :style="{ 'margin': `auto auto auto ${storeType==1 ? '3rem' : '1rem'}`}"
          :class="['stepLine',item.active?'':'active2']"
          v-if="step.length - 1 > key"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ["step", "storeType"],
  data() {
    return {};
  },
  methods: {},
  mounted() {}
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 步骤
.step_box {
  background: #d7000f;
  padding: 2rem 0 1rem 0;
  .step_box_1 {
    width: 96%;
    display: flex;
    margin: 0 auto;
    justify-content: space-around;
  }
  .step_box_2 {
    display: flex;
    .step {
      color: #ffffff;
      .step_number {
        width: 1.25rem;
        height: 1.25rem;
        background: #ffffff;
        border-radius: 50%;
        color: #d7000f;
        text-align: center;
        line-height: 1.25rem;
        margin: 0 auto 0.3rem auto;
      }
      .step_name {
        font-size: 0.8rem;
      }
    }
    .stepLine {
      height: 1px;
      width: 3rem;
      background: #e6e6e6;
    }
  }
  .active {
    color: rgba(255, 255, 255, 0.5) !important;
  }
  .active2 {
    background: rgba(255, 255, 255, 0.5) !important;
  }
}
</style>

