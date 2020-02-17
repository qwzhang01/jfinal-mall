package com.qw.shiro;

import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import cn.qw.shiro.AuthzHandler;
import cn.qw.shiro.ShiroKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;

public class ShiroInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        AuthzHandler ah = ShiroKit.getAuthzHandler(ai.getActionKey());
        if (ah != null) {
            Controller c = ai.getController();
            try {
                ah.assertAuthorized();// 执行权限检查。
            } catch (UnauthenticatedException lae) {
                // RequiresGuest，RequiresAuthentication，RequiresUser，未满足时，抛出未经授权的异常。
                // 如果没有进行身份验证，返回HTTP401状态码
                c.renderJson(new ApiResult(null, ApiStatus.signError, "没有登录"));
                return;
            } catch (AuthorizationException ae) {
                // RequiresRoles，RequiresPermissions授权异常
                // 如果没有权限访问对应的资源，返回HTTP状态码403。
                c.renderJson(new ApiResult(null, ApiStatus.operateError, "没有权限"));
                return;
            } catch (Exception e) {// 出现了异常，应该是没有登录。
                c.renderJson(new ApiResult(null, ApiStatus.signError, "授权校验异常"));
                return;
            }
        }
        // 执行正常逻辑
        ai.invoke();
    }
}