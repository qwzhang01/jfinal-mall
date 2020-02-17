<template>
  <div class="coment">
    <!-- 导航 -->
    <Navbar :navbar="navbar"/>
    <!-- 咨讯 -->
    <div class="info_title">{{title}}</div>
    <div v-html="coment" class="info_coment"></div>
  </div>
</template>

<script>
import { infoDetail } from "../../../api/info/";
import Navbar from "../../components/navbar";
export default {
  components: {
    Navbar
  },
  data() {
    return {
      // 头部导航栏
      navbar: {
        height: "2.75rem",
        left: [
          {
            // 图标大小
            size: "25px",
            coment: "返回",
            to: "/infoList",
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
      coment: "",
      title: ""
    };
  },
  methods: {
    ArticleDetailFun(id) {
      var that = this;
      let params = {
        articleId: id
      };
      infoDetail(params).then(res => {
        // console.log(res);
        if (res.status == 0) {
          that.coment = res.data.content;
          that.title = res.data.title;
        }
      });
    }
  },
  mounted() {
    // 接受参数
    if (this.$route.query.id) {
      // console.log(this.$route.query.id);
      this.ArticleDetailFun(this.$route.query.id);
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="less" scoped>
.coment {
  width: 100%;
  padding: 0.5rem;
  background: #ffffff;
  // height: -webkit-fill-available;
}
.info_title {
  margin: 1rem 0;
  font-size: 1rem;
  font-weight: bold;
}
</style>
<style scoped>
.info_coment >>> {
  width: 100%;
  font-size: 0.6rem !important;
}
.info_coment >>> img {
  width: 100% !important;
}
</style>

