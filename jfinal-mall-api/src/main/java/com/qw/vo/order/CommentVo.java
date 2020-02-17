package com.qw.vo.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CommentVo implements Serializable {

    private static final long serialVersionUID = -1215503014785975784L;

    private Integer orderId;
    // 服务评价
    private BigDecimal sellerScore;
    // 物流评价
    private BigDecimal logisticsScore;
    // 商品评价
    private List<OrderCommentVo> list;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getSellerScore() {
        return sellerScore;
    }

    public void setSellerScore(BigDecimal sellerScore) {
        this.sellerScore = sellerScore;
    }

    public BigDecimal getLogisticsScore() {
        return logisticsScore;
    }

    public void setLogisticsScore(BigDecimal logisticsScore) {
        this.logisticsScore = logisticsScore;
    }


    public List<OrderCommentVo> getList() {
        return list;
    }

    public void setList(List<OrderCommentVo> list) {
        this.list = list;
    }
}