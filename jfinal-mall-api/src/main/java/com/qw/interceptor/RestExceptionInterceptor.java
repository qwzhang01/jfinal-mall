package com.qw.interceptor;

import cn.qw.base.RestController;
import com.qw.service.common.ExceptionReportService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.ActionException;
import com.jfinal.log.Log;

import javax.servlet.http.HttpServletRequest;

public class RestExceptionInterceptor implements Interceptor {

    protected final Log log = Log.getLog(getClass());

    @Override
    public void intercept(Invocation ai) {
        try {
            ai.invoke();// 执行正常逻辑
        } catch (Exception e) {
            // 格式化异常情况返回值
            RestController c = (RestController) ai.getController();
            HttpServletRequest request = c.getRequest();
            if (e instanceof ActionException) {
                int errorCode = ((ActionException) e).getErrorCode();
                if (errorCode == 404) {
                    c.renderCommonError(errorCode, "404，请求地址不存在。");
                    return;
                }
            }
            c.renderCommonError(500, e.getLocalizedMessage());
            ExceptionReportService.me().report(e, request);
        }
    }
}
