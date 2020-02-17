package com.qw.interceptor;

import cn.qw.base.RestController;
import cn.qw.entity.ApiResult;
import cn.qw.entity.ApiStatus;
import cn.qw.kit.AppIdKit;
import com.qw.service.bakend.system.StaffService;
import com.qw.service.frontend.member.UserService;
import com.jfinal.aop.Clear;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 验证接口是否安全合法调用
 *
 * @author qw
 */
@Clear
public class RestSecurityInterceptor implements Interceptor {

    private static final String ADMIN_FLAG = "67B14728AD9902AECBA32E22FA4F6BD";
    protected final Log log = Log.getLog(getClass());

    @Override
    public void intercept(Invocation ai) {

        // 获取请求内容
        RestController c = (RestController) ai.getController();
        HttpServletRequest request = c.getRequest();
        // 获取请求头信息
        String unique_id = request.getHeader("unique_id");
        String access_token = request.getHeader("token");
        String user_id = request.getHeader("user_id");

        // 获取parameter信息
        if (StrKit.isBlank(unique_id)) {
            unique_id = request.getParameter("unique_id");
        }

        if (StrKit.isBlank(access_token)) {
            access_token = request.getParameter("token");
        }
        if (StrKit.isBlank(user_id)) {
            user_id = request.getParameter("user_id");
        }

        // 获取驼峰格式的信息
        if (StrKit.isBlank(user_id)) {
            user_id = request.getHeader("userId");
            if (StrKit.isBlank(user_id)) {
                user_id = request.getParameter("userId");
            }
        }
        // 矫正非法userId
        if (StrKit.isBlank(user_id)) {
            user_id = "0";
        }
        if ("null".equals(user_id)) {
            user_id = "0";
        }

        // 检测session
        if (StrKit.isBlank(unique_id)) {
            c.renderJson(new ApiResult(ApiStatus.signNull, "验证失败，非法访问。"));
        }

        // 获取后台的唯一标识符
        String admin = request.getHeader("platinum_butler");
        if (StrKit.isBlank(admin)) {
            admin = request.getParameter("platinum_butler");
        }

        if (ADMIN_FLAG.equals(admin)) {
            if (validAdmin(access_token, user_id)) {
                try {
                    ai.invoke();
                } finally {
                    AppIdKit.removeThreadLocal();
                }
            } else {
                c.renderJson(new ApiResult(ApiStatus.signError, "验证失败，非法访问。"));
            }
        } else {
            if (validClient(access_token, user_id)) {
                try {
                    ai.invoke();
                } finally {
                    AppIdKit.removeThreadLocal();
                }
            } else {
                c.renderJson(new ApiResult(ApiStatus.signError, "验证失败，非法访问。"));
            }
        }
    }

    /**
     * 校验后台登录状态
     */
    private boolean validAdmin(String accessToken, String userId) {


        if (StrKit.isBlank(userId)) {
            return false;
        }
        Integer userIdInt = 0;
        try {
            userIdInt = Integer.valueOf(userId);
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
        return StaffService.me().validToken(accessToken, userIdInt);
    }

    /**
     * 校验 客户端 token
     */
    private boolean validClient(String accessToken, String userId) {
        Integer userIdInt = 0;
        try {
            userIdInt = Integer.valueOf(userId);
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
        return UserService.me().validToken(accessToken, userIdInt);
    }
}