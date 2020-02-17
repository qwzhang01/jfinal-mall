package com.qw.event.message;

import java.io.Serializable;

public class SmsEvent implements Serializable {
    private static final long serialVersionUID = 5996561485210365132L;

    private String msg;
    private String result;
    private String mobile;
    private String code;
    private boolean isOk;

    public SmsEvent(boolean isOk, String code, String mobile, String result, String msg) {
        this.code = code;
        this.mobile = mobile;
        this.result = result;
        this.msg = msg;
        this.isOk = isOk;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
