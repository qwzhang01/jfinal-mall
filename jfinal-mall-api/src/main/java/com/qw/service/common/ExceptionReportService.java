package com.qw.service.common;

import cn.qw.base.BaseService;
import cn.qw.kit.IpKit;
import com.qw.helper.MailHelper;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

public class ExceptionReportService extends BaseService {

    private static ExceptionReportService service;

    private ExceptionReportService() {
    }

    public static ExceptionReportService me() {
        if (service == null) {
            service = new ExceptionReportService();
        }
        return service;
    }

    public void report(Exception e, HttpServletRequest request) {
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
            MailHelper.sendSimpleEmail(request.getServerName(), content, mailTos);
        }
        log.error(content);
    }
}