package com.qw.controller.common;

import cn.qw.base.RestController;
import com.qw.helper.MailHelper;

/**
 * 邮件管理
 */
public class SendMailController extends RestController {

    /**
     * @param subject|标题|String|必填
     * @param content|内容|String|必填
     * @param mailTos|邮箱地址|String|必填
     * @title 发送邮件API
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void send_mail() {
        String subject = getPara("subject");
        String content = getPara("content");
        String mailTos = getPara("mailTos");
        try {
            MailHelper.sendSimpleEmail(subject, content, mailTos);
            renderJson("发送成功！");
        } catch (Exception ex) {
            renderOperateError(ex.getMessage());
        }
    }
}