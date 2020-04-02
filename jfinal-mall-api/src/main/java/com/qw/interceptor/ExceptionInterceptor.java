package com.qw.interceptor;

import cn.qw.base.RestController;
import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import cn.qw.plugin.event.EventKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.ActionException;
import com.jfinal.log.Log;
import com.qw.event.ExceptionListener;

import javax.servlet.http.HttpServletRequest;

public class ExceptionInterceptor implements Interceptor {

    protected final Log log = Log.getLog(getClass());

    @Override
    public void intercept(Invocation ai) {
        try {
            ai.invoke();
        } catch (Exception e) {
            RestController c = (RestController) ai.getController();
            HttpServletRequest request = c.getRequest();
            if (e instanceof ActionException) {
                int errorCode = ((ActionException) e).getErrorCode();
                if (errorCode == 404) {
                    c.renderJson(new ApiResult(ApiStatus.operateError, "404，请求地址不存在。"));
                    return;
                }
            }
            c.renderJson(new ApiResult(ApiStatus.operateError, e.getMessage()));
            EventKit.post(new ExceptionListener(e, request));
        }
    }
}
