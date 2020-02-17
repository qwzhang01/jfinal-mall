package com.qw.vo.good;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author qw
 */
public class GoodSkuVo implements Serializable {

    /**
     * SKU 对应规格编码
     * spc ID 按照升序排序后，对应的spcItemId 组合，使用过下划线链接
     */
    private String skuComputerCode;
    /**
     * SKU 编码 即库存编码
     */
    private String skuCode;
    private BigDecimal shopPrice;
    private BigDecimal marketPrice;
    private BigDecimal supplyPrice;
    private Long storeCount;
    private String saveImgPath;
    private String showImgPath;
    private Boolean disAbled;

    public String getSkuComputerCode() {
        return skuComputerCode;
    }

    public void setSkuComputerCode(String skuComputerCode) {
        this.skuComputerCode = skuComputerCode;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public BigDecimal getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(BigDecimal shopPrice) {
        this.shopPrice = shopPrice;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public BigDecimal getSupplyPrice() {
        return supplyPrice;
    }

    public void setSupplyPrice(BigDecimal supplyPrice) {
        this.supplyPrice = supplyPrice;
    }

    public Long getStoreCount() {
        return storeCount;
    }

    public void setStoreCount(Long storeCount) {
        this.storeCount = storeCount;
    }

    public String getSaveImgPath() {
        return saveImgPath;
    }

    public void setSaveImgPath(String saveImgPath) {
        this.saveImgPath = saveImgPath;
    }

    public String getShowImgPath() {
        return showImgPath;
    }

    public void setShowImgPath(String showImgPath) {
        this.showImgPath = showImgPath;
    }

    public Boolean getDisAbled() {
        return disAbled;
    }

    public void setDisAbled(Boolean disAbled) {
        this.disAbled = disAbled;
    }
}