package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseFreightRegion<M extends BaseFreightRegion<M>> extends BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	/**
	 * 模板id
	 */
	public void setTemplateId(java.lang.Long templateId) {
		set("template_id", templateId);
	}

	/**
	 * 模板id
	 */
	public java.lang.Long getTemplateId() {
		return getLong("template_id");
	}

	/**
	 * 运费模板配置ID
	 */
	public void setConfigId(java.lang.Long configId) {
		set("config_id", configId);
	}

	/**
	 * 运费模板配置ID
	 */
	public java.lang.Long getConfigId() {
		return getLong("config_id");
	}

	/**
	 * region表id
	 */
	public void setRegionId(java.lang.Long regionId) {
		set("region_id", regionId);
	}

	/**
	 * region表id
	 */
	public java.lang.Long getRegionId() {
		return getLong("region_id");
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
