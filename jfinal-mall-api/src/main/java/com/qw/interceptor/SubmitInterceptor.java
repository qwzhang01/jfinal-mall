package com.qw.interceptor;


import cn.qw.base.RestController;
import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import cn.qw.kit.AppIdKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * 防止重复提交拦截器，目前设置是同一秒不可以请求多次
 */
public class SubmitInterceptor implements Interceptor {
    private static final String CACHE_NAME = "com.qw.Resubmit";

    @Override
    public void intercept(Invocation invocation) {
        Integer userId = AppIdKit.getUserId();
        if (userId != 0) {
            String ping = CacheKit.get(CACHE_NAME, userId);
            if (StrKit.notBlank(ping)) {
                RestController c = (RestController) invocation.getController();
                c.renderJson(new ApiResult(ApiStatus.operateError, "请勿重复请求。"));
                return;
            } else {
                CacheKit.put(CACHE_NAME, userId, "pong");
            }
        }
        invocation.invoke();
    }
}