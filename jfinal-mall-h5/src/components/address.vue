<template>
  <!-- 选择地址  -->
  <div class="address" @click="addresslist">
    <div class="address_icon">
      <img src="../assets/dizhi.png" alt="地址icon" />
    </div>
    <div class="address_info">
      <div>收货人：{{address.name}} {{address.mobile}}</div>
      <div>收货地址：{{address.address}}</div>
    </div>
    <div class="next_icon">
      <img src="../assets/you_jian_tou.png" alt="下一步" />
    </div>
  </div>
</template>

<script>
export default {
  props: ["address"],
  data() {
    return {};
  },
  methods: {
    addresslist() {
      this.$emit("addresslist", "address");
    }
  },
  mounted() {}
};
</script>

<style lang="less" scoped>
// 收货人地址
.address {
  margin-top: 0.625rem;
  padding: 0.9375rem;
  background: #ffffff;
  position: relative;
  display: flex;
  align-items: center;
  .address_icon {
    img {
      margin-top: 1.5rem;
      width: 0.9375rem;
      height: 1.125rem;
      margin-right: 0.8rem;
    }
  }
  .address_info {
    width: 100%;
    color: #333333;
    div:nth-child(1) {
      font-size: 1rem;
    }
    div:nth-child(2) {
      margin-top: 0.4rem;
      font-size: 0.8125rem;
      line-height: 1.2rem;
    }
  }
  .next_icon {
    margin-left: 0.3rem;
    margin-top: 1.5rem;
    img {
      width: 1.375rem;
      height: 1.375rem;
    }
  }
}
.address ::before {
  content: "";
  left: 0;
  right: 0;
  bottom: 0;
  height: 2px;
  position: absolute;
  background: -webkit-repeating-linear-gradient(
    135deg,
    #ff6c6c 0,
    #ff6c6c 20%,
    transparent 0,
    transparent 25%,
    #1989fa 0,
    #1989fa 45%,
    transparent 0,
    transparent 50%
  );
  background: repeating-linear-gradient(
    -45deg,
    #ff6c6c 0,
    #ff6c6c 20%,
    transparent 0,
    transparent 25%,
    #1989fa 0,
    #1989fa 45%,
    transparent 0,
    transparent 50%
  );
  background-size: 80px;
}
</style>

