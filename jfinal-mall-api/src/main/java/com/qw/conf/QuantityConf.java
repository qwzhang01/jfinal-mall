package com.qw.conf;

/**
 * 缓存器
 *
 * @author qw
 */
public class QuantityConf {
    /**
     * 短信验证码缓存器
     */
    public static final String MOBILE_CODE = "MobileCode";
    /**
     * 登录信息缓存器（Token、UserId、权限信息）、微信登录的token、jsTicket等信息
     */
    public static final String TOKEN_REDIS = "TokenRedis";
    /**
     * 登录错误信息缓存器（账号密码错误5次以上，15分钟内不能登录）
     */
    public static final String LOGIN_ERROR = "LoginError";
    /**
     * 系统参数缓存器
     */
    public static final String PARAM_CATCHE = "ParamCatche";
    /**
     * 消息队列缓存器
     */
    public static final String BUTLER_QUENE = "ButlerQuene";
    /**
     * 秒杀
     */
    public static final String FLASH_SALE = "FlashSale";
}