package com.qw.vo.order;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderCommentVo implements Serializable {
    private Integer goodId;
    private BigDecimal descScore;
    private String comment;
    private String[] imgarr;
    private Integer anonymous;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public BigDecimal getDescScore() {
        return descScore;
    }

    public void setDescScore(BigDecimal descScore) {
        this.descScore = descScore;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String[] getImgarr() {
        return imgarr;
    }

    public void setImgarr(String[] imgarr) {
        this.imgarr = imgarr;
    }

    public Integer getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Integer anonymous) {
        this.anonymous = anonymous;
    }
}
