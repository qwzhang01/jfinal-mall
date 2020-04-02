package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCmsArticleCat<M extends BaseCmsArticleCat<M>> extends BaseModel<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
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
	public void setKeyword(java.lang.String keyword) {
		set("keyword", keyword);
	}

	/**
	 * 搜索关键词
	 */
	public java.lang.String getKeyword() {
		return getStr("keyword");
	}

}
