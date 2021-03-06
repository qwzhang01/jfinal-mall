package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFreightConfig<M extends BaseFreightConfig<M>> extends BaseModel<M> implements IBean {

	/**
	 * 配置id
	 */
	public void setConfigId(java.lang.Long configId) {
		set("config_id", configId);
	}

	/**
	 * 配置id
	 */
	public java.lang.Long getConfigId() {
		return getLong("config_id");
	}

	/**
	 * 首(重：体积：件）
	 */
	public void setFirstUnit(java.lang.Double firstUnit) {
		set("first_unit", firstUnit);
	}

	/**
	 * 首(重：体积：件）
	 */
	public java.lang.Double getFirstUnit() {
		return getDouble("first_unit");
	}

	/**
	 * 首(重：体积：件）运费
	 */
	public void setFirstMoney(java.math.BigDecimal firstMoney) {
		set("first_money", firstMoney);
	}

	/**
	 * 首(重：体积：件）运费
	 */
	public java.math.BigDecimal getFirstMoney() {
		return get("first_money");
	}

	/**
	 * 继续加（件：重量：体积）区间
	 */
	public void setContinueUnit(java.lang.Double continueUnit) {
		set("continue_unit", continueUnit);
	}

	/**
	 * 继续加（件：重量：体积）区间
	 */
	public java.lang.Double getContinueUnit() {
		return getDouble("continue_unit");
	}

	/**
	 * 继续加（件：重量：体积）的运费
	 */
	public void setContinueMoney(java.math.BigDecimal continueMoney) {
		set("continue_money", continueMoney);
	}

	/**
	 * 继续加（件：重量：体积）的运费
	 */
	public java.math.BigDecimal getContinueMoney() {
		return get("continue_money");
	}

	/**
	 * 运费模板ID
	 */
	public void setTemplateId(java.lang.Long templateId) {
		set("template_id", templateId);
	}

	/**
	 * 运费模板ID
	 */
	public java.lang.Long getTemplateId() {
		return getLong("template_id");
	}

	/**
	 * 是否是默认运费配置.0不是，1是
	 */
	public void setIsDefault(java.lang.Boolean isDefault) {
		set("is_default", isDefault);
	}

	/**
	 * 是否是默认运费配置.0不是，1是
	 */
	public java.lang.Boolean getIsDefault() {
		return get("is_default");
	}

	/**
	 * 店铺ID
	 */
	public void setStoreId(java.lang.Long storeId) {
		set("store_id", storeId);
	}

	/**
	 * 店铺ID
	 */
	public java.lang.Long getStoreId() {
		return getLong("store_id");
	}

}
