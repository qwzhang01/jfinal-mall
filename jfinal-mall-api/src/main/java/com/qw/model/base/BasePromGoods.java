package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePromGoods<M extends BasePromGoods<M>> extends BaseModel<M> implements IBean {

	/**
	 * 活动ID
	 */
	public void setId(java.lang.Long id) {
		set("id", id);
	}

	/**
	 * 活动ID
	 */
	public java.lang.Long getId() {
		return getLong("id");
	}

	/**
	 * 促销活动名称
	 */
	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	/**
	 * 促销活动名称
	 */
	public java.lang.String getTitle() {
		return getStr("title");
	}

	/**
	 * 促销类型:0直接打折,1减价优惠,2固定金额出售,3买就赠优惠券
	 */
	public void setType(java.lang.Integer type) {
		set("type", type);
	}

	/**
	 * 促销类型:0直接打折,1减价优惠,2固定金额出售,3买就赠优惠券
	 */
	public java.lang.Integer getType() {
		return getInt("type");
	}

	/**
	 * 优惠体现
	 */
	public void setExpression(java.lang.String expression) {
		set("expression", expression);
	}

	/**
	 * 优惠体现
	 */
	public java.lang.String getExpression() {
		return getStr("expression");
	}

	/**
	 * 活动描述
	 */
	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	/**
	 * 活动描述
	 */
	public java.lang.String getDescription() {
		return getStr("description");
	}

	/**
	 * 活动开始时间
	 */
	public void setStartTime(java.lang.Integer startTime) {
		set("start_time", startTime);
	}

	/**
	 * 活动开始时间
	 */
	public java.lang.Integer getStartTime() {
		return getInt("start_time");
	}

	/**
	 * 活动结束时间
	 */
	public void setEndTime(java.lang.Integer endTime) {
		set("end_time", endTime);
	}

	/**
	 * 活动结束时间
	 */
	public java.lang.Integer getEndTime() {
		return getInt("end_time");
	}

	/**
	 * 1正常，0管理员关闭
	 */
	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}

	/**
	 * 1正常，0管理员关闭
	 */
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	/**
	 * 是否已结束
	 */
	public void setIsEnd(java.lang.Integer isEnd) {
		set("is_end", isEnd);
	}

	/**
	 * 是否已结束
	 */
	public java.lang.Integer getIsEnd() {
		return getInt("is_end");
	}

	/**
	 * 适用范围
	 */
	public void setGroup(java.lang.String group) {
		set("group", group);
	}

	/**
	 * 适用范围
	 */
	public java.lang.String getGroup() {
		return getStr("group");
	}

	/**
	 * 商家店铺id
	 */
	public void setStoreId(java.lang.Integer storeId) {
		set("store_id", storeId);
	}

	/**
	 * 商家店铺id
	 */
	public java.lang.Integer getStoreId() {
		return getInt("store_id");
	}

	/**
	 * 排序
	 */
	public void setOrderby(java.lang.Integer orderby) {
		set("orderby", orderby);
	}

	/**
	 * 排序
	 */
	public java.lang.Integer getOrderby() {
		return getInt("orderby");
	}

	/**
	 * 活动宣传图片
	 */
	public void setPromImg(java.lang.String promImg) {
		set("prom_img", promImg);
	}

	/**
	 * 活动宣传图片
	 */
	public java.lang.String getPromImg() {
		return getStr("prom_img");
	}

	/**
	 * 是否推荐
	 */
	public void setRecommend(java.lang.Integer recommend) {
		set("recommend", recommend);
	}

	/**
	 * 是否推荐
	 */
	public java.lang.Integer getRecommend() {
		return getInt("recommend");
	}

	/**
	 * 每人限购数
	 */
	public void setBuyLimit(java.lang.Integer buyLimit) {
		set("buy_limit", buyLimit);
	}

	/**
	 * 每人限购数
	 */
	public java.lang.Integer getBuyLimit() {
		return getInt("buy_limit");
	}

}
