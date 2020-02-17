package cn.qw.shiro;

import java.util.concurrent.ConcurrentMap;

/**
 * ShiroKit. (Singleton, ThreadSafe)
 */
public class ShiroKit {

    /**
     * 用来记录那个action或者actionpath中是否有shiro认证注解。
     */
    private static ConcurrentMap<String, AuthzHandler> authzMaps = null;

    /**
     * 禁止初始化
     */
    private ShiroKit() {
    }

    public static void init(ConcurrentMap<String, AuthzHandler> maps) {
        authzMaps = maps;
    }

    public static AuthzHandler getAuthzHandler(String actionKey) {
        return authzMaps.get(actionKey);
    }
}
