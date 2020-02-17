<template>
  <div>
    <!-- 导航 -->
    <Navbar :navbar="navbar" @navRight="del" />
    <!-- 列表 -->
    <div class="list_box">
      <div class="list">
        <!-- 收货人 -->
        <van-cell-group>
          <van-field
            label="收货人"
            v-model="from.consignee"
            clearable
            maxlength="11"
            placeholder="请输入收货人"
          />
          <!-- 手机号码 -->
          <van-field
            label="手机号码"
            type="number"
            clearable
            maxlength="11"
            v-model="from.mobile"
            placeholder="请输入手机号码"
          />
          <!-- 所在地区 -->
          <van-field
            label="所在地区"
            :readonly="true"
            v-model="from.detailed"
            placeholder="请输入所在地区"
            right-icon="ellipsis"
            @click.native="showFun"
          />
          <!-- 详细地址 -->
          <van-field
            type="textarea"
            rows="3"
            autosize
            clearable
            v-model="from.address"
            label="详细地址"
            maxlength="100"
            placeholder="请输入详细地址"
          />
        </van-cell-group>
      </div>
      <!-- 默认地址 -->
      <div class="default" @click.native="from.is_default = !from.is_default">
        <van-checkbox checked-color="#D7000F" size="1.375rem" v-model="from.is_default">默认收货地址</van-checkbox>
      </div>
    </div>
    <!-- 提交 -->
    <yd-button
      size="large"
      shape="angle"
      type="primary"
      color="#ffffff"
      bgcolor="#D7000F"
      class="btn"
      @click.native="submit"
    >{{ from.address_id == 0?"新增":"保存"}}</yd-button>
    <!-- 弹窗 -->
    <yd-popup v-model="show" position="right" width="70%">
      <!-- 关闭 -->
      <yd-cell-item>
        <div slot="right">
          <van-icon name="close" size="1rem" @click.native="show = false" />
        </div>
      </yd-cell-item>
      <!-- 地址列表 -->
      <City :list_arr="list_arr" @addressFun="addressFun" />
    </yd-popup>
  </div>
</template>

<script>
import { Dialog } from "vant";
import Navbar from "../../components/navbar";
import City from "../../components/city";
import comm_js from "../../comm/";
import { ModifyAddress, AddAddress, DelAddress, Regin } from "../../../api/";
import { setTimeout } from "timers";
import commJs from "../../comm/";
// 暂存区
let detailed = "";
export default {
  components: {
    Navbar,
    City
  },
  data() {
    return {
      state: "保存",
      // 弹窗
      show: false,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/addresslist",
            color: "#333333"
          }
        ],
        right: [
          {
            // 图标大小
            size: "25px",
            coment: this.$route.query.item ? "删除" : "",
            to: "",
            color: "#D7000F"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: this.$route.query.item ? "修改地址" : "新增地址",
          //颜色
          color: "#333333"
        }
      },
      // 地址列表
      from: {
        address_id: 0,
        is_default: 0,
        consignee: "",
        mobile: "",
        address: "", // 省市区
        detailed: "", // 详细地址
        addressSonId: 0 // 最后选择的id
      },
      // 获取省市区的parentId
      parentId: 0,
      list_arr: []
    };
  },
  methods: {
    // 获取省市区
    ReginFun() {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId"),
        parentId: this.parentId
      };
      Regin(params).then(res => {
        if (res.status == 0) {
          that.list_arr = [];
          that.list_arr = res.data;
        } else {
          that.show = false;
        }
      });
    },
    // 删除
    del(data) {
      let that = this;
      comm_js.Dialog("温馨提示", "确定执行删除操作？", Dialog).then(res => {
        if (res === "confirm") {
          that.DelAddressFun(that.from.address_id);
        }
      });
    },
    // 删除
    DelAddressFun(id) {
      let that = this;
      let params = {
        userId: sessionStorage.getItem("userId"),
        address_id: id
      };
      DelAddress(params).then(res => {
        if (res.status == 0) {
          comm_js.Dialog2("删除成功！", "success", 1000, that);
          setTimeout(() => {
            that.$router.push({
              path: that.navbar.left[0].to
            });
          }, 1200);
        }
      });
    },
    // 新增
    AddAddressFun(params) {
      let that = this;
      AddAddress(params).then(res => {
        if (res.status == 0) {
          comm_js.Dialog2("新增成功！", "success", 1000, that);
          setTimeout(() => {
            that.$router.push({
              path: that.navbar.left[0].to
            });
          }, 1200);
        }
      });
    },
    //修改地址
    ModifyAddressFun(params) {
      let that = this;
      ModifyAddress(params).then(res => {
        if (res.status == 0) {
          comm_js.Dialog2("修改成功！", "success", 1000, that);
          setTimeout(() => {
            that.$router.push({
              path: that.navbar.left[0].to
            });
          }, 1200);
        }
      });
    },
    // 开启地址弹窗
    showFun() {
      this.show = true;
      this.list_arr = [];
      this.parentId = 0;
      this.ReginFun();
      detailed = "";
    },
    // 选择地址
    addressFun(item) {
      console.log(item);
      this.parentId = item.id;
      detailed += item.name + " ";
      this.ReginFun();
      setTimeout(() => {
        if (!this.show) {
          // 改变地址
          this.from.detailed = detailed;
          this.from.addressSonId = item.id;
        }
      }, 500);
      console.log(detailed);
    },
    // 提交
    submit() {
      let that = this;
      // 检测

      // 收货人
      if (comm_js.test(this.from.consignee, "收货人", [1], this) === false) {
        return;
      }
      // 手机号码
      if (
        comm_js.test(this.from.mobile, "手机号码", [1, 2, 3], this) === false
      ) {
        return;
      }
      // 省市区
      if (comm_js.test(this.from.detailed, "省市区", [], this) === false) {
        return;
      }
      // 详细地址
      if (comm_js.test(this.from.address, "详细地址", [], this) === false) {
        return;
      }

      // 提交
      that.from.is_default = that.from.is_default ? 1 : 0;
      if (that.from.address_id == 0) {
        // 新增
        this.AddAddressFun(this.from);
      } else {
        // 修改
        this.ModifyAddressFun(this.from);
      }
    }
  },
  mounted() {
    // 路由配置
    this.navbar.left[0].to =
      this.navbar.left[0].to +
      "?u=" +
      commJs.jumpUrl(this, this.navbar.left[0].to, ["item"]);

    if (this.$route.query.item) {
      let item = JSON.parse(this.$route.query.item);
      this.from.address_id = item.address_id;
      this.from.consignee = item.consignee;
      this.from.mobile = item.mobile;
      this.from.address = item.address;
      this.from.detailed = `${item.provinceed +
        " " +
        item.cityed +
        " " +
        item.twoned +
        " " +
        item.districted}`;
      this.from.addressSonId = item.minId;
      this.from.is_default = item.is_default;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.list_box {
  padding-bottom: 3.25rem;
  .list {
    /deep/ .van-cell {
      align-items: center;
      font-family: PingFang-SC-Medium;
    }
  }
}
.btn {
  position: fixed;
  bottom: 0;
  height: 3.125rem;
  font-size: 1rem;
  font-family: PingFang-SC-Medium;
  margin-top: 0;
}
.default {
  padding-top: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-family: PingFang-SC-Medium;
}
</style>

