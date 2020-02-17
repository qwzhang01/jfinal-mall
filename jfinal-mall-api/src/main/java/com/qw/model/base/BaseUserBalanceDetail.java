package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserBalanceDetail<M extends BaseUserBalanceDetail<M>> extends BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 用户ID
	 */
	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	/**
	 * 用户ID
	 */
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8 推广首单奖励
	 */
	public void setAmountType(java.lang.Integer amountType) {
		set("amount_type", amountType);
	}

	/**
	 * 1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8 推广首单奖励
	 */
	public java.lang.Integer getAmountType() {
		return getInt("amount_type");
	}

	/**
	 * 关联对应业务的ID（1-orderId2-orderId3-orderId4-充值id 5 orderId 6提现id7-orderId）
	 */
	public void setObjectId(java.lang.Integer objectId) {
		set("object_id", objectId);
	}

	/**
	 * 关联对应业务的ID（1-orderId2-orderId3-orderId4-充值id 5 orderId 6提现id7-orderId）
	 */
	public java.lang.Integer getObjectId() {
		return getInt("object_id");
	}

	/**
	 * 1收入 2支出
	 */
	public void setInOutFlag(java.lang.Integer inOutFlag) {
		set("in_out_flag", inOutFlag);
	}

	/**
	 * 1收入 2支出
	 */
	public java.lang.Integer getInOutFlag() {
		return getInt("in_out_flag");
	}

	/**
	 * 订单支付但是没有结束的佣金1冻结 2正常，3取消
	 */
	public void setFrozenFlag(java.lang.Integer frozenFlag) {
		set("frozen_flag", frozenFlag);
	}

	/**
	 * 订单支付但是没有结束的佣金1冻结 2正常，3取消
	 */
	public java.lang.Integer getFrozenFlag() {
		return getInt("frozen_flag");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

}
