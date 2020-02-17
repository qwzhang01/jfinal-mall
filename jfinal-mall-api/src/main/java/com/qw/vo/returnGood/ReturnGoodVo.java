package com.qw.vo.returnGood;

import java.io.Serializable;
import java.util.List;

/**
 * 退款商品VO
 *
 * @author qw
 */
public class ReturnGoodVo implements Serializable {
    /**
     * 订单id
     */
    private Integer orderId;
    /**
     * 退货换货
     */
    private List<OrderReturnGoodVo> list;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<OrderReturnGoodVo> getList() {
        return list;
    }

    public void setList(List<OrderReturnGoodVo> list) {
        this.list = list;
    }
}
