<template>
  <div>
    <el-form :ref="formName" :model="form" :rules="rules" label-width="120px" style="width: 100%;">
      <div v-for="(item,key) in formKey" :key="key">
        <!-- 搜索 -->
        <el-form-item
          :label="item.name"
          :prop="item.val"
          v-if="item.type=='search'"
          :rules="rules.val"
        >
          <!-- multiple true:多选 false:单选 -->
          <el-select
            style="width: 90%;"
            v-model="form[item.val]"
            :multiple="false"
            filterable
            remote
            @change="changeInput"
            reserve-keyword
            :placeholder="item.placeholder?item.placeholder:`请输入${item.name}`"
            :remote-method="remoteMethod"
            :loading="loading"
          >
            <el-option v-for="(item,key) in options4" :key="key" :label="item.label" :value="item"></el-option>
          </el-select>
        </el-form-item>
        <!-- 输入框 -->
        <el-form-item
          :label="item.name"
          :prop="item.val"
          :rules="
                    item.test=='input'?rules.val:
                    item.test=='int'?rules.InterNum:
                    item.test=='int2'?rules.InterNum2:
                    item.test=='decimal'?rules.numPot2:
                    ''
                "
          v-if="item.type=='input'"
        >
          <el-input
            v-model="form[item.val]"
            :type="item.inputType?item.inputType:'text'"
            :placeholder="item.placeholder?item.placeholder:`请输入${item.name}`"
            style="width: 90%;"
          ></el-input>
          <span v-html="item.modification"></span>
        </el-form-item>
        <!-- 日期和时间范围 -->
        <el-form-item
          :label="item.name"
          :prop="item.val"
          :rules="
                    item.test=='input'?rules.val:
                    item.test=='int'?rules.InterNum:
                    item.test=='int2'?rules.InterNum2:
                    item.test=='decimal'?rules.numPot2:
                    ''
                "
          v-if="item.type=='dateTime'"
        >
          <el-date-picker
            v-model="form[item.val]"
            :editable="false"
            :clearable="true"
            :time-arrow-control="true"
            :unlink-panels="true"
            format="yyyy-MM-dd HH:mm"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetimerange"
            range-separator=" 至 "
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          ></el-date-picker>
        </el-form-item>
        <!-- 选择框 -->
        <el-form-item
          :label="item.name"
          :prop="item.val"
          :rules="
                    item.test=='input'?rules.val:
                    item.test=='int'?rules.InterNum:
                    item.test=='int2'?rules.InterNum2:
                    item.test=='decimal'?rules.numPot2:
                    ''
                "
          v-if="item.type=='select'"
        >
          <el-select
            v-model="form[item.val]"
            :placeholder="item.placeholder?item.placeholder:`请输入${item.name}`"
          >
            <el-option v-for="i in item.options" :key="i.value" :label="i.label" :value="i.value"></el-option>
          </el-select>
        </el-form-item>
        <!-- 单选框 -->
        <el-form-item :label="item.name" v-if="item.type=='radio'">
          <el-radio-group v-model="form[item.val]" size="small">
            <el-radio
              v-for="(item,index) in item.radioArr"
              :key="index"
              border
              :label="item.index"
            >{{item.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <!-- 上传图片 -->
        <el-form-item :label="item.name" v-if="item.type=='uploaderImg'">
          <div @change="uploader(item.val)">
            <el-upload
              class="avatar-uploader"
              :action="UpdateFileFast"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="form[item.val].showPath" :src="form[item.val].showPath" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon" id="uploader_icon"></i>
            </el-upload>
          </div>
          <span v-html="form[item.val].ps"></span>
        </el-form-item>
      </div>

      <slot></slot>
      <el-form-item class="submit" v-if="submit">
        <el-button type="primary" @click="onSubmit(formName)" :disabled="disabled">{{submit}}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { UpdateFileFast } from "../api/comm.js";
// 表单
import { mapGetters } from "vuex";
import rules from "../comm/rules";
export default {
  props: [
    "form", // 表单数据
    "formKey", // 表单的key
    "states", // 搜索的数据集合
    "formName", // 表单名字
    "submit", // 提交按钮的名字
    "disabled" // 按钮是否禁用
  ],
  data() {
    return {
      UpdateFileFast,
      rules,
      // 搜索
      options4: "",
      list: [],
      loading: false,
      // fom属性
      fromkey: ""
    };
  },
  computed: {
    ...mapGetters(["getFrom"])
  },
  mounted() {},
  methods: {
    // 获取from 属性
    uploader(item) {
      this.fromkey = item;
    },
    // 上传图片
    handleAvatarSuccess(res, file) {
      // 整合
      this.form[this.fromkey].savePath = res.data.savePath;
      this.form[this.fromkey].showPath = res.data.showPath;
    },

    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 2;

      // if (!isJPG) {
      //   this.$message.error('上传头像图片只能是 JPG 格式!');
      // }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }
      // return isJPG && isLt2M;
      return isLt2M;
    },

    // 监听获取产品
    changeInput(item) {
      setTimeout(() => {
        this.$emit("goods", item);
      }, 500);
    },
    // 搜索输入的值
    remoteMethod(query) {
      this.$emit("changeInputFun", query);
      setTimeout(() => {
        this.list = this.states;
        if (query !== "") {
          this.loading = true;
          setTimeout(() => {
            this.loading = false;
            this.options4 = this.list.filter(item => {
              if (item.label.toLowerCase().indexOf(query.toLowerCase()) > -1) {
                return item.label;
              }
            });
          }, 200);
        } else {
          this.options4 = "";
        }
      }, 300);
    },
    // 提交
    onSubmit(formName) {
      let _this = this;
      _this.$refs[formName].validate(valid => {
        if (valid) {
          _this.$emit("submit", formName);
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    }
  },
  created() {
    if (this.$refs[this.formName] !== undefined) {
      this.$refs[this.formName].resetFields();
    }
  }
};
</script>
<style lang="less" scoped>
.submit {
  margin-top: 50px;
}
/deep/ .el-select__tags-text {
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
#uploader_icon {
  border: 1px dotted #999999;
  padding: 35px;
}
.avatar {
  width: 10rem;
}
</style>
