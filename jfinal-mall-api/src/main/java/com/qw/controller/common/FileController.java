package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import cn.qw.kit.FastDFSKit;
import com.qw.interceptor.RestSecurityInterceptor;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * 图片上传
 */
public class FileController extends RestController {

    /**
     * @title 图片上传(File)
     * @respBody {"status":"0","data":"", "msg":"操作成功"}
     * @respParam showPath|回显地址|String|必填
     * @respParam savePath|保存地址|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void updateFile() {
        UploadFile sourceFile = getFile();
        String fileName = sourceFile.getFileName();
        // 生成唯一的文件名(避免原文件名带有中文)
        int index = fileName.lastIndexOf(".");
        // 获取后缀名
        String fileNameHouZhui = fileName.substring(index);
        String uuidFileName = UUID.randomUUID().toString().replace("-", "") + fileNameHouZhui;
        String file = FastDFSKit.uploadFile(sourceFile.getFile(), uuidFileName);

        Record result = new Record();
        result.set("showPath", PropKit.get("fileHost") + file);
        result.set("savePath", file);
        String key = getPara("key");
        if (StrKit.notBlank(key)) {
            result.set("key", key);
        }
        renderJson(result);
    }

    /**
     * @title 上传base64字符串 图片
     * @respBody {"status":"0","data":"", "msg":"操作成功"}
     * @respParam showPath|回显地址|String|必填
     * @respParam savePath|保存地址|String|必填
     */
    @Clear(RestSecurityInterceptor.class)
    public void updateString() {
        HttpServletRequest request = getRequest();
        String img = HttpKit.readData(request);
        if (StrKit.isBlank(img)) {
            renderParamNull("上传文件为空");
            return;
        }
        img = img.replaceAll("data:image/jpeg;base64,", "");
        String pre = ".jpg";
        // 说明是多张图片
        if (img.contains(",")) {
            String[] split = img.split(",");
            img = split[1];
            pre = split[0].split("/")[1].split(";")[0];
            pre = "." + pre;
        }
        img = img.replaceAll("\r|\n", "");

        String fileName = DateKit.dateToString(new Date(), "yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(5) + pre;

        byte[] decode = Base64Kit.decode(img);
        String file = FastDFSKit.uploadFile(decode, fileName);
        Record result = new Record();
        result.set("showPath", PropKit.get("fileHost") + file);
        result.set("savePath", file);
        String key = getPara("key");
        if (StrKit.notBlank(key)) {
            result.set("key", key);
        }
        renderJson(result);
    }
}