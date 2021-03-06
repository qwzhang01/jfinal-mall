package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePointSum<M extends BasePointSum<M>> extends BaseModel<M> implements IBean {

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

	/**
	 * 我的投资汇总
	 */
	public void setMineInvestTotal(java.math.BigDecimal mineInvestTotal) {
		set("mine_invest_total", mineInvestTotal);
	}

	/**
	 * 我的投资汇总
	 */
	public java.math.BigDecimal getMineInvestTotal() {
		return get("mine_invest_total");
	}

	/**
	 * 我的下线投资汇总
	 */
	public void setSublineInvestTotal(java.math.BigDecimal sublineInvestTotal) {
		set("subline_invest_total", sublineInvestTotal);
	}

	/**
	 * 我的下线投资汇总
	 */
	public java.math.BigDecimal getSublineInvestTotal() {
		return get("subline_invest_total");
	}

	/**
	 * 我的投资剩余积分
	 */
	public void setMineInvestRemain(java.math.BigDecimal mineInvestRemain) {
		set("mine_invest_remain", mineInvestRemain);
	}

	/**
	 * 我的投资剩余积分
	 */
	public java.math.BigDecimal getMineInvestRemain() {
		return get("mine_invest_remain");
	}

	/**
	 * 我的下线投资剩余积分
	 */
	public void setSublineInvestRemain(java.math.BigDecimal sublineInvestRemain) {
		set("subline_invest_remain", sublineInvestRemain);
	}

	/**
	 * 我的下线投资剩余积分
	 */
	public java.math.BigDecimal getSublineInvestRemain() {
		return get("subline_invest_remain");
	}

	/**
	 * 待领取的剩余积分（剩余冻结积分）
	 */
	public void setFreezeRemain(java.math.BigDecimal freezeRemain) {
		set("freeze_remain", freezeRemain);
	}

	/**
	 * 待领取的剩余积分（剩余冻结积分）
	 */
	public java.math.BigDecimal getFreezeRemain() {
		return get("freeze_remain");
	}

	/**
	 * 剩余可用积分（包含可提现积分）
	 */
	public void setUsableRemain(java.math.BigDecimal usableRemain) {
		set("usable_remain", usableRemain);
	}

	/**
	 * 剩余可用积分（包含可提现积分）
	 */
	public java.math.BigDecimal getUsableRemain() {
		return get("usable_remain");
	}

	/**
	 * 剩余可提现积分
	 */
	public void setWithdrawableRemain(java.math.BigDecimal withdrawableRemain) {
		set("withdrawable_remain", withdrawableRemain);
	}

	/**
	 * 剩余可提现积分
	 */
	public java.math.BigDecimal getWithdrawableRemain() {
		return get("withdrawable_remain");
	}

	/**
	 * 提现总额
	 */
	public void setWithdrawableTotal(java.math.BigDecimal withdrawableTotal) {
		set("withdrawable_total", withdrawableTotal);
	}

	/**
	 * 提现总额
	 */
	public java.math.BigDecimal getWithdrawableTotal() {
		return get("withdrawable_total");
	}

	/**
	 * 消费积分总和
	 */
	public void setConsumeTotal(java.math.BigDecimal consumeTotal) {
		set("consume_total", consumeTotal);
	}

	/**
	 * 消费积分总和
	 */
	public java.math.BigDecimal getConsumeTotal() {
		return get("consume_total");
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
