package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseOrderGoods<M extends BaseOrderGoods<M>> extends BaseModel<M> implements IBean {

	/**
	 * 表id自增
	 */
	public void setRecId(java.lang.Integer recId) {
		set("rec_id", recId);
	}

	/**
	 * 表id自增
	 */
	public java.lang.Integer getRecId() {
		return getInt("rec_id");
	}

	/**
	 * 订单id
	 */
	public void setOrderId(java.lang.Integer orderId) {
		set("order_id", orderId);
	}

	/**
	 * 订单id
	 */
	public java.lang.Integer getOrderId() {
		return getInt("order_id");
	}

	/**
	 * 订单编号（没有定订单ID的时候做外健用，redis中用）
	 */
	public void setOrderSn(java.lang.String orderSn) {
		set("order_sn", orderSn);
	}

	/**
	 * 订单编号（没有定订单ID的时候做外健用，redis中用）
	 */
	public java.lang.String getOrderSn() {
		return getStr("order_sn");
	}

	/**
	 * 商品id
	 */
	public void setGoodsId(java.lang.Long goodsId) {
		set("goods_id", goodsId);
	}

	/**
	 * 商品id
	 */
	public java.lang.Long getGoodsId() {
		return getLong("goods_id");
	}

	/**
	 * SKU-ID
	 */
	public void setSkuId(java.lang.Integer skuId) {
		set("skuId", skuId);
	}

	/**
	 * SKU-ID
	 */
	public java.lang.Integer getSkuId() {
		return getInt("skuId");
	}

	/**
	 * 商品名称
	 */
	public void setGoodsName(java.lang.String goodsName) {
		set("goods_name", goodsName);
	}

	/**
	 * 商品名称
	 */
	public java.lang.String getGoodsName() {
		return getStr("goods_name");
	}

	/**
	 * 商品货号
	 */
	public void setGoodsSn(java.lang.String goodsSn) {
		set("goods_sn", goodsSn);
	}

	/**
	 * 商品货号
	 */
	public java.lang.String getGoodsSn() {
		return getStr("goods_sn");
	}

	/**
	 * 购买数量
	 */
	public void setGoodsNum(java.lang.Integer goodsNum) {
		set("goods_num", goodsNum);
	}

	/**
	 * 购买数量
	 */
	public java.lang.Integer getGoodsNum() {
		return getInt("goods_num");
	}

	/**
	 * 商品规格key
	 */
	public void setSpecKey(java.lang.String specKey) {
		set("spec_key", specKey);
	}

	/**
	 * 商品规格key
	 */
	public java.lang.String getSpecKey() {
		return getStr("spec_key");
	}

	/**
	 * 规格对应的中文名字
	 */
	public void setSpecKeyName(java.lang.String specKeyName) {
		set("spec_key_name", specKeyName);
	}

	/**
	 * 规格对应的中文名字
	 */
	public java.lang.String getSpecKeyName() {
		return getStr("spec_key_name");
	}

	/**
	 * 本店销售价格
	 */
	public void setGoodsPrice(java.math.BigDecimal goodsPrice) {
		set("goods_price", goodsPrice);
	}

	/**
	 * 本店销售价格
	 */
	public java.math.BigDecimal getGoodsPrice() {
		return get("goods_price");
	}

	/**
	 * 商品成本价
	 */
	public void setCostPrice(java.math.BigDecimal costPrice) {
		set("cost_price", costPrice);
	}

	/**
	 * 商品成本价
	 */
	public java.math.BigDecimal getCostPrice() {
		return get("cost_price");
	}

	/**
	 * 供应价格
	 */
	public void setSupplyPrice(java.math.BigDecimal supplyPrice) {
		set("supply_price", supplyPrice);
	}

	/**
	 * 供应价格
	 */
	public java.math.BigDecimal getSupplyPrice() {
		return get("supply_price");
	}

	/**
	 * 商品实际购买价格（不包括积分抵扣的金额），但是包含商品个数
	 */
	public void setFinalPrice(java.math.BigDecimal finalPrice) {
		set("final_price", finalPrice);
	}

	/**
	 * 商品实际购买价格（不包括积分抵扣的金额），但是包含商品个数
	 */
	public java.math.BigDecimal getFinalPrice() {
		return get("final_price");
	}

	/**
	 * 积分抵扣金额
	 */
	public void setPointAsMoney(java.math.BigDecimal pointAsMoney) {
		set("point_as_money", pointAsMoney);
	}

	/**
	 * 积分抵扣金额
	 */
	public java.math.BigDecimal getPointAsMoney() {
		return get("point_as_money");
	}

	/**
	 * 购买商品赠送积分
	 */
	public void setGiveIntegral(java.lang.Integer giveIntegral) {
		set("give_integral", giveIntegral);
	}

	/**
	 * 购买商品赠送积分
	 */
	public java.lang.Integer getGiveIntegral() {
		return getInt("give_integral");
	}

	/**
	 * 活动ID(秒杀ID、一元抢购ID)
	 */
	public void setActivityId(java.lang.Integer activityId) {
		set("activity_id", activityId);
	}

	/**
	 * 活动ID(秒杀ID、一元抢购ID)
	 */
	public java.lang.Integer getActivityId() {
		return getInt("activity_id");
	}

	/**
	 * 活动类型：1 普通订单、2 秒杀活动订单、3 一元抢购抽奖订单
	 */
	public void setActivityType(java.lang.Integer activityType) {
		set("activity_type", activityType);
	}

	/**
	 * 活动类型：1 普通订单、2 秒杀活动订单、3 一元抢购抽奖订单
	 */
	public java.lang.Integer getActivityType() {
		return getInt("activity_type");
	}

	/**
	 * 是否需要上门服务（1.无需上门服务;3.需要上门服务）
	 */
	public void setDoorServiceStatus(java.lang.Integer doorServiceStatus) {
		set("door_service_status", doorServiceStatus);
	}

	/**
	 * 是否需要上门服务（1.无需上门服务;3.需要上门服务）
	 */
	public java.lang.Integer getDoorServiceStatus() {
		return getInt("door_service_status");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setCreatedBy(java.lang.String createdBy) {
		set("created_by", createdBy);
	}

	public java.lang.String getCreatedBy() {
		return getStr("created_by");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setUpdateBy(java.lang.String updateBy) {
		set("update_by", updateBy);
	}

	public java.lang.String getUpdateBy() {
		return getStr("update_by");
	}

}
