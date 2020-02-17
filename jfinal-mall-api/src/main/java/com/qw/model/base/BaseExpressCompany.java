package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseExpressCompany<M extends BaseExpressCompany<M>> extends BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 公司名称
	 */
	public void setName(java.lang.String name) {
		set("name", name);
	}

	/**
	 * 公司名称
	 */
	public java.lang.String getName() {
		return getStr("name");
	}

	/**
	 * 公司编码
	 */
	public void setCode(java.lang.String code) {
		set("code", code);
	}

	/**
	 * 公司编码
	 */
	public java.lang.String getCode() {
		return getStr("code");
	}

	/**
	 * 说明
	 */
	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}

	/**
	 * 说明
	 */
	public java.lang.String getRemark() {
		return getStr("remark");
	}

	/**
	 * 类型
	 */
	public void setType(java.lang.String type) {
		set("type", type);
	}

	/**
	 * 类型
	 */
	public java.lang.String getType() {
		return getStr("type");
	}

	/**
	 * 国家编码
	 */
	public void setNationCode(java.lang.String nationCode) {
		set("nation_code", nationCode);
	}

	/**
	 * 国家编码
	 */
	public java.lang.String getNationCode() {
		return getStr("nation_code");
	}

	/**
	 * 是否使用 1使用 2不使用
	 */
	public void setIsDisable(java.lang.Integer isDisable) {
		set("isDisable", isDisable);
	}

	/**
	 * 是否使用 1使用 2不使用
	 */
	public java.lang.Integer getIsDisable() {
		return getInt("isDisable");
	}

}
