package com.qw.vo.order;

import java.io.Serializable;
import java.util.List;

/**
 * 提交保单与前端对应的实体
 */
public class SubOrderVo implements Serializable {
    private Integer addressId;
    /**
     * 是否是积分购买
     * 1是2否
     */
    private Integer isPoint;
    /**
     * 是否是购物车
     * 1是2否
     */
    private Integer isCart;
    private List<OrderStore> orderStoreList;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getIsPoint() {
        return isPoint;
    }

    public void setIsPoint(Integer isPoint) {
        this.isPoint = isPoint;
    }

    public void setIsCart(Integer isCart) {
        this.isCart = isCart;
    }

    public Integer getIsCart() {
        return isCart;
    }

    public List<OrderStore> getOrderStoreList() {
        return orderStoreList;
    }

    public void setOrderStoreList(List<OrderStore> orderStoreList) {
        this.orderStoreList = orderStoreList;
    }

    public static class OrderStore implements Serializable {
        private Integer delivery;
        private String userNote;
        private List<OrderGood> orderGoodList;

        public Integer getDelivery() {
            return delivery;
        }

        public void setDelivery(Integer delivery) {
            this.delivery = delivery;
        }

        public String getUserNote() {
            return userNote;
        }

        public void setUserNote(String userNote) {
            this.userNote = userNote;
        }

        public List<OrderGood> getOrderGoodList() {
            return orderGoodList;
        }

        public void setOrderGoodList(List<OrderGood> orderGoodList) {
            this.orderGoodList = orderGoodList;
        }
    }

    public static class OrderGood implements Serializable {
        private Integer skuId;
        private Integer buyNum;

        public Integer getSkuId() {
            return skuId;
        }

        public void setSkuId(Integer skuId) {
            this.skuId = skuId;
        }

        public Integer getBuyNum() {
            return buyNum;
        }

        public void setBuyNum(Integer buyNum) {
            this.buyNum = buyNum;
        }
    }
}