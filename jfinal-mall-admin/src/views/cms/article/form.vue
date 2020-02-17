<template>
  <section class="app-container">
    <div class="table-container">
      <el-form :model="articleForm" :rules="rules" ref="articleForm" label-width="100px">
        <el-form-item label="标题：" prop="title" :rules="rules.val">
          <el-input type="input" v-model="articleForm.title"></el-input>
        </el-form-item>
        <el-form-item label="作者：" prop="author" :rules="rules.val">
          <el-input type="input" v-model="articleForm.author"></el-input>
        </el-form-item>
        <el-form-item label="关键字：" prop="keywords" :rules="rules.val">
          <el-input type="input" v-model="articleForm.keywords"></el-input>
        </el-form-item>
        <el-form-item label="缩略图" prop="thumb" :rules="rules.val">
          <el-upload
            class="good-uploader"
            :action="UpdateFileFast"
            :show-file-list="false"
            :on-success="handleThumbSuccess"
            :before-upload="beforeThumbUpload"
          >
            <img v-if="articleForm.showThumb" :src="articleForm.showThumb" class="good" />
            <i v-else class="el-icon-plus good-uploader-icon good"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="内容：" prop="showContent">
          <editor api-key="API_KEY" id="tinymce" v-model="articleForm.showContent" :init="init"></editor>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleCancel()">重 置</el-button>
        <el-button type="primary" @click="handleSubmit()" :disabled="subDisabled">保 存</el-button>
      </span>
    </div>
  </section>
</template>
<script>
import util from "@/util/util";
import { saveArticle, detailArticle } from "@/api/CMS/article";
import { Message, MessageBox } from "element-ui";

import tinymce from "tinymce/tinymce";
import Editor from "@tinymce/tinymce-vue";
import "tinymce/themes/silver/theme";
import "tinymce/plugins/image";
import "tinymce/plugins/media";
import "tinymce/plugins/table";
import "tinymce/plugins/lists";
import "tinymce/plugins/contextmenu";
import "tinymce/plugins/wordcount";
import "tinymce/plugins/colorpicker";
import "tinymce/plugins/textcolor";

import rules from "@/comm/rules";
let Base64 = require("js-base64").Base64;
import { UpdateFileFast, UpdateFileFastFn } from "../../../api/comm.js";

export default {
  components: { editor: Editor },
  data() {
    return {
      UpdateFileFast,
      subDisabled: false,
      API_KEY: "b4d3m0aae39vlsudqrzyh4ifdxebbeuwoprcbq4y7u0hp8py",
      rules,
      init: {
        toolbar1:
          "undo redo | styleselect | fontselect | fontsizeselect | bold italic underline strikethrough | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent",
        toolbar2:
          "fullscreen preview | forecolor backcolor emoticons | table | link image media | mybutton",
        language_url: "/static/tinymce/langs/zh_CN.js", //语言包的路径
        language: "zh_CN", //语言
        skin_url: "/static/tinymce/skins/ui/oxide", //skin路径
        content_css: "/static/tinymce/skins/content/default",
        height: 600, //编辑器高度
        branding: true, //是否禁用“Powered by TinyMCE”
        menubar: false, //顶部菜单栏显示
        plugins: ["wordcount", "image", "media", "table", "colorpicker"],
        images_upload_url: UpdateFileFast,
        //images_upload_base_path: UpdateFile,
        //images_upload_credentials: UpdateFile,
        //上传图片回调
        images_upload_handler: (blobInfo, success, failure) => {
          let fd = new FormData();
          fd.append("file", blobInfo.blob());
          UpdateFileFastFn(fd)
            .then(res => {
              let result = res.data;
              success(result.showPath);
            })
            .catch(err => {});
        }
      },
      articleForm: {
        id: "",
        title: "",
        author: "",
        keywords: "",
        thumb: "",
        showThumb: "",
        content: "",
        showContent: ""
      }
    };
  },
  mounted() {
    if (this.$route.query.articleId) {
      this.articleForm.id = this.$route.query.articleId;
      this.detail();
    }
  },
  methods: {
    handleThumbSuccess(res, file) {
      this.articleForm.showThumb = URL.createObjectURL(file.raw);
      this.articleForm.thumb = res.data.savePath;
    },
    beforeThumbUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("上传商品图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传商品图片大小不能超过 2MB!");
      }
      return isJPG && isLt2M;
    },
    // 获取详情
    detail() {
      let _this = this;
      detailArticle(this.articleForm.id).then(res => {
        if (res.status == 0) {
          _this.articleForm = res.data;
          _this.articleForm.showContent = Base64.decode(
            _this.articleForm.content
          );
        }
      });
    },
    handleCancel() {
      if (this.$refs["articleForm"] !== undefined) {
        this.$refs["articleForm"].resetFields();
      }
    },
    handleSubmit() {
      this.$refs["articleForm"].validate(valid => {
        if (!valid) {
          return false;
        }
        this.subDisabled = false;
        this.articleForm.content = Base64.encode(this.articleForm.showContent);
        let form = this.articleForm;
        delete form.showContent;
        saveArticle(form).then(res => {
          if (res.status === 0) {
            this.inputVisible = false;
            Message({
              message: "保存成功",
              type: "success",
              duration: 3 * 1000
            });
            this.$router.push("/cms/article");
          }
        });
      });
    }
  }
};
</script>
<style scoped>
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
.good {
  border: 1px dashed #c0ccda !important;
  width: 148px;
  height: 148px;
  display: block;
  background-color: #fbfdff;
}
</style>