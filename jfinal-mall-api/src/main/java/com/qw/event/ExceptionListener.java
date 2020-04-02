package com.qw.event;

import cn.qw.kit.IpKit;
import cn.qw.kit.MailKit;
import cn.qw.plugin.event.EventObject;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

public class ExceptionListener implements EventObject {
    private  final Log log = Log.getLog(this.getClass());

    private Exception e;
    private HttpServletRequest request;

    public ExceptionListener(Exception e, HttpServletRequest request){
        this.e = e;
        this.request = request;
    }

    @Override
    public void run() {
        StringBuffer requestUrl = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestUrl.append('?');
            requestUrl.append(request.getQueryString());
        }
        String remoteIp = IpKit.getIpAddr(request);
        String requestPara = JsonKit.toJson(request.getParameterMap());
        String content = "";
        content += "\nErrorMessage:" + e.getMessage();
        content += "\nRequestUrl:" + requestUrl;
        content += "\nRemoteIp:" + remoteIp;
        content += "\nRequestPara:" + requestPara;
        content += "\nStackTrace:" + ExceptionUtils.getStackTrace(e);

        if (PropKit.getBoolean("mailMode", false)) {
            String[] mailTos = {"qwzh110@qq.com"};
            MailKit.sendSimpleEmail(request.getServerName(), content, mailTos);
        }
        log.error(content);
    }
}
