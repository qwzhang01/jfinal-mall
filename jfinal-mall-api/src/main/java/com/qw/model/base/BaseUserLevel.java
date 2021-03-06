package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserLevel<M extends BaseUserLevel<M>> extends BaseModel<M> implements IBean {

	public void setLevelId(java.lang.Integer levelId) {
		set("level_id", levelId);
	}

	public java.lang.Integer getLevelId() {
		return getInt("level_id");
	}

	/**
	 * 头衔名称
	 */
	public void setLevelName(java.lang.String levelName) {
		set("level_name", levelName);
	}

	/**
	 * 头衔名称
	 */
	public java.lang.String getLevelName() {
		return getStr("level_name");
	}

	/**
	 * 等级必要金额
	 */
	public void setAmount(java.math.BigDecimal amount) {
		set("amount", amount);
	}

	/**
	 * 等级必要金额
	 */
	public java.math.BigDecimal getAmount() {
		return get("amount");
	}

	/**
	 * 折扣
	 */
	public void setDiscount(java.lang.Integer discount) {
		set("discount", discount);
	}

	/**
	 * 折扣
	 */
	public java.lang.Integer getDiscount() {
		return getInt("discount");
	}

	public void setDescribe(java.lang.String describe) {
		set("describe", describe);
	}

	public java.lang.String getDescribe() {
		return getStr("describe");
	}

}
