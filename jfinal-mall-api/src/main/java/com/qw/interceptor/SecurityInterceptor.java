package com.qw.interceptor;

import cn.qw.base.RestController;
import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import cn.qw.kit.AppIdKit;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.qw.service.bakend.system.StaffService;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证接口是否安全合法调用
 *
 * @author qw
 */
@Clear
public class SecurityInterceptor implements Interceptor {

    protected final Log log = Log.getLog(getClass());

    @Override
    public void intercept(Invocation ai) {

        RestController c = (RestController) ai.getController();
        HttpServletRequest request = c.getRequest();

        String token = request.getHeader("token");
        String userId = request.getHeader("userId");

        if (valid(token, userId)) {
            try {
                ai.invoke();
            } finally {
                AppIdKit.removeThreadLocal();
            }
        } else {
            c.renderJson(new ApiResult(ApiStatus.signError, "验证失败，非法访问。"));
        }

    }

    /**
     * 校验后台登录状态
     */
    private boolean valid(String accessToken, String userId) {
        if (StrKit.isBlank(userId)) {
            userId = "0";
        }
        if ("null".equals(userId)) {
            userId = "0";
        }
        Integer userIdInt = 0;
        try {
            userIdInt = Integer.valueOf(userId);
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
        boolean validToken = StaffService.me().validToken(accessToken, userIdInt);
        if (validToken) {
            AppIdKit.setUserId(userIdInt);
            return true;
        }
        return false;
    }
}