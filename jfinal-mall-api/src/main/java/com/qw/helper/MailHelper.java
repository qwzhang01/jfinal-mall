package com.qw.helper;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.qw.service.common.ConfigService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import java.util.Map;

public class MailHelper {

    private static final Log log = Log.getLog(MailHelper.class);

    public static void sendSimpleEmail(String subject, String msg, String... mailTos) {
        try {
            SimpleEmail email = new SimpleEmail();
            email.setMsg(msg);
            send(email, subject, mailTos);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public static void sendHtmlEmail(String subject, String htmlMsg, String... mailTos) {
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHtmlMsg(htmlMsg);
            send(email, subject, mailTos);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private static void send(Email email, String subject, String... mailTos) throws EmailException {
        if (StrKit.isBlank(subject)) {
            subject = "白金管家运营管理平台";
        }
        Map<String, String> mailConfig = ConfigService.me().findMap("mail_config");
        email.setHostName(mailConfig.get("host_name"));
        email.setAuthentication(mailConfig.get("mail_from"), mailConfig.get("mail_pwd"));
        email.setCharset("UTF-8");
        email.setSubject(subject);
        email.addTo(mailTos);
        email.setFrom(mailConfig.get("mail_from"), "白金管家运营管理平台");
        email.setSSLOnConnect(true);
        email.setSmtpPort(465);
        email.send();
    }
}
