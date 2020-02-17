package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGoodVisit<M extends BaseGoodVisit<M>> extends BaseModel<M> implements IBean {

	/**
	 * 主键
	 */
	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	/**
	 * 主键
	 */
	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 会员ID
	 */
	public void setUserId(java.lang.Integer userId) {
		set("user_id", userId);
	}

	/**
	 * 会员ID
	 */
	public java.lang.Integer getUserId() {
		return getInt("user_id");
	}

	/**
	 * 商品ID
	 */
	public void setGoodsId(java.lang.Integer goodsId) {
		set("goods_id", goodsId);
	}

	/**
	 * 商品ID
	 */
	public java.lang.Integer getGoodsId() {
		return getInt("goods_id");
	}

	/**
	 * 邀请人ID
	 */
	public void setShareUserId(java.lang.Integer shareUserId) {
		set("share_user_id", shareUserId);
	}

	/**
	 * 邀请人ID
	 */
	public java.lang.Integer getShareUserId() {
		return getInt("share_user_id");
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
