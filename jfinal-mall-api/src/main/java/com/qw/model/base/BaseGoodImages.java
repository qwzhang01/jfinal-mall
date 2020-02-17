package com.qw.model.base;

import cn.qw.base.BaseModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by QW, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseGoodImages<M extends BaseGoodImages<M>> extends BaseModel<M> implements IBean {

	/**
	 * 图片id 自增
	 */
	public void setImgId(java.lang.Integer imgId) {
		set("img_id", imgId);
	}

	/**
	 * 图片id 自增
	 */
	public java.lang.Integer getImgId() {
		return getInt("img_id");
	}

	/**
	 * 商品id
	 */
	public void setGoodsId(java.lang.Integer goodsId) {
		set("goods_id", goodsId);
	}

	/**
	 * 商品id
	 */
	public java.lang.Integer getGoodsId() {
		return getInt("goods_id");
	}

	/**
	 * 图片地址
	 */
	public void setImageUrl(java.lang.String imageUrl) {
		set("image_url", imageUrl);
	}

	/**
	 * 图片地址
	 */
	public java.lang.String getImageUrl() {
		return getStr("image_url");
	}

	/**
	 * 商品缩略图排序,最小的拍最前面
	 */
	public void setImgSort(java.lang.Boolean imgSort) {
		set("img_sort", imgSort);
	}

	/**
	 * 商品缩略图排序,最小的拍最前面
	 */
	public java.lang.Boolean getImgSort() {
		return get("img_sort");
	}

	/**
	 * 相册ID
	 */
	public void setAlbumId(java.lang.Integer albumId) {
		set("album_id", albumId);
	}

	/**
	 * 相册ID
	 */
	public java.lang.Integer getAlbumId() {
		return getInt("album_id");
	}

}
