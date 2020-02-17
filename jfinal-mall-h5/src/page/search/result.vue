<template>
  <div>
    <!-- 结果 -->
    <div class="Search" v-if="actveNav">
      <div class="left">
        <van-icon name="arrow-left"/>
        <router-link :to="toUrl">
          <span>返回</span>
        </router-link>
      </div>
      <Search @searchFun="searchFun" :search="search"/>
    </div>
    <!-- 分类 -->
    <div v-else>
      <Navbar :navbar="navbar"/>
    </div>
    <!-- 结果 -->
    <Lable
      :distanceActive="distanceActive"
      :screen="screen"
      :screenIndex="screenIndex"
      :arrowactive="arrowactive"
      @screenFun="screenFun"
    />
    <!-- 综合 -->
    <div v-if="comprehensiveActive" class="comprehensive">
      <div
        v-for="(item,key) in comprehensive"
        :key="key"
        :class="['comprehensive_coemnt',comprehensiveIndex == key ?'comprehensive_active':'']"
        @click="comprehensiveBtn(key)"
      >
        <span>{{item}}</span>
        <span v-if="comprehensiveIndex == key">
          <img src="../../assets/gou.png" alt="icon">
        </span>
      </div>
    </div>
    <!-- 筛选 -->
    <yd-popup v-model="show4" position="right" width="80%">
      <Screen :screenFrom="screenFrom" @selectFun="selectFun"/>
    </yd-popup>
    <!-- 列表 -->
    <van-list
      v-model="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="goodListFun"
      id="screen"
    >
      <List :list_type="1" :theme="3" :list="list" @introduce="Good" class="like"/>
    </van-list>
  </div>
</template>

<script>
import { Dialog } from "vant";
import Navbar from "../../components/navbar";
import Screen from "../../components/screen";
import List from "../../components/list";
import Search from "../../components/search";
import Lable from "../../components/lable";
import commJs from "../../comm";
import {
  // 根据分类搜索商品
  searchPageByCategory,
  // 名称、关键字搜索
  searchByName,
  searchForm
} from "../../../api/result/";
export default {
  components: {
    Navbar,
    Search,
    Lable,
    List,
    Screen
  },
  data() {
    return {
      // 筛选弹窗
      show4: false,
      // 规格数据
      screenFrom: {
        // 起始价格
        startMoeny: "",
        // 结束价格
        endMoeny: "",
        // 品牌
        brand: [],
        // 规格
        specification: []
      },
      // 规格筛选结果
      selectNorms: {},
      actveNav: false,
      comprehensive: ["综合排序", "好评"],
      comprehensiveIndex: 0,
      comprehensiveActive: false,
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/categoryList",
            color: "#333333"
          }
        ],
        // 标题
        title: {
          // 内容
          coment: "",
          //颜色
          color: "#333333"
        },
        right: []
      },
      // 搜索内容
      search: "",
      history: [],
      toUrl: "",
      distanceActive: false,
      // 宝贝筛选
      screen: ["综合", "销量", "价格", "筛选"],
      screenIndex: 0,
      // 1：从高价格开始排序  2：从低价格开始排序
      arrowactive: 0,
      // 排序字段
      sortColumn: "",
      // 排序方式
      sortType: "ASC",
      pageNumber: 1,
      pageSize: 10,
      totalPage: 1,
      finished: false,
      loading: false,
      list: [],
      id: "",
      url: ""
    };
  },
  methods: {
    // 搜索
    searchFun(data) {
      // if(data){
      // 获取全部数据
      this.search = data;
      console.log(data);
      this.list = [];
      this.sortType = "ASC";
      this.pageNumber = 1;
      this.screenIndex = 0;
      this.searchByNameFun(this.search);
      this.searchFormFun();
      if (data) {
        commJs.history(data);
      }
    },
    // 筛选
    screenFun(key) {
      this.screenIndex = key;
      this.comprehensiveActive = false;
      this.categoryId = "";
      if (key == 0 || key == 1 || key == 2) {
        this.pageNumber = 1;
        // 综合
        if (key == 0) {
          console.log("综合");
          this.sortColumn = "";
          this.sortType = "DESC";
          this.comprehensiveActive = !this.comprehensiveActive;
        }

        // 销量
        if (key == 1) {
          console.log("销量");
          this.sortColumn = "saleNum";
          this.sortType = "DESC";
        }

        // 价格
        if (key == 2) {
          console.log("价格");
          this.sortColumn = "shopPrice";
          if (this.arrowactive == 1) {
            this.sortType = "DESC";
          } else {
            this.sortType = "ASC";
          }
          this.arrowactive = this.arrowactive == 1 ? 2 : 1;
        } else {
          this.arrowactive = 0;
        }
        this.loading = false;
        this.finished = false;
        this.list = [];
        this.goodListFun();
      } else {
        // 筛选
        if (key == 3) {
          this.arrowactive = 0;
          this.show4 = !this.show4;
        }
      }
    },
    goodListFun() {
      this.searchByNameFun(this.search);
    },
    // 查看商品
    Good(data) {
      // console.log(data);
      // console.log(this.url);
      sessionStorage.setItem("upper", this.url + "&u=" + this.toUrl);
      this.$router.push({
        path: `/goodsinfo?goodId=${data.item.goodId}`
      });
    },
    // 筛选规格、搜索关键字
    searchByNameFun(goodName = "") {
      let that = this;
      let data = Object.assign(
        {
          categoryId: this.id,
          goodName: goodName ? goodName : this.search,
          // 好评（goodCommentRate）销量（saleNum）价格（shopPrice）
          sortColumn: this.sortColumn,
          sortType: this.sortType,
          pageNumber: this.pageNumber,
          pageSize: this.pageSize
        },
        that.selectNorms
      );
      searchByName(data).then(res => {
        if (res.status == 0) {
          // 总页数 > 当前页数
          setTimeout(() => {
            if (res.data.totalPage >= that.pageNumber) {
              that.pageSize = res.data.pageSize;
              that.totalPage = res.data.totalPage;
              that.list.push(
                ...res.data.list.map(item => {
                  item.text = `${item.goodCommentNum}条评价 ${
                    item.goodCommentRate
                  }%好评`;
                  return item;
                })
              );
              that.pageNumber = parseInt(that.pageNumber) + 1;
            } else {
              that.finished = true;
            }
            // 加载状态结束
            that.loading = false;
          }, 500);
        }
      });
    },
    // 综合
    comprehensiveBtn(key) {
      this.comprehensiveActive = !this.comprehensiveActive;
      this.comprehensiveIndex = key;
      this.sortColumn = key == 0 ? "" : "goodCommentRate";
      this.pageNumber = 1;
      this.list = [];
      this.searchByNameFun();
    },
    // 规格数据
    searchFormFun() {
      let that = this;
      let data = {
        categoryId: this.id,
        goodName: this.search
      };
      searchForm(data).then(res => {
        if (res.status == 0) {
          that.screenFrom = {};
          that.screenFrom = Object.assign(that.screenFrom, res.data);
        }
      });
    },
    // 规格选择结果
    selectFun(item) {
      this.selectNorms = Object.assign(this.selectNorms, item);
      this.show4 = !this.show4;
      this.list = [];
      this.pageNumber = 1;
      this.searchByNameFun();
    }
  },
  mounted() {
    // 名称、关键字搜索
    if (this.$route.query.search) {
      this.toUrl = "/search";
      this.search = this.$route.query.search;
      this.actveNav = true;
      this.url = `/result?search=${this.$route.query.search}`;
      // console.log(this.url);
      this.searchFormFun();
    }
    // 根据分类搜索商品
    else {
      this.navbar.title.coment = this.$route.query.text;
      this.actveNav = false;
      this.id = this.$route.query.id;
      this.url = `/result?text=${this.$route.query.text}&id=${
        this.$route.query.id
      }`;
      // console.log(this.url)
      this.searchFormFun();
    }

    if (this.$route.query.u) {
      this.navbar.left[0].to = this.$route.query.u;
      this.toUrl = this.$route.query.u;
    }
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
// 商品列表
.like {
  background: #ffffff;
  .title {
    font-family: PingFang-SC-Bold;
    color: #333333;
    font-size: 1rem;
    padding-top: 0.9375rem;
    padding-bottom: 1.25rem;
  }
  /deep/ .yd-list-title {
    font-weight: inherit;
    color: #333333;
    font-size: 0.875rem;
    margin: 0.625rem 0;
  }
  /deep/ .demo-list-price {
    color: #d7000f;
    font-size: 1rem;
  }
  /deep/ .remarks {
    margin-top: 0.2rem;
    font-size: 0.8rem;
  }
}
// 综合
.comprehensive {
  background: #ffffff;
  margin-bottom: 0.625rem;
  font-size: 0.8rem;
  padding-bottom: 0.8rem;
  .comprehensive_coemnt {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.8rem 0.9375rem 0 0.9375rem;
  }
  color: #333333;
  img {
    width: 1rem;
    height: 0.625rem;
  }
}
.comprehensive_active {
  color: #d7000f !important;
}
</style>

