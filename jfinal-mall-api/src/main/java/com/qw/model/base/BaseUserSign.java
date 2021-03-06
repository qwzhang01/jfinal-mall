package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUserSign<M extends BaseUserSign<M>> extends BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 用户id
	 */
	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	/**
	 * 用户id
	 */
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	/**
	 * 累计签到天数
	 */
	public void setSignTotal(java.lang.Integer signTotal) {
		set("sign_total", signTotal);
	}

	/**
	 * 累计签到天数
	 */
	public java.lang.Integer getSignTotal() {
		return getInt("sign_total");
	}

	/**
	 * 连续签到天数
	 */
	public void setContinueSignCount(java.lang.Integer continueSignCount) {
		set("continue_sign_count", continueSignCount);
	}

	/**
	 * 连续签到天数
	 */
	public java.lang.Integer getContinueSignCount() {
		return getInt("continue_sign_count");
	}

	/**
	 * 签到时间，时间格式20170907
	 */
	public void setDateMonth(java.lang.String dateMonth) {
		set("date_month", dateMonth);
	}

	/**
	 * 签到时间，时间格式20170907
	 */
	public java.lang.String getDateMonth() {
		return getStr("date_month");
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
