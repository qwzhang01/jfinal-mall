package cn.qw.kit;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.qw.service.common.ConfigService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import java.util.Map;

public class MailKit {

    private static final Log log = Log.getLog(MailKit.class);

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

    private static final String host_name = "smtp.126.com";
    private static final String mail_from = "weirdoqi@126.com";
    private static final String mail_pwd = "qiqiweirdo110";

    private static void send(Email email, String subject, String... mailTos) throws EmailException {
        email.setHostName(host_name);
        email.setAuthentication(mail_from, mail_pwd);
        email.setCharset("UTF-8");
        email.setSubject(subject);
        email.addTo(mailTos);
        email.setFrom(mail_from, PropKit.get("name"));
        email.setSSLOnConnect(true);
        email.setSmtpPort(465);
        email.send();
    }
}
