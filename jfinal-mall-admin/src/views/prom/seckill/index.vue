<template>
  <el-card class="box-card">
    <el-row class="nav-search">
      <!-- 导航 -->
      <Nav :nav="nav" class="nav" @nav_btn="nav_btn" :span="12"></Nav>
      <!-- 搜索 -->
      <Search :search="search" :searchFrom="searchFrom" :span="12">
        <el-button type="success" @click="searchFun">搜索</el-button>
        <el-button type="primary" @click="handleClose" v-if="type === 1">添加活动</el-button>
      </Search>
    </el-row>

    <!-- 表格 -->
    <Table :table="table" :tableKey="tableKey">
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" v-if="1 === type" @click="Edit(scope.row)">编辑</el-button>
          <el-button type="warning" size="mini" @click="Copy(scope.row)">复制</el-button>
          <el-button
            type="success"
            size="mini"
            @click="Jump(scope.row)"
          >{{1 === type?'设置商品':'查看商品'}}</el-button>
          <el-button type="warning" size="mini" v-if="1 === type" @click="Sync(scope.row)">同步</el-button>
          <el-button type="danger" size="mini" @click="Delete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </Table>
    <!-- 分页 -->
    <Paging
      :page="page"
      @handleCurrentChange="handleCurrentChange"
      @handleSizeChange="handleSizeChange"
    ></Paging>

    <!-- 弹窗 -->
    <el-dialog
      :title="form.formTitle"
      :visible.sync="dialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :rules="rules"
      :before-close="handleClose"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="秒杀活动名称：" prop="name" :rules="rules.val">
          <el-input v-model="form.name" type="text" placeholder="请输入活动名称" style="width:55%"></el-input>
        </el-form-item>
        <el-form-item label="秒杀时间：" prop="dateTimePickerRanger" :rules="rules.val">
          <el-date-picker
            v-model="form.dateTimePickerRanger"
            :editable="false"
            :clearable="true"
            :time-arrow-control="true"
            :unlink-panels="true"
            format="yyyy-MM-dd HH:mm"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="每人限购数：" prop="buyLimit" :rules="rules.val">
          <el-input v-model="form.buyLimit" type="number" placeholder="请输入每人限购数" style="width:55%"></el-input>
        </el-form-item>
        <el-form-item class="submit">
          <el-button type="primary" @click="onSubmit('form')">立即保存</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>
<script>
import config from "./config.js";
import rules from "../../../comm/rules";
import comm from "../../../comm/";
import Nav from "../../../components/nav";

import Search from "../../../components/search";
import Table from "../../../components/table";
import Paging from "../../../components/paging";
import {
  Pages,
  Forms,
  Copys,
  Deletes,
  Syncs
} from "../../../api/prom/seckill";

export default {
  components: {
    Nav,
    Search,
    Table,
    Paging
  },
  data() {
    return {
      rules,
      dialogVisible: false,
      // 导航菜单
      nav: config.nav,
      // 搜索key
      search: config.search,
      // 表格
      table: [],
      //  表格key
      tableKey: config.tableKey,
      // 搜索
      searchFrom: {
        name: ""
      },
      // 秒杀类型 1即将开始 2秒杀中 3结束秒杀
      type: 1,
      page: {
        // 页码
        pageNumber: 1,
        // 每页显示条数
        pageSize: 10,
        // 总页面
        totalPage: 1,
        // 总条数
        totalRow: 0
      },

      // 活动表单
      form: {
        id: "",
        name: "",
        dateTimePickerRanger: [],
        beginTime: "",
        endTime: "",
        buyLimit: "0",
        formTitle: "添加秒杀活动"
      },
      // 复制一场对应的活动ID
      fromId: "",
      // 表单是否可以提交
      formSubFlag: true
    };
  },
  methods: {
    // 表单弹窗
    handleClose() {
      this.formSubFlag = true;

      this.dialogVisible = !this.dialogVisible;
      if (this.$refs["form"] !== undefined) {
        this.$refs["form"].resetFields();
      }
      this.clean();
    },
    // 提交表单
    onSubmit(formName) {
      let that = this;
      if (!that.formSubFlag) {
        comm.message("请不要重复点击提交按钮", "warning", that);
        return;
      }
      this.$refs[formName].validate(valid => {
        if (valid) {
          that.form.beginTime = that.form.dateTimePickerRanger[0];
          that.form.endTime = that.form.dateTimePickerRanger[1];
          if (
            comm.stringToDate(that.form.beginTime) >=
            comm.stringToDate(that.form.endTime)
          ) {
            comm.message("秒杀开始时间不能大于或等于结束时间", "warning", that);
            return false;
          }
          if (new Date() >= comm.stringToDate(that.form.beginTime)) {
            comm.message("秒杀开始时间不能小于或等于现在", "warning", that);
            return false;
          }
          that.formSubFlag = false;

          let form = that.form;
          delete form.dateTimePickerRanger;

          if (that.form.formTitle === "复制秒杀活动") {
            Copys(form).then(res => {
              if (res.status === 0) {
                that.dialogVisible = !that.dialogVisible;
                that.PageFun();
                comm.message(res.msg, "success", this);
              }
            });
          } else {
            Forms(form).then(res => {
              if (res.status == 0) {
                that.dialogVisible = !that.dialogVisible;
                that.PageFun();
                comm.message(res.msg, "success", this);
              }
            });
          }
          return true;
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    //  清除form
    clean() {
      this.form.id = "";
      this.form.name = "";
      this.form.dateTimePickerRanger = [];
      this.form.beginTime = "";
      this.form.endTime = "";
      this.form.buyLimit = "0";
      this.form.formTitle = "添加秒杀活动";
    },
    Edit(item) {
      this.handleClose();
      let data = JSON.parse(JSON.stringify(item));
      data.beginTime = data.beginTime + ":00";
      data.endTime = data.endTime + ":00";

      this.form.id = data.id;
      this.form.name = data.name;
      this.form.dateTimePickerRanger[0] = data.beginTime;
      this.form.dateTimePickerRanger[1] = data.endTime;
      this.form.buyLimit = data.buyLimit;
      this.form.formTitle = "编辑秒杀活动";
    },
    // 复制一场
    Copy(item) {
      this.handleClose();
      let data = JSON.parse(JSON.stringify(item));
      data.beginTime = data.beginTime + ":00";
      data.endTime = data.endTime + ":00";

      this.form.id = data.id;
      this.form.name = data.name;
      this.form.dateTimePickerRanger[0] = data.beginTime;
      this.form.dateTimePickerRanger[1] = data.endTime;
      this.form.buyLimit = data.buyLimit;
      this.form.formTitle = "复制秒杀活动";
    },
    // 同步缓存
    Sync(item) {
      let that = this;
      let msg = '你确定同步"' + item.name + '"活动至页面？';
      comm.message2(msg, "提示", "warning", that).then(res => {
        if (res == "confirm") {
          Syncs({ flashId: item.id }).then(res => {
            if (res.status === 0) {
              comm.message(res.msg, "success", this);
            }
          });
        }
      });
    },

    // 设置商品
    Jump(item) {
      item.type = this.type;
      this.$router.push({
        path: "/seckill/good",
        query: {
          data: JSON.stringify(item)
        }
      });
    },
    Delete(item) {
      let that = this;
      let msg = '你确定删除秒杀活动"' + item.name + '"吗？';
      comm.message2(msg, "提示", "warning", that).then(res => {
        if (res == "confirm") {
          Deletes({ flashId: item.id }).then(res => {
            if (res.status === 0) {
              that.PageFun();
              comm.message(res.msg, "success", this);
            }
          });
        }
      });
    },
    // 导航标题切换
    nav_btn(i) {
      this.type = parseInt(i) + 1;
      this.page.pageNumber = 1;
      this.page.pageSize = 10;
      this.PageFun();
      this.clean();
    },
    // 搜索
    searchFun() {
      this.PageFun();
    },
    // 获取数据
    PageFun() {
      let that = this;
      let params = Object.assign(
        {
          type: this.type,
          pageNumber: this.page.pageNumber,
          pageSize: this.page.pageSize
        },
        that.searchFrom
      );
      Pages(params).then(res => {
        if (res.status == 0) {
          that.table = [];
          that.table.push(...res.data.list);
          that.page.totalPage = res.data.totalPage;
          that.page.totalRow = res.data.totalRow;
        } else {
          comm.message(res.msg, "warning", that);
        }
      });
    },
    // 前往第几页
    handleCurrentChange(val) {
      this.page.pageNumber = val;
      this.PageFun();
    },
    // 每页显示条数
    handleSizeChange(val) {
      this.page.pageSize = val;
      this.PageFun();
    }
  },
  mounted() {
    this.PageFun();
  }
};
</script>
<style lang="less" scoped>
.nav-search {
  .nav {
    display: flex;
    margin-bottom: 25px;
  }
  button {
    margin: 0 0.5rem;
  }
}
.submit {
  text-align: center;
  padding-top: 30px;
}
/deep/ .el-dialog--small {
  width: 30%;
}
.line {
  text-align: center;
}
.item_time {
  display: flex;
  flex-wrap: wrap;
}
</style>
