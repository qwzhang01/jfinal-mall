package cn.qw.entity;

public class ApiStatus {

    /**
     * 调用成功
     */
    public static int success = 0;

    /**
     * 操作失败，不符合条件
     */
    public static int operateError = 501;

    /**
     * 表示http请求方式错误，请求方式应该为POST请求
     */
    public static int mustPost = 4001;

    /**
     * 校验数据为空
     */
    public static int signNull = 4002;

    /**
     * 身份校验错误
     */
    public static int signError = -101;

    /**
     * 必填参数为空
     */
    public static int paramNull = 4004;

    /**
     * 参数错误
     */
    public static int paramError = 4005;

    /**
     * 返回数据为空
     */
    public static int dataNull = 5000;
}
