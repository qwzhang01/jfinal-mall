package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseStoreAddress<M extends BaseStoreAddress<M>> extends BaseModel<M> implements IBean {

	public void setStoreAddressId(java.lang.Integer storeAddressId) {
		set("store_address_id", storeAddressId);
	}

	public java.lang.Integer getStoreAddressId() {
		return getInt("store_address_id");
	}

	/**
	 * 收货人
	 */
	public void setConsignee(java.lang.String consignee) {
		set("consignee", consignee);
	}

	/**
	 * 收货人
	 */
	public java.lang.String getConsignee() {
		return getStr("consignee");
	}

	/**
	 * 省份
	 */
	public void setProvinceId(java.lang.Integer provinceId) {
		set("province_id", provinceId);
	}

	/**
	 * 省份
	 */
	public java.lang.Integer getProvinceId() {
		return getInt("province_id");
	}

	/**
	 * 城市
	 */
	public void setCityId(java.lang.Integer cityId) {
		set("city_id", cityId);
	}

	/**
	 * 城市
	 */
	public java.lang.Integer getCityId() {
		return getInt("city_id");
	}

	/**
	 * 地区
	 */
	public void setDistrictId(java.lang.Integer districtId) {
		set("district_id", districtId);
	}

	/**
	 * 地区
	 */
	public java.lang.Integer getDistrictId() {
		return getInt("district_id");
	}

	/**
	 * 乡镇
	 */
	public void setTownId(java.lang.Integer townId) {
		set("town_id", townId);
	}

	/**
	 * 乡镇
	 */
	public java.lang.Integer getTownId() {
		return getInt("town_id");
	}

	/**
	 * 地址
	 */
	public void setAddress(java.lang.String address) {
		set("address", address);
	}

	/**
	 * 地址
	 */
	public java.lang.String getAddress() {
		return getStr("address");
	}

	/**
	 * 邮政编码
	 */
	public void setZipCode(java.lang.String zipCode) {
		set("zip_code", zipCode);
	}

	/**
	 * 邮政编码
	 */
	public java.lang.String getZipCode() {
		return getStr("zip_code");
	}

	/**
	 * 手机
	 */
	public void setMobile(java.lang.String mobile) {
		set("mobile", mobile);
	}

	/**
	 * 手机
	 */
	public java.lang.String getMobile() {
		return getStr("mobile");
	}

	/**
	 * 1为默认收货地址
	 */
	public void setIsDefault(java.lang.Boolean isDefault) {
		set("is_default", isDefault);
	}

	/**
	 * 1为默认收货地址
	 */
	public java.lang.Boolean getIsDefault() {
		return get("is_default");
	}

	/**
	 * 0为发货地址。1为收货地址
	 */
	public void setType(java.lang.Boolean type) {
		set("type", type);
	}

	/**
	 * 0为发货地址。1为收货地址
	 */
	public java.lang.Boolean getType() {
		return get("type");
	}

	/**
	 * 店铺id
	 */
	public void setStoreId(java.lang.Long storeId) {
		set("store_id", storeId);
	}

	/**
	 * 店铺id
	 */
	public java.lang.Long getStoreId() {
		return getLong("store_id");
	}

}