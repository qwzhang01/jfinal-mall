package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGoodSku<M extends BaseGoodSku<M>> extends BaseModel<M> implements IBean {

	/**
	 * 规格商品id
	 */
	public void setItemId(java.math.BigInteger itemId) {
		set("item_id", itemId);
	}

	/**
	 * 规格商品id
	 */
	public java.math.BigInteger getItemId() {
		return get("item_id");
	}

	/**
	 * 商品id
	 */
	public void setGoodsId(java.lang.Integer goodsId) {
		set("goods_id", goodsId);
	}

	/**
	 * 商品id
	 */
	public java.lang.Integer getGoodsId() {
		return getInt("goods_id");
	}

	/**
	 * 商品规格备注
	 */
	public void setGoodsMark(java.lang.String goodsMark) {
		set("goods_mark", goodsMark);
	}

	/**
	 * 商品规格备注
	 */
	public java.lang.String getGoodsMark() {
		return getStr("goods_mark");
	}

	/**
	 * SKU编码
	 */
	public void setSku(java.lang.String sku) {
		set("sku", sku);
	}

	/**
	 * SKU编码
	 */
	public java.lang.String getSku() {
		return getStr("sku");
	}

	/**
	 * 规格商品主图
	 */
	public void setSpecImg(java.lang.String specImg) {
		set("spec_img", specImg);
	}

	/**
	 * 规格商品主图
	 */
	public java.lang.String getSpecImg() {
		return getStr("spec_img");
	}

	/**
	 * 规格键名
	 */
	public void setKey(java.lang.String key) {
		set("key", key);
	}

	/**
	 * 规格键名
	 */
	public java.lang.String getKey() {
		return getStr("key");
	}

	/**
	 * 规格键名中文
	 */
	public void setKeyName(java.lang.String keyName) {
		set("key_name", keyName);
	}

	/**
	 * 规格键名中文
	 */
	public java.lang.String getKeyName() {
		return getStr("key_name");
	}

	/**
	 * 销售价格
	 */
	public void setPrice(java.math.BigDecimal price) {
		set("price", price);
	}

	/**
	 * 销售价格
	 */
	public java.math.BigDecimal getPrice() {
		return get("price");
	}

	/**
	 * 供货商供应价格
	 */
	public void setSupplyPrice(java.math.BigDecimal supplyPrice) {
		set("supply_price", supplyPrice);
	}

	/**
	 * 供货商供应价格
	 */
	public java.math.BigDecimal getSupplyPrice() {
		return get("supply_price");
	}

	/**
	 * 市场价格
	 */
	public void setMarketPrice(java.math.BigDecimal marketPrice) {
		set("market_price", marketPrice);
	}

	/**
	 * 市场价格
	 */
	public java.math.BigDecimal getMarketPrice() {
		return get("market_price");
	}

	/**
	 * 库存数量
	 */
	public void setStoreCount(java.lang.Integer storeCount) {
		set("store_count", storeCount);
	}

	/**
	 * 库存数量
	 */
	public java.lang.Integer getStoreCount() {
		return getInt("store_count");
	}

	/**
	 * 是否可用（0否1是）
	 */
	public void setDisAbled(java.lang.Boolean disAbled) {
		set("disAbled", disAbled);
	}

	/**
	 * 是否可用（0否1是）
	 */
	public java.lang.Boolean getDisAbled() {
		return get("disAbled");
	}

}
