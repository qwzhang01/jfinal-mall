package com.qw.event.user;

import java.io.Serializable;


/**
 * 注册成功事件
 */
public class SignUpEvent implements Serializable {
    private Integer userId;

    public SignUpEvent(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
