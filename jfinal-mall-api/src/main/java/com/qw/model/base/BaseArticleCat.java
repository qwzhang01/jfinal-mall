package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseArticleCat<M extends BaseArticleCat<M>> extends BaseModel<M> implements IBean {

	public void setCatId(java.lang.Long catId) {
		set("cat_id", catId);
	}

	public java.lang.Long getCatId() {
		return getLong("cat_id");
	}

	/**
	 * 类别名称
	 */
	public void setCatName(java.lang.String catName) {
		set("cat_name", catName);
	}

	/**
	 * 类别名称
	 */
	public java.lang.String getCatName() {
		return getStr("cat_name");
	}

	/**
	 * 默认分组
	 */
	public void setCatType(java.lang.Integer catType) {
		set("cat_type", catType);
	}

	/**
	 * 默认分组
	 */
	public java.lang.Integer getCatType() {
		return getInt("cat_type");
	}

	/**
	 * 夫级ID
	 */
	public void setParentId(java.lang.Integer parentId) {
		set("parent_id", parentId);
	}

	/**
	 * 夫级ID
	 */
	public java.lang.Integer getParentId() {
		return getInt("parent_id");
	}

	/**
	 * 是否导航显示
	 */
	public void setShowInNav(java.lang.Boolean showInNav) {
		set("show_in_nav", showInNav);
	}

	/**
	 * 是否导航显示
	 */
	public java.lang.Boolean getShowInNav() {
		return get("show_in_nav");
	}

	/**
	 * 排序
	 */
	public void setSortOrder(java.lang.Integer sortOrder) {
		set("sort_order", sortOrder);
	}

	/**
	 * 排序
	 */
	public java.lang.Integer getSortOrder() {
		return getInt("sort_order");
	}

	/**
	 * 分类描述
	 */
	public void setCatDesc(java.lang.String catDesc) {
		set("cat_desc", catDesc);
	}

	/**
	 * 分类描述
	 */
	public java.lang.String getCatDesc() {
		return getStr("cat_desc");
	}

	/**
	 * 搜索关键词
	 */
	public void setKeywords(java.lang.String keywords) {
		set("keywords", keywords);
	}

	/**
	 * 搜索关键词
	 */
	public java.lang.String getKeywords() {
		return getStr("keywords");
	}

	/**
	 * 别名
	 */
	public void setCatAlias(java.lang.String catAlias) {
		set("cat_alias", catAlias);
	}

	/**
	 * 别名
	 */
	public java.lang.String getCatAlias() {
		return getStr("cat_alias");
	}

}
