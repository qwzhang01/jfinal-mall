package com.qw.vo.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单详情
 *
 * @author qw
 */
public class OrderDetailVo implements Serializable {
    /**
     * 总商品数
     */
    private Integer totalNum;
    /**
     * 总价格
     */
    private BigDecimal totalPrice;
    /**
     * 积分抵扣金额
     */
    private BigDecimal totalPointAsMoney;
    private List<OrderStore> orderStoreList;
    /**
     * 收货人信息
     */
    private Consignee consignee;
    /**
     * 抽奖人信息
     */
    private LotteryUser lotteryUser;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPointAsMoney() {
        return totalPointAsMoney;
    }

    public void setTotalPointAsMoney(BigDecimal totalPointAsMoney) {
        this.totalPointAsMoney = totalPointAsMoney;
    }

    public List<OrderStore> getOrderStoreList() {
        return orderStoreList;
    }

    public void setOrderStoreList(List<OrderStore> orderStoreList) {
        this.orderStoreList = orderStoreList;
    }

    public Consignee getConsignee() {
        return consignee;
    }

    public void setConsignee(Consignee consignee) {
        this.consignee = consignee;
    }

    public LotteryUser getLotteryUser() {
        return lotteryUser;
    }
    public void setLotteryUser(LotteryUser lotteryUser) {
        this.lotteryUser = lotteryUser;
    }

    /**
     * 店铺列表
     */
    public class OrderStore implements Serializable {
        private Integer storeId;
        private String storeName;
        private Integer sumNum;
        private BigDecimal sumPrice;
        private String servicePhone;
        private List<Express> expressList;
        private List<OrderGood> orderGood;

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public Integer getSumNum() {
            return sumNum;
        }

        public void setSumNum(Integer sumNum) {
            this.sumNum = sumNum;
        }

        public BigDecimal getSumPrice() {
            return sumPrice;
        }

        public void setSumPrice(BigDecimal sumPrice) {
            this.sumPrice = sumPrice;
        }

        public String getServicePhone() {
            return servicePhone;
        }

        public void setServicePhone(String servicePhone) {
            this.servicePhone = servicePhone;
        }

        public List<OrderGood> getOrderGood() {
            return orderGood;
        }

        public void setOrderGood(List<OrderGood> orderGood) {
            this.orderGood = orderGood;
        }

        public List<Express> getExpressList() {
            return expressList;
        }

        public void setExpressList(List<Express> expressList) {
            this.expressList = expressList;
        }
    }

    /**
     * 快递方式
     */
    public class Express implements Serializable {
        private Integer id;
        private String name;
        private BigDecimal price;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    /**
     * 商品信息
     */
    public class OrderGood implements Serializable {
        private Long goodId;
        private Integer skuId;
        private String goodName;
        private Integer goodNum;
        private BigDecimal goodPrice;
        private String imgPath;
        private String specName;

        public Integer getSkuId() {
            return skuId;
        }

        public void setSkuId(Integer skuId) {
            this.skuId = skuId;
        }

        public Long getGoodId() {
            return goodId;
        }

        public void setGoodId(Long goodId) {
            this.goodId = goodId;
        }

        public String getGoodName() {
            return goodName;
        }

        public void setGoodName(String goodName) {
            this.goodName = goodName;
        }

        public Integer getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(Integer goodNum) {
            this.goodNum = goodNum;
        }

        public BigDecimal getGoodPrice() {
            return goodPrice;
        }

        public void setGoodPrice(BigDecimal goodPrice) {
            this.goodPrice = goodPrice;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getSpecName() {
            return specName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }
    }

    /**
     * 收货人
     */
    public class Consignee implements Serializable {
        private Integer addressId;
        private String name;
        private String mobile;
        private String address;

        public Integer getAddressId() {
            return addressId;
        }

        public void setAddressId(Integer addressId) {
            this.addressId = addressId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public class LotteryUser implements Serializable{
        private Integer absentNum = 0;
        private List<String> headPic;
        public Integer getAbsentNum() {
            return absentNum;
        }
        public void setAbsentNum(Integer absentNum) {
            this.absentNum = absentNum;
        }

        public List<String> getHeadPic() {
            return headPic;
        }

        public void setHeadPic(List<String> headPic) {
            this.headPic = headPic;
        }
    }
}