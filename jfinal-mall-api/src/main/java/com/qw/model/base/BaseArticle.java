package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseArticle<M extends BaseArticle<M>> extends BaseModel<M> implements IBean {

	public void setArticleId(java.lang.Integer articleId) {
		set("article_id", articleId);
	}

	public java.lang.Integer getArticleId() {
		return getInt("article_id");
	}

	/**
	 * 类别ID
	 */
	public void setCatId(java.lang.Integer catId) {
		set("cat_id", catId);
	}

	/**
	 * 类别ID
	 */
	public java.lang.Integer getCatId() {
		return getInt("cat_id");
	}

	/**
	 * 文章标题
	 */
	public void setTitle(java.lang.String title) {
		set("title", title);
	}

	/**
	 * 文章标题
	 */
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}

	public java.lang.String getContent() {
		return getStr("content");
	}

	/**
	 * 文章作者
	 */
	public void setAuthor(java.lang.String author) {
		set("author", author);
	}

	/**
	 * 文章作者
	 */
	public java.lang.String getAuthor() {
		return getStr("author");
	}

	/**
	 * 作者邮箱
	 */
	public void setAuthorEmail(java.lang.String authorEmail) {
		set("author_email", authorEmail);
	}

	/**
	 * 作者邮箱
	 */
	public java.lang.String getAuthorEmail() {
		return getStr("author_email");
	}

	/**
	 * 关键字
	 */
	public void setKeywords(java.lang.String keywords) {
		set("keywords", keywords);
	}

	/**
	 * 关键字
	 */
	public java.lang.String getKeywords() {
		return getStr("keywords");
	}

	public void setArticleType(java.lang.Boolean articleType) {
		set("article_type", articleType);
	}

	public java.lang.Boolean getArticleType() {
		return get("article_type");
	}

	public void setIsOpen(java.lang.Boolean isOpen) {
		set("is_open", isOpen);
	}

	public java.lang.Boolean getIsOpen() {
		return get("is_open");
	}

	public void setAddTime(java.lang.Long addTime) {
		set("add_time", addTime);
	}

	public java.lang.Long getAddTime() {
		return getLong("add_time");
	}

	/**
	 * 附件地址
	 */
	public void setFileUrl(java.lang.String fileUrl) {
		set("file_url", fileUrl);
	}

	/**
	 * 附件地址
	 */
	public java.lang.String getFileUrl() {
		return getStr("file_url");
	}

	public void setOpenType(java.lang.Boolean openType) {
		set("open_type", openType);
	}

	public java.lang.Boolean getOpenType() {
		return get("open_type");
	}

	/**
	 * 链接地址
	 */
	public void setLink(java.lang.String link) {
		set("link", link);
	}

	/**
	 * 链接地址
	 */
	public java.lang.String getLink() {
		return getStr("link");
	}

	/**
	 * 文章摘要
	 */
	public void setDescription(java.lang.String description) {
		set("description", description);
	}

	/**
	 * 文章摘要
	 */
	public java.lang.String getDescription() {
		return getStr("description");
	}

	/**
	 * 浏览量
	 */
	public void setClick(java.lang.Integer click) {
		set("click", click);
	}

	/**
	 * 浏览量
	 */
	public java.lang.Integer getClick() {
		return getInt("click");
	}

	/**
	 * 文章预告发布时间
	 */
	public void setPublishTime(java.lang.Integer publishTime) {
		set("publish_time", publishTime);
	}

	/**
	 * 文章预告发布时间
	 */
	public java.lang.Integer getPublishTime() {
		return getInt("publish_time");
	}

	/**
	 * 文章缩略图
	 */
	public void setThumb(java.lang.String thumb) {
		set("thumb", thumb);
	}

	/**
	 * 文章缩略图
	 */
	public java.lang.String getThumb() {
		return getStr("thumb");
	}

}
