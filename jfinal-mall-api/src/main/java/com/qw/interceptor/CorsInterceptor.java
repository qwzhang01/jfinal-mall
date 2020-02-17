package com.qw.interceptor;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.JsonKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import org.apache.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CorsInterceptor implements Interceptor {
    protected final Log log = Log.getLog(getClass());

    @Override
    public void intercept(Invocation ai) {

        // 处理跨域
        RestController c = (RestController) ai.getController();
        if (PropKit.getBoolean("cross_domain", false)) {

            cors(c.getRequest(), c.getResponse());

            if (c.getRequest().getMethod().equals("OPTIONS")) {
                c.renderSuccess("OPTION SUCCESS");
                return;
            }
        }
        HttpServletRequest request = c.getRequest();
        log.info("---------请求地址：" + request.getServletPath() + "-------请求入参:" + JsonKit.toJson(request.getParameterMap()));
        String user_id = request.getHeader("user_id");
        if (StrKit.isBlank(user_id)) {
            user_id = request.getParameter("user_id");
        }
        if (StrKit.isBlank(user_id)) {
            user_id = request.getHeader("userId");
            if (StrKit.isBlank(user_id)) {
                user_id = request.getParameter("userId");
            }
        }
        if ("null".equals(user_id)) {
            user_id = "0";
        }
        if (StrKit.notBlank(user_id)) {
            AppIdKit.setUserId(Integer.valueOf(user_id));
        }
        ai.invoke();
    }

    /**
     * 处理跨域
     */
    private void cors(HttpServletRequest req, HttpServletResponse res) {
        String header = req.getHeader(HttpHeaders.REFERER);
        if (StrKit.isBlank(header)) {
            header = req.getHeader("Origin");
        }
        if (StrKit.isBlank(header)) {
            header = "*";
        }

        if (header.endsWith("/")) {
            header = header.substring(0, header.length() - 1);
        }

        String remoteHost = req.getRemoteHost();
        log.info("[请求HOST] host=  " + remoteHost);

        res.setHeader("Access-Control-Expose-Headers", "*");
        // 允许跨域的域名
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Vary", "Origin");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        // 允许跨域的方法
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        // 允许跨域时，在header中携带的参数名
        res.setHeader("Access-Control-Allow-Headers", "*");
    }
}
