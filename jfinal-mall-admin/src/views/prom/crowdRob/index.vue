<template>
  <el-card class="box-card">
    <el-row class="nav-search">
      <!-- 导航 -->
      <Nav :nav="nav" class="nav" @nav_btn="nav_btn" :span="6"></Nav>
      <Search :search="search" :searchFrom="searchFrom" :span="18">
        <el-button type="success" @click="searchFun">搜索</el-button>
        <el-button type="primary" @click="handleClose" v-if="type===1">添加活动</el-button>
        <el-button type="warning" @click="set" v-if="type===1">设置</el-button>
      </Search>
    </el-row>
    <!-- 表格 -->
    <Table :table="table" :tableKey="tableKey">
      <el-table-column label="操作" width="250">
        <template slot-scope="scope">
          <el-button
            type="success"
            size="mini"
            @click="Jump(scope.row)"
          >{{type === 1?'设置商品': '查看商品'}}</el-button>
        </template>
      </el-table-column>
    </Table>
    <!-- 分页 -->
    <Paging
      :page="page"
      @handleCurrentChange="handleCurrentChange"
      @handleSizeChange="handleSizeChange"
    ></Paging>

    <!-- 弹窗 活动-->
    <el-dialog
      title="添加抽奖活动"
      :visible.sync="dialogVisible"
      width="50%"
      :close-on-click-modal="false"
      :rules="rules"
      :before-close="handleClose"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="抽奖活动名称：" prop="title" :rules="rules.val">
          <el-input v-model="form.title" type="text" placeholder="请输入抽奖活动名称" style="width:55%"></el-input>
        </el-form-item>
        <el-form-item label="成团人数" prop="maxNum" :rules="rules.val">
          <el-input v-model="form.maxNum" type="number" placeholder="请输入成团人数" style="width:55%"></el-input>
        </el-form-item>
        <el-form-item label="最小开团人数" prop="minNum" :rules="rules.val">
          <el-input v-model="form.minNum" type="number" placeholder="请输入最小开团人数" style="width:55%"></el-input>
        </el-form-item>
        <el-form-item label="活动时间" prop="dateTime" :rules="rules.val">
          <el-date-picker
            v-model="form.dateTime"
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
        <el-form-item label="开团类型" prop="type" :rules="rules.val">
          <el-radio-group v-model="form.type" size="small">
            <el-radio :key="1" :label="1">定时开</el-radio>
            <el-radio :key="2" :label="2">人满开</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item class="submit" v-if="type == 1 || show_type">
          <el-button type="primary" @click="onSubmit('form')" v-if="show_type">立即创建</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 弹窗 设置 -->
    <el-dialog
      title="活动设置"
      :visible.sync="dialogVisible2"
      width="50%"
      :rules="rules"
      :close-on-click-modal="false"
      :before-close="handleClose2"
    >
      <div>
        <From
          :form="setform"
          :formKey="setformKey"
          :submit="'保存'"
          :formName="'setform'"
          :disabled="set_submit_disabled"
          @submit="onSubmit"
        />
      </div>
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
import From from "../../../components/from";
import Paging from "../../../components/paging";
import {
  Pages,
  Forms,
  setShow,
  setHomeImg,
  setBanner,
  getSet,
  setReg
} from "../../../api/prom/crowdRob.js";
export default {
  components: {
    Nav,
    Search,
    Table,
    Paging,
    From
  },
  data() {
    return {
      rules,
      dialogVisible: false,
      dialogVisible2: false,
      show_type: true,
      sunbmitName: "添加活动",
      // 导航菜单
      nav: config.nav,
      // 搜索key
      search: config.search,
      // 表格
      table: [],
      //  表格key
      tableKey: config.tableKey,
      // 表单 key
      formKey: config.activityformKey,
      // 设置表单
      setform: {
        // 首页是否显示
        isShow: "1",
        // 首页图片
        homeImg: {
          // 路径图片
          savePath: "",
          // 显示图片
          showPath: "",
          ps: "注意：1"
        },
        // 活动页bannber图
        bannerImg: {
          // 路径图片
          savePath: "",
          // 显示图片
          showPath: "",
          ps: "注意：2"
        },
        // 规则图片
        ruleImg: {
          // 路径图片
          savePath: "",
          // 显示图片
          showPath: "",
          ps: "注意：3"
        }
      },
      // 设置表单key
      setformKey: config.setformKey,
      // 设置表单提交禁用状态
      set_submit_disabled: false,
      // 搜索
      searchFrom: {
        title: ""
      },
      // 活动表单
      form: {
        id: "",
        title: "",
        maxNum: "",
        minNum: "",
        type: "",
        startTime: "",
        endTime: "",
        dateTime: []
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
      // 提交按钮 禁用状态
      submit_disabled: false
    };
  },

  methods: {
    // 导航标题切换
    nav_btn(i) {
      this.type = parseInt(i) + 1;
      this.page.pageNumber = 1;
      this.page.pageSize = 10;
      this.PageFun();
      this.clean();
    },
    // 弹窗
    handleClose() {
      // console.log('关闭弹窗');
      this.dialogVisible = !this.dialogVisible;
      this.show_type = true;
      if (this.$refs["form"] !== undefined) {
        this.$refs["form"].resetFields();
      }
      this.clean();
    },
    // 提交
    onSubmit(formName) {
      let _this = this;
      if (formName == "form") {
        _this.$refs[formName].validate(valid => {
          if (valid) {
            // 参团人数
            if (parseInt(_this.form.maxNum) < parseInt(_this.form.minNum)) {
              _this.$message({
                message: "参团最大人数必须大于或者等于参团最小人数",
                type: "warning"
              });
              return;
            }
            // 日期格式转换
            let time = _this.form.dateTime;
            if (time.length !== 2) {
              _this.$message("请选择活动时间", "warning");
              return;
            }
            _this.form.startTime = this.form.dateTime[0];
            _this.form.endTime = this.form.dateTime[1];
            delete _this.form.dateTime;
            if (
              comm.stringToDate(_this.form.startTime) >=
              comm.stringToDate(_this.form.endTime)
            ) {
              _this.$message({
                message: "抽奖开始时间不能大于或等于结束时间",
                type: "warning"
              });
              return false;
            }
            if (new Date() >= comm.stringToDate(_this.form.startTime)) {
              _this.$message({
                message: "活动开始时间不能小于或等于现在",
                type: "warning"
              });
              return false;
            }
            _this.FormsFun(this.form);
          }
        });
      } else if (formName == "setform") {
        // 设置图片校验
        if (
          !_this.setform.bannerImg.savePath ||
          !_this.setform.homeImg.savePath ||
          !_this.setform.ruleImg.savePath
        ) {
          comm.message("请上传图片", "warning", _this);
          return;
        }
        // console.log(this.setform.bannerImg.savePath);
        _this.setShowFun(
          parseInt(_this.setform.isShow) == 1 ? "true" : "false"
        );
        _this.setHomeImgFun(_this.setform.homeImg.savePath);
        _this.bannerFun(_this.setform.bannerImg.savePath);
        _this.setRegFun(_this.setform.ruleImg.savePath);
        _this.dialogVisible2 = !_this.dialogVisible2;
      }
    },
    // 设置首页是否显示
    setShowFun(isShow) {
      setShow({ isShow }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", this);
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },

    //设置首页图片
    setHomeImgFun(homeImg) {
      return setHomeImg({ homeImg }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", this);
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },
    // 设置活动banner图片
    bannerFun(banner) {
      return setBanner({ banner }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", this);
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },
    // 设置活动图片
    setRegFun(regulation) {
      setReg({ regulation }).then(res => {
        if (res.status == 0) {
          comm.message(res.msg, "success", this);
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },
    // 获取抢购抽奖设置信息
    getSetFun() {
      let that = this;
      getSet().then(res => {
        if (res.status == 0) {
          // console.log(res.data);
          // 设置表单
          that.setform.isShow = res.data.isShow ? "1" : "2";
          that.setform.homeImg.savePath = res.data.homeImg.savePath;
          that.setform.homeImg.showPath = res.data.homeImg.showPath;
          that.setform.bannerImg.savePath = res.data.banner.savePath;
          that.setform.bannerImg.showPath = res.data.banner.showPath;
          that.setform.ruleImg.savePath = res.data.regulation.savePath;
          that.setform.ruleImg.showPath = res.data.regulation.showPath;
        }
      });
    },
    Jump(item) {
      item.type = this.type;
      console.log(item);
      this.$router.push({
        path: "/crowdRob/good",
        query: {
          data: JSON.stringify(item)
        }
      });
    },
    // 获取数据
    PageFun() {
      let params = Object.assign(
        {
          type: this.type,
          pageNumber: this.page.pageNumber,
          pageSize: this.page.pageSize
        },
        this.searchFrom
      );
      let that = this;
      Pages(params).then(res => {
        if (res.status == 0) {
          that.table = [];
          that.table.push(...res.data.list);
          for (var i = 0; i < res.data.list.length; i++) {
            that.table[i].type =
              that.table[i].type == 1
                ? "定时开"
                : that.table[i].type == 2
                ? "人满开"
                : "";
          }
          that.page.totalPage = res.data.totalPage;
          that.page.totalRow = res.data.totalRow;
        } else {
          comm.message(res.msg, "warning", this);
        }
      });
    },
    // 新增/编辑
    FormsFun(form) {
      let that = this;
      Forms(form).then(res => {
        if (res.status == 0) {
          that.clean();
          that.PageFun();
          that.dialogVisible = !that.dialogVisible;
          comm.message(res.msg, "success", this);
        }
      });
    },
    //  清除表单
    clean() {
      this.form.id = "";
      this.form.startTime = "";
      this.form.endTime = "";
      this.form.title = "";
      this.form.maxNum = "";
      this.form.minNum = "";
      this.form.type = "";
      this.form.dateTime = [];
      this.searchFrom.title = "";
      this.searchFrom.startTime = "";
      this.searchFrom.endTime = "";
      this.searchFrom.dateTime = [];
    },
    // 搜索
    searchFun() {
      this.PageFun();
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
    },
    handleClose2() {
      this.dialogVisible2 = !this.dialogVisible2;
      if (this.$refs["setform"] !== undefined) {
        this.$refs["setform"].resetFields();
      }
    },
    // 设置
    set() {
      this.dialogVisible2 = !this.dialogVisible2;
      this.getSetFun();
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
