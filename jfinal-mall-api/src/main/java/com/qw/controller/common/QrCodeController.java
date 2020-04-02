package com.qw.controller.common;

import cn.qw.base.RestController;
import cn.qw.kit.Base64Decoder;
import cn.qw.render.QrCodeRender;
import com.jfinal.aop.Clear;
import com.qw.interceptor.SecurityInterceptor;

/**
 * 二维码
 */
public class QrCodeController extends RestController {
    /**
     * @param title|标题|String|必填
     * @param width|宽度|int|必填
     * @param height|高度|int|必填
     * @param url|Base64URL|String|必填
     * @title 根据链接生成二维码
     */
    @Clear(SecurityInterceptor.class)
    public void index() {
        String title = getPara("title", "白金管家");
        int width = getParaToInt("width", 400);
        int height = getParaToInt("height", 400);
        String url = getPara("url");
        url = Base64Decoder.decode(url);

        String logo = this.getClass().getClassLoader().getResource("/logo.png").getPath();

        render(new QrCodeRender(url, title, width, height, logo));
    }
}
