package cn.qw.entity;

import java.io.Serializable;

/**
 * Api返回的结果对象
 */
public class ApiResult implements Serializable {

    private static final long serialVersionUID = 1840042328547867221L;

    /**
     * 返回的结果数据
     */
    private Object data;
    /**
     * 0为正常，小于0为验证失败
     */
    private int status;
    /**
     * 身份验证错误时，返回错误原因
     */
    private String msg;

    public ApiResult() {
    }

    public ApiResult(Object data, int status, String msg) {
        this.data = data;
        this.status = status;
        this.msg = msg;
    }

    public ApiResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
