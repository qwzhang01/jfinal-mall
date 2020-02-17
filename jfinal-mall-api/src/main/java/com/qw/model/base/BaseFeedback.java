package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFeedback<M extends BaseFeedback<M>> extends BaseModel<M> implements IBean {

	/**
	 * 默认自增ID
	 */
	public void setMsgId(java.lang.Integer msgId) {
		set("msg_id", msgId);
	}

	/**
	 * 默认自增ID
	 */
	public java.lang.Integer getMsgId() {
		return getInt("msg_id");
	}

	/**
	 * 回复留言ID
	 */
	public void setParentId(java.lang.Integer parentId) {
		set("parent_id", parentId);
	}

	/**
	 * 回复留言ID
	 */
	public java.lang.Integer getParentId() {
		return getInt("parent_id");
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

	public void setUserName(java.lang.String userName) {
		set("user_name", userName);
	}

	public java.lang.String getUserName() {
		return getStr("user_name");
	}

	/**
	 * 留言标题
	 */
	public void setMsgTitle(java.lang.String msgTitle) {
		set("msg_title", msgTitle);
	}

	/**
	 * 留言标题
	 */
	public java.lang.String getMsgTitle() {
		return getStr("msg_title");
	}

	/**
	 * 留言类型
	 */
	public void setMsgType(java.lang.Boolean msgType) {
		set("msg_type", msgType);
	}

	/**
	 * 留言类型
	 */
	public java.lang.Boolean getMsgType() {
		return get("msg_type");
	}

	/**
	 * 处理状态
	 */
	public void setMsgStatus(java.lang.Boolean msgStatus) {
		set("msg_status", msgStatus);
	}

	/**
	 * 处理状态
	 */
	public java.lang.Boolean getMsgStatus() {
		return get("msg_status");
	}

	/**
	 * 留言内容
	 */
	public void setMsgContent(java.lang.String msgContent) {
		set("msg_content", msgContent);
	}

	/**
	 * 留言内容
	 */
	public java.lang.String getMsgContent() {
		return getStr("msg_content");
	}

	/**
	 * 留言时间
	 */
	public void setMsgTime(java.lang.Long msgTime) {
		set("msg_time", msgTime);
	}

	/**
	 * 留言时间
	 */
	public java.lang.Long getMsgTime() {
		return getLong("msg_time");
	}

	public void setMessageImg(java.lang.String messageImg) {
		set("message_img", messageImg);
	}

	public java.lang.String getMessageImg() {
		return getStr("message_img");
	}

	public void setOrderId(java.lang.Long orderId) {
		set("order_id", orderId);
	}

	public java.lang.Long getOrderId() {
		return getLong("order_id");
	}

	public void setMsgArea(java.lang.Boolean msgArea) {
		set("msg_area", msgArea);
	}

	public java.lang.Boolean getMsgArea() {
		return get("msg_area");
	}

}
