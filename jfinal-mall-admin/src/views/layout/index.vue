<template>
  <el-container class="container">
    <el-header class="header">
      <el-col :span="10" class="logo" :class="collapsed?'logo-collapse-width':'logo-width'">
        <span v-if="!collapsed">
          <el-image :src="logoImg" fit="cover" style="float: left;"></el-image>
          {{set.title}}
        </span>
        <el-image :src="logoImg" fit="cover" v-else></el-image>
      </el-col>
      <!-- 折叠面板 -->
      <el-col :span="1">
        <div class="tools" @click.prevent="collapse">
          <i class="fa fa-align-justify fa-rotate-90" v-if="collapsed"></i>
          <i v-else class="fa fa-align-justify"></i>
        </div>
      </el-col>
      <el-col :span="4" class="userinfo">
        <el-dropdown trigger="hover">
          <span class="el-dropdown-link userinfo-inner">
            欢迎您：{{$store.getters.getUser.name}}&nbsp;&nbsp;&nbsp;&nbsp;
            <i class="fa fa-user"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <!-- <el-dropdown-item divided>我的消息</el-dropdown-item> -->
            <el-dropdown-item @click.native="changePassword">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logoutPage">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-col>
    </el-header>

    <el-container class="main">
      <el-aside :class="[collapsed?'menu-collapsed':'menu-expanded','nav_left']">
        <!--导航菜单-->
        <el-menu
          class="el-menu-vertical-demo"
          unique-opened
          router
          :default-active="$route.path"
          :collapse="collapsed"
          background-color="#222d32"
          text-color="#fff"
          active-text-color="#409EFF"
          @open="handleopen"
          @close="handleclose"
          @select="handleselect"
        >
          <template
            v-for="(item,index) in $router.options.routes"
            v-key="index"
            v-if="!item.hidden && checkPermissionLayout(item.meta)"
          >
            <el-submenu :index="index+''" v-if="!item.leaf ">
              <template slot="title">
                <i :class="item.iconCls"></i>
                {{item.name}}
              </template>
              <el-menu-item
                v-for="child in item.children"
                :index="child.path"
                :key="child.path"
                v-if="!child.hidden && checkPermissionLayout(child.meta)"
              >{{child.name}}</el-menu-item>
            </el-submenu>

            <el-menu-item v-if="item.leaf&&item.children.length>0" :index="item.children[0].path">
              <i :class="item.iconCls"></i>
              {{item.children[0].name}}
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>
      <!-- 页面main板块 -->
      <el-main class="content-container">
        <el-row class="grid-content bg-purple-light">
          <!-- 面包屑 -->
          <el-col :span="24" class="breadcrumb-container">
            <strong class="title">{{$route.name}}</strong>
            <el-breadcrumb separator="/" class="breadcrumb-inner">
              <el-breadcrumb-item
                v-for="item in $route.matched"
                :key="item.path"
                :to="{path: item.path}"
              >{{ item.name }}</el-breadcrumb-item>
            </el-breadcrumb>
          </el-col>
          <!-- 内容 -->
          <el-col :span="24" class="content-wrapper">
            <transition name="fade" mode="out-in">
              <router-view></router-view>
            </transition>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
    <!-- 修改密码表单 -->
    <el-dialog
      title="修改密码"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="changePwForm" :rules="rules" ref="changePwForm" label-width="100px">
        <el-form-item label="新密码：" prop="password" :rules="rules.password">
          <el-input
            type="password"
            v-model="changePwForm.password"
            auto-complete="off"
            placeholder="密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码：" prop="confirmPassword" :rules="rules.password">
          <el-input
            type="password"
            v-model="changePwForm.confirmPassword"
            auto-complete="off"
            placeholder="确认密码"
          ></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="changePwSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import { logout } from "@/api/login";
import { removeToken, removeUserId } from "@/util/cookie";
import { checkPermission } from "@/util/operator";
import rules from "../../comm/rules";
import { changePw } from "@/api/staff";
import set from "@/set.js";
import logoImg from "@/assets/logo.png";

export default {
  data() {
    return {
      set,
      logoImg,
      collapsed: false,
      dialogVisible: false,
      changePwForm: {
        password: "",
        confirmPassword: ""
      },
      rules
    };
  },
  methods: {
    checkPermissionLayout(routerMeta) {
      if (routerMeta) {
        return this.checkPermission(routerMeta.authInfo);
      }
      return true;
    },
    checkPermission,
    // 打开菜单
    handleopen() {},
    // 折叠菜单
    handleclose() {},
    handleselect: function(a, b) {},
    // 修改密码
    changePassword() {
      this.dialogVisible = true;
      this.changePwForm = {
        password: "",
        confirmPassword: ""
      };
    },
    // 保存修改的密码
    changePwSubmit() {
      let _this = this;
      _this.$refs["changePwForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        changePw(_this.changePwForm).then(res => {
          if (res.status === 0) {
            _this.dialogVisible = false;
            Message({
              message: "修改成功",
              type: "success",
              duration: 3 * 1000
            });
            _this.getList();
          } else {
            Message({
              message: res.msg ? res.msg : "系统异常",
              type: "error",
              duration: 3 * 1000
            });
          }
        });
      });
    },
    //退出登录
    logoutPage: function() {
      var _this = this;
      this.$confirm("确认退出吗?", "提示", {
        type: "warning"
      })
        .then(() => {
          logout().then(res => {
            if (res.status === 0) {
              removeToken();
              removeUserId();
              _this.$router.push("/login");
            }
          });
        })
        .catch(() => {});
    },
    //折叠导航栏
    collapse: function() {
      this.collapsed = !this.collapsed;
    }
  },
  mounted() {}
};
</script>

<style scoped lang="scss">
.container {
  // overflow: hidden;
  background-color: #f4f5f6;
  height: 100%;
  .header {
    height: 100%;
    line-height: 60px;
    background-color: #3c8dbc;
    box-shadow: 3px 3px 3px 0 #3c8dbc;
    color: #fff;
    .tools {
      padding: 0px 23px;
      width: 14px;
      height: 60px;
      line-height: 60px;
      margin: 0 auto;
      cursor: pointer;
    }
    .userinfo {
      text-align: right;
      padding-right: 35px;
      float: right;
      .userinfo-inner {
        cursor: pointer;
        color: #fff;
        img {
          width: 40px;
          height: 40px;
          border-radius: 20px;
          margin: 10px 0px 10px 10px;
          float: right;
        }
      }
    }
    .logo {
      width: 230px;
      height: 60px;
      font-size: 22px;
      border-color: rgba(238, 241, 146, 0.3);
      border-right-width: 1px;
      border-right-style: solid;
      /deep/ .el-image {
        padding: 13px 5px;
        width: 35px;
        height: 35px;
      }
    }
    .logo-width {
      width: 230px;
    }
    .logo-collapse-width {
      width: 60px;
    }
  }

  .main {
    background: #f4f5f6;
    top: 6%;
    bottom: 0px;
    aside {
      flex: 0 0 250px;
      width: 250px;
      top: 0px;
      overflow: hidden;
      bottom: 0px;
      .el-menu {
        height: 100%;
      }
      .collapsed {
        width: 60px;
        .item {
          position: relative;
        }
      }
    }
    .menu-collapsed {
      flex: 0 0 45px;
      width: 45px;
    }
    .menu-expanded {
      flex: 0 0 250px;
      width: 250px;
    }
    .nav_left {
      background: #373b46;
    }
    .content-container {
      background: #f4f5f6;
      flex: 1;
      right: 0px;
      top: 0px;
      bottom: 0px;
      left: 230px;
      overflow-y: scroll;
      .breadcrumb-container {
        margin-bottom: 10px;
        .title {
          width: 200px;
          float: left;
          color: #475669;
        }
        .breadcrumb-inner {
          float: right;
        }
      }
      .content-wrapper {
        background-color: #fff;
        min-height: 800px;
      }
    }
  }
}
</style>