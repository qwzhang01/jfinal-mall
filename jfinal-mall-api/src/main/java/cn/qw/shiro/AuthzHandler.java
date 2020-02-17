package cn.qw.shiro;

import org.apache.shiro.authz.AuthorizationException;

/**
 * 访问控制处理器接口
 */
public interface AuthzHandler {
    /**
     * 访问控制检查
     *
     * @throws AuthorizationException 授权异常
     */
    void assertAuthorized() throws AuthorizationException;
}
