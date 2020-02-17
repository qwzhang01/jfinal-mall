    <template>
  <Nav :nav="nav" class="nav" @nav_btn="nav_btn">
    <div class="search_box">
      <el-button type="primary" @click="handleAdd">新增</el-button>
    </div>
    <div class="table-container">
      <!--列表-->
      <el-table :data="list" highlight-current-row v-loading="listLoading" style="width: 100%;">
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column width="120" align="center">
          <template slot-scope="scope">
            <el-image :src="scope.row.img" fit="cover"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          v-for="(val,i) in colomus"
          :key="i"
          :prop="val.props"
          :label="val.label"
          :formatter="fmtFn"
          :min-width="val.width"
        ></el-table-column>
        <el-table-column label="显示状态">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.is_show"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="2"
              @change="handleShowStatus(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button @click="detail(scope.row)" type="success" size="mini" v-if="type === 6">设置商品</el-button>
            <el-button @click="delSet(scope.row)" type="danger" size="mini">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Paging
        :page="page"
        @handleCurrentChange="handleCurrentChange"
        @handleSizeChange="handleSizeChange"
      ></Paging>
    </div>

    <!-- 新增banner表单 -->
    <el-dialog
      :title="addTitle"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="dialogVisible"
      width="30%"
    >
      <el-form :model="setForm" :rules="rules" ref="setForm" label-width="100px">
        <el-form-item label="标题" prop="title" :rules="rules.val" v-if="type == 2">
          <el-input type="input" v-model="setForm.title"></el-input>
        </el-form-item>

        <el-form-item label="跳转类型" prop="gotoType" :rules="rules.val">
          <el-select v-model="setForm.gotoType" placeholder="请选择跳转类型">
            <el-option
              v-for="item in gotoType"
              :key="item.key"
              :label="item.value"
              :value="item.key"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item
          label="跳转页面"
          prop="gotoId"
          :rules="rules.val"
          v-if="setForm.gotoType == 1 || setForm.gotoType == 2 || setForm.gotoType == 3 || setForm.gotoType == 5 || setForm.gotoType == 6"
        >
          <el-select
            v-model="setForm.gotoId"
            filterable
            remote
            :remote-method="getGotoId"
            placeholder="请选择跳转页面"
          >
            <el-option v-for="item in gotoId" :key="item.key" :label="item.value" :value="item.key"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="WEB地址：" prop="webUrl" :rules="rules.val" v-if=" setForm.gotoType == 4">
          <el-input type="input" v-model="setForm.webUrl"></el-input>
        </el-form-item>
        <el-form-item
          label="背景颜色："
          prop="bgColor"
          :rules="rules.val"
          v-if=" type == 1 || type == 5"
        >
          <el-input type="input" v-model="setForm.bgColor"></el-input>
        </el-form-item>
        <el-form-item label="图片" prop="img" :rules="rules.val">
          <el-upload
            class="good-uploader"
            :action="UpdateFileFast"
            :show-file-list="false"
            :on-success="successImg"
          >
            <el-image v-if="setForm.imgShowPath" :src="setForm.imgShowPath" fit="cover"></el-image>
            <i v-else class="el-icon-plus"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="setFormSubmit()">确 定</el-button>
      </span>
    </el-dialog>
  </Nav>
</template>
<script>
import Nav from "../../../components/nav";
import Paging from "../../../components/paging";
import util from "@/util/util";
import { pageList, del, findGoto, save, showSave } from "@/api/CMS/appSet";
import { Message, MessageBox } from "element-ui";
import { UpdateFileFast } from "../../../api/comm.js";
import rules from "@/comm/rules";
import { dict } from "@/api/system/param";

export default {
  components: {
    Nav,
    Paging
  },
  data() {
    return {
      UpdateFileFast,
      rules,
      gotoType: [],
      nav: [
        { name: "Banner", id: 0 },
        { name: "金刚区按钮", id: 1 },
        { name: "大广告", id: 2 },
        { name: "小广告", id: 3 },
        { name: "专题", id: 4 }
      ],
      listLoading: false,
      dialogVisible: false,
      page: {
        pageNumber: 1,
        pageSize: 10,
        totalPage: 1,
        totalRow: 0
      },
      list: [],
      colomus: [],
      type: 1, // 1banner2金刚区3资讯区4大广告5小广告6专场
      addTitle: "新增Banner",
      colomusBanner: [
        {
          label: "跳转类型",
          width: 100,
          props: "goto_type"
        },
        {
          label: "跳转页面",
          width: 200,
          props: "goto_id"
        },
        {
          label: "背景颜色",
          width: 100,
          props: "background_color"
        }
      ],
      colomusDiomand: [
        {
          label: "标题",
          width: 100,
          props: "title"
        },
        {
          label: "跳转类型",
          width: 200,
          props: "goto_type"
        },
        {
          label: "跳转页面",
          width: 200,
          props: "goto_id"
        }
      ],
      bigAdvertise: [
        {
          label: "跳转类型",
          width: 100,
          props: "goto_type"
        },
        {
          label: "跳转页面",
          width: 200,
          props: "goto_id"
        }
      ],
      smallAdvertise: [
        {
          label: "跳转类型",
          width: 100,
          props: "goto_type"
        },
        {
          label: "跳转页面",
          width: 200,
          props: "goto_id"
        },
        {
          label: "背景颜色",
          width: 100,
          props: "background_color"
        }
      ],
      specialShow: [
        {
          label: "跳转类型",
          width: 100,
          props: "goto_type"
        },
        {
          label: "跳转页面",
          width: 200,
          props: "goto_id"
        }
      ],
      setForm: {
        title: "",
        gotoType: "",
        gotoId: "",
        webUrl: "",
        img: "",
        imgShowPath: "",
        bgColor: ""
      },
      gotoId: [],
      gotoTypeName: [
        "",
        "资讯",
        "商品详情",
        "商品分类",
        "WEB地址",
        "店铺首页",
        "专题页",
        "推荐店铺",
        "邀请有礼",
        "拼团抽奖",
        "秒杀",
        "积分中心",
        "积分购买商品",
        "商品奖励积分"
      ],
      isShowName: ["", "显示", "不显示"]
    };
  },
  mounted() {
    let _this = this;
    dict("app_goto_type").then(res => {
      _this.gotoType = res.data;
    });
    this.colomus = this.colomusBanner;
    this.getList();
  },
  methods: {
    fmtFn(row, column) {
      let tmp = row[column.property];
      if ("goto_type" === column.property) {
        tmp = this.gotoTypeName[tmp];
      }
      if ("is_show" === column.property) {
        tmp = this.isShowName[tmp];
      }
      return tmp;
    },
    // Tab跳转，刷新列表
    nav_btn(i) {
      if (i == 0) {
        this.colomus = this.colomusBanner;
        this.type = 1;
      } else if (i == 1) {
        this.colomus = this.colomusDiomand;
        this.type = 2;
      } else if (2 == i) {
        this.colomus = this.bigAdvertise;
        this.type = 4;
      } else if (3 == i) {
        this.colomus = this.smallAdvertise;
        this.type = 5;
      } else if (4 == i) {
        this.colomus = this.specialShow;
        this.type = 6;
      }
      this.getList();
    },
    // 新增
    handleAdd() {
      this.dialogVisible = true;
      if (this.$refs["setForm"] !== undefined) {
        this.$refs["setForm"].resetFields();
        this.setForm.imgShowPath = "";
      }
    },
    // 提交表单
    setFormSubmit() {
      this.setForm.type = this.type;
      this.$refs["setForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        this.dialogVisible = false;
        save(this.setForm).then(res => {
          Message({
            message: "保存成功",
            type: "success",
            duration: 3 * 1000
          });
          this.getList();
        });
      });
    },
    getGotoId(query) {
      let _this = this;
      _this.gotoId = [];
      findGoto(_this.setForm.gotoType, query).then(res => {
        if (res.status === 0) {
          let gotoId = _this.setForm.gotoId;
          if (gotoId) {
            _this.gotoId = res.data.filter(item => {
              return item.key != gotoId;
            });
          } else {
            _this.gotoId = res.data;
          }
        }
      });
    },
    successImg(response, file) {
      if (response.status == 0) {
        this.setForm.img = response.data.savePath;
        this.setForm.imgShowPath = response.data.showPath;
      } else {
        comm.message(res.msg, "warning", this);
      }
    },
    // 删除
    delSet(row) {
      MessageBox.confirm("确定要删除吗", "删除", {
        confirmButtonText: "确定删除",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        del(row.id).then(res => {
          if (res.status === 0) {
            Message({
              message: "删除成功",
              type: "success",
              duration: 3 * 1000
            });
            this.getList();
          }
        });
      });
    },
    detail(row) {
      this.$router.push({
        path: "/app/specShow/good",
        query: {
          data: JSON.stringify(row)
        }
      });
    },
    handleShowStatus(row) {
      showSave({ id: row.id, isShow: row.is_show }).then(res => {
        if (res.status === 0) {
          Message({
            message: "设置成功",
            type: "success",
            duration: 3 * 1000
          });
        } else {
          Message({
            message: res.msg,
            type: "error",
            duration: 3 * 1000
          });
        }
      });
    },
    getList() {
      this.listLoading = true;
      let para = {
        pageNumber: this.page.pageNumber,
        pageSize: this.page.pageSize,
        type: this.type
      };
      //NProgress.start();
      pageList(para).then(res => {
        this.page.totalPage = res.data.totalPage;
        this.page.totalRow = res.data.totalRow;
        this.list = res.data.list;
        this.listLoading = false;
        //NProgress.done();
      });
    },
    // 前往第几页
    handleCurrentChange(val) {
      this.page.pageNumber = val;
      this.getList();
    },
    // 每页显示条数
    handleSizeChange(val) {
      this.page.pageSize = val;
      this.getList();
    }
  }
};
</script>
<style lang="less" scoped>
.nav {
  display: flex;
  margin-bottom: 25px;

  /deep/ .search_box {
    align-items: center;
    display: flex;
    justify-content: flex-end;
    margin-right: 1.25rem;
    padding-top: 14px;
  }
}
.good-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.good-uploader .el-upload:hover {
  border-color: #409eff;
}
.good-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 148px;
  height: 148px;
  line-height: 148px;
  text-align: center;
}
</style>