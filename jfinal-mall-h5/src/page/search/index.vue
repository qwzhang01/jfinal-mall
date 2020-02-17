<template>
  <div>
    <div class="Search">
      <div class="left">
        <van-icon name="arrow-left"/>
        <router-link :to="toUrl">
          <span>返回</span>
        </router-link>
      </div>
      <Search @searchFun="searchFun" :search="search"/>
    </div>

    <div>
      <div class="History">
        <span>搜索历史</span>
        <span>
          <van-icon name="delete" size="1.2rem" @click="Del(1)"/>
        </span>
      </div>
      <div class="coment" v-if="history">
        <span v-for="(item,key) in history" :key="key" @click="dataUp(item)">
          {{item}}
          <van-icon name="close" size="1rem" class="del_icon" @click.stop="Del(2,key)"/>
        </span>
      </div>
    </div>
    <div>
      <div class="History">
        <span>热门搜索</span>
        <!-- <span><van-icon name="delete" size="1.2rem" /></span> -->
      </div>
      <div class="coment">
        <span v-for="(item,key) in HostSearch" :key="key" @click="searchFun(item)">
          {{item}}
          <van-icon name="fire" size="1rem" class="del_icon" color="#D7000F"/>
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from "../../components/navbar";
import Search from "../../components/search";
import commJs from "../../comm";
import { searKey } from "../../../api/";
export default {
  components: {
    Navbar,
    Search
  },
  data() {
    return {
      // 搜索内容
      search: "",
      history: [],
      // 热搜
      HostSearch: [],
      toUrl: "/categoryList"
    };
  },
  methods: {
    // 搜索
    searchFun(data) {
      if (data) {
        // 获取全部数据
        this.search = data;
        // 存储
        commJs.history(data);
        this.$router.push({
          path: "./result?search=" + data
        });
      } else {
        commJs.Dialog2("搜索内容不能为空", "error", 1500, this);
      }
    },
    // 清除
    Del(mun, key) {
      commJs.Dialog("温馨提示", `你确定删除相关历史记录？`).then(res => {
        console.log(res);
        if (res === "confirm") {
          if (mun == 2) {
            // 删除
            console.log(key);
            let history = sessionStorage.getItem("history").split(",");
            history.splice(key, 1);
            this.history = history;
            sessionStorage.setItem("history", history.join(","));
          } else if (mun == 1) {
            // 清空历史
            let history = sessionStorage.removeItem("history");
            this.history = history;
          }
        }
      });
    },
    // 热门搜索
    searKeyFun() {
      let that = this;
      searKey().then(res => {
        if (res.status == 0) {
          if (res.data.length > 0) {
            that.search = res.data[0];
            that.HostSearch = res.data;
          }
        } else {
          commJs.Dialog2(res.msg, "error", 1000, that);
        }
      });
    },
    // 赋值
    dataUp(item) {
      console.log(item);
      this.$router.push({
        path: "./result?search=" + item
      });
    }
  },
  mounted() {
    this.searKeyFun();
    this.history = sessionStorage.getItem("history")
      ? (this.history = sessionStorage.getItem("history").split(","))
      : [];
    if (this.$route.query.u) {
      sessionStorage.setItem(
        "upper",
        this.$route.query.u == "/"
          ? "/"
          : commJs.jumpUrl(this, this.navbar.left[0].to, ["goodId", "type"])
      );
      this.toUrl = this.$route.query.u;
    }
    // if(sessionStorage.getItem('upper')){
    //   this.toUrl = sessionStorage.getItem('upper');
    // }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
// 搜索
.Search {
  display: flex;
  background: #ffffff;
  align-items: center;
  font-size: 0.8rem;
  width: 100%;
  .left {
    display: flex;
    align-items: center;
    color: #333333;
    width: 3.1rem;
    margin-left: 0.5rem;
  }
  .search,
  /deep/ .van-search {
    width: 100%;
  }
  /deep/ .van-field__control {
    padding-top: 0.1875rem;
  }
}

.History {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 1rem;
  color: #333333;
  height: 3rem;
  padding: 0 0.7rem;
}
.coment {
  padding: 0 0.7rem;
  display: flex;
  flex-wrap: wrap;
  span {
    font-size: 0.8rem;
    margin: 0.8rem 1.7rem 0.5rem 0;
    padding: 0.5rem 1.2rem;
    background: #ffffff;
    position: relative;
    .del_icon {
      position: absolute;
      right: -0.4rem;
      top: -0.5rem;
    }
  }
}
</style>

