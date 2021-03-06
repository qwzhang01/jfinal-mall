package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCouponOrderRecord<M extends BaseCouponOrderRecord<M>> extends BaseModel<M> implements IBean {

	/**
	 * 自增长ID
	 */
	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	/**
	 * 自增长ID
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 优惠券ID
	 */
	public void setCouponListId(java.lang.Integer couponListId) {
		set("coupon_list_id", couponListId);
	}

	/**
	 * 优惠券ID
	 */
	public java.lang.Integer getCouponListId() {
		return getInt("coupon_list_id");
	}

	/**
	 * 优惠券消费金额 (本次)
	 */
	public void setConsumeMoney(java.lang.Integer consumeMoney) {
		set("consume_money", consumeMoney);
	}

	/**
	 * 优惠券消费金额 (本次)
	 */
	public java.lang.Integer getConsumeMoney() {
		return getInt("consume_money");
	}

	/**
	 * 代金券剩余面额 表 (本次)
	 */
	public void setResidueMoney(java.lang.Integer residueMoney) {
		set("residue_money", residueMoney);
	}

	/**
	 * 代金券剩余面额 表 (本次)
	 */
	public java.lang.Integer getResidueMoney() {
		return getInt("residue_money");
	}

	/**
	 * 创建时间
	 */
	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	/**
	 * 创建时间
	 */
	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	/**
	 * 修改时间
	 */
	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	/**
	 * 修改时间
	 */
	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

}
