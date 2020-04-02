package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.DateKit;
import cn.qw.kit.UploadKit;
import cn.qw.render.FileRender;
import cn.qw.shiro.ClearShiro;
import com.jfinal.aop.Clear;
import com.jfinal.kit.Base64Kit;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.qw.interceptor.SecurityInterceptor;
import org.apache.commons.lang3.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.UUID;

/**
 * 图片上传
 */
public class FileController extends RestController {

    @Clear(SecurityInterceptor.class)
    public void show() {
        String path = get("p");
        String file_path = PropKit.get("file_path");
        String fileUrl = file_path + File.separator + path;
        render(new FileRender(fileUrl));
    }
    /**
     * @title 图片上传(File)
     * @respBody {"status":"0","data":"", "msg":"操作成功"}
     * @respParam showPath|回显地址|String|必填
     * @respParam savePath|保存地址|String|必填
     */
    @Clear(SecurityInterceptor.class)
    public void updateFile() {
        UploadFile sourceFile = getFile();
        File file = sourceFile.getFile();
        Record result = new Record();
        result.set("savePath", "web/file/show?p=" + file.getName());
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
        String file = UploadKit.uploadFile(decode, fileName);
        Record result = new Record();
        result.set("savePath", file);
        String key = getPara("key");
        if (StrKit.notBlank(key)) {
            result.set("key", key);
        }
        renderJson(result);
    }
}