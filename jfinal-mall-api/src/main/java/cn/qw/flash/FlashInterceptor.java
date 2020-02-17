package cn.qw.flash;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 在前后端不分离的项目中，对于redirect的方式，传递信息
 */
public class FlashInterceptor implements Interceptor {

    /**
     * 该拦截器取得当前ActionPath，从Cache中检查是否有传送给当前Action的Flash对象Map
     * 若有，则遍历Map，并将所有key，value注入到当前的request请求中。
     */
    @Override
    public void intercept(Invocation ai) {
        Controller c = ai.getController();
        HttpSession session = c.getSession(false);
        if (null != session) {
            String curAction = ai.getViewPath();
            String method = ai.getMethodName();
            if (!"index".equals(method)) {
                curAction += method;
            } else if (!"/".equals(curAction)) {
                curAction = curAction.substring(0, curAction.length() - 1);
            }
            Map<String, Object> flashMap = FlashManager.me().getFlash(session,
                    curAction);
            if (flashMap != null) {
                for (Entry<String, Object> flashEntry : flashMap.entrySet()) {
                    c.setAttr(flashEntry.getKey(), flashEntry.getValue());
                }
            }
        }
        ai.invoke();
    }
}