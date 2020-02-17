package com.qw.vo.good;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author qw
 */
public class GoodFormVo implements Serializable {

    /********** 基本信息 begin ************/
    private Long goodsId;
    private Integer storeId;
    private Integer[] catId;
    private Integer catId3;
    private String goodsSn;
    private Long brandId;
    private String goodsName;
    private String goodsRemark;
    private String keywords;
    /**
     * 商品主图
     */
    private String originalImg;
    /**
     * 商品主图
     */
    private String originalImgShow;

    /**
     * 商品画廊
     */
    private String[] imgAlbum;

    /**
     * 是否需要上门服务（1.无需上门服务;2.可选上门服务;3.必须上门服务）
     */
    private Integer doorServiceStatus;
    /********** 基本信息 end ************/

    /********** 促销信息 begin ***********/
    /**
     * 0下架1上架2违规下架
     */
    private Integer isOnSale;
    private Date onSaleTime;

    private Integer isRecommend;
    private Integer isHot;
    /**
     * 是否包邮0否1是
     */
    private Integer isFreeShipping;
    private BigDecimal pointAsMoney;
    private Integer isEarnPoint;
    private Integer giveIntegral;
    private Integer sort;

    /********** 促销信息 end ***********/

    /**
     * 商品SKU
     */
    private List<GoodSkuVo> skuData;

    /**
     * 库存预警值
     */
    private Integer storeCountWorn;

    private String[] detailShowImgList;
    private String[] detailSaveImgList;

    public String[] getDetailShowImgList() {
        return detailShowImgList;
    }

    public void setDetailShowImgList(String[] detailShowImgList) {
        this.detailShowImgList = detailShowImgList;
    }

    public String[] getDetailSaveImgList() {
        return detailSaveImgList;
    }

    public void setDetailSaveImgList(String[] detailSaveImgList) {
        this.detailSaveImgList = detailSaveImgList;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public Integer[] getCatId() {
        return catId;
    }

    public void setCatId(Integer[] catId) {
        this.catId = catId;
    }

    public Integer getCatId3() {
        return catId3;
    }

    public void setCatId3(Integer catId3) {
        this.catId3 = catId3;
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsRemark() {
        return goodsRemark;
    }

    public void setGoodsRemark(String goodsRemark) {
        this.goodsRemark = goodsRemark;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getOriginalImg() {
        return originalImg;
    }

    public void setOriginalImg(String originalImg) {
        this.originalImg = originalImg;
    }

    public String getOriginalImgShow() {
        return originalImgShow;
    }

    public void setOriginalImgShow(String originalImgShow) {
        this.originalImgShow = originalImgShow;
    }

    public Integer getIsRecommend() {
        return isRecommend;
    }

    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
    }

    public Integer getDoorServiceStatus() {
        return doorServiceStatus;
    }

    public void setDoorServiceStatus(Integer doorServiceStatus) {
        this.doorServiceStatus = doorServiceStatus;
    }

    public Integer getIsOnSale() {
        return isOnSale;
    }

    public void setIsOnSale(Integer isOnSale) {
        this.isOnSale = isOnSale;
    }

    public Date getOnSaleTime() {
        return onSaleTime;
    }

    public void setOnSaleTime(Date onSaleTime) {
        this.onSaleTime = onSaleTime;
    }

    public Integer getIsHot() {
        return isHot;
    }

    public void setIsHot(Integer isHot) {
        this.isHot = isHot;
    }

    public Integer getIsFreeShipping() {
        return isFreeShipping;
    }

    public void setIsFreeShipping(Integer isFreeShipping) {
        this.isFreeShipping = isFreeShipping;
    }

    public BigDecimal getPointAsMoney() {
        return pointAsMoney;
    }

    public void setPointAsMoney(BigDecimal pointAsMoney) {
        this.pointAsMoney = pointAsMoney;
    }

    public Integer getIsEarnPoint() {
        return isEarnPoint;
    }

    public void setIsEarnPoint(Integer isEarnPoint) {
        this.isEarnPoint = isEarnPoint;
    }

    public List<GoodSkuVo> getSkuData() {
        return skuData;
    }

    public void setSkuData(List<GoodSkuVo> skuData) {
        this.skuData = skuData;
    }

    public Integer getStoreCountWorn() {
        return storeCountWorn;
    }

    public void setStoreCountWorn(Integer storeCountWorn) {
        this.storeCountWorn = storeCountWorn;
    }

    public String[] getImgAlbum() {
        return imgAlbum;
    }

    public void setImgAlbum(String[] imgAlbum) {
        this.imgAlbum = imgAlbum;
    }

    public Integer getGiveIntegral() {
        return this.giveIntegral;
    }

    public void setGiveIntegral(Integer giveIntegral) {
        this.giveIntegral = giveIntegral;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}