package com.qw.service.bakend.cms;

import cn.qw.base.BaseService;
import com.qw.model.*;
import com.qw.service.bakend.good.GoodService;
import com.qw.service.bakend.store.StoreService;
import com.qw.service.frontend.cms.ArticleService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeSetService extends BaseService {
    private static HomeSetService service;

    private HomeSetService() {
    }

    public static synchronized HomeSetService me() {
        if (service == null) {
            service = new HomeSetService();
        }
        return service;
    }

    public Page<HomeSet> page(int pageNumber, int pageSize, int type) {
        String select = "SELECT *";
        String from = " FROM butler_home_set WHERE parent_id = 0 AND position = ?";
        from += " ORDER BY sort_id ASC";
        Page<HomeSet> paginate = HomeSet.dao.paginate(pageNumber, pageSize, select, from, type);
        List<HomeSet> list = paginate.getList();
        for (HomeSet hs : list) {
            String img = hs.getImg();
            if (StrKit.notBlank(img)) {
                hs.setImg(PropKit.get("fileHost") + img);
            }
        }
        return paginate;
    }

    public void deleteByParentId(int id) {
        String sql = "DELETE FROM butler_home_set WHERE parent_id = ?";
        Db.update(sql, id);
    }

    public void deleteById(int id) {
        HomeSet.dao.deleteById(id);
    }

    public List<Record> findGoto(int gotoType, String keyword) {
//        1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀
        if (1 == gotoType) {
            return ArticleService.me().search(keyword);
        }
        if (2 == gotoType) {
            return GoodService.me().search(keyword).stream().map(m -> {
                Record r = new Record();
                r.set("key", m.getInt("goodId"));
                r.set("value", m.getStr("name"));
                return r;
            }).collect(Collectors.toList());
        }
        if (3 == gotoType) {
            return GoodService.me().searchCategory(keyword);
        }
        if (5 == gotoType) {
            return StoreService.me().search(keyword);
        }
        if (6 == gotoType) {
            return null;
        }
        return null;
    }

    public Page<Record> goodPage(int pageNumber, int pageSize, int parentId, String goodName) {
        String select = "SELECT hs.id, g.goods_name goodName, CONCAT('" + PropKit.get("fileHost") + "', g.original_img) imgPath, g.goods_sn goodSn, g.goods_id";
        String from = " FROM butler_home_set hs LEFT JOIN butler_good g ON g.goods_id = hs.goto_id WHERE hs.position = 6 AND hs.parent_id = ?";
        List<Object> paras = new ArrayList<>();
        paras.add(parentId);
        if (StrKit.notBlank(goodName)) {
            from += " AND g.goods_name LIKE ?";
            paras.add("%" + goodName + "%");
        }
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            BigDecimal smallPrice = com.qw.service.frontend.good.GoodService.me().getSmallPrice(r.getLong("goods_id"));
            r.set("price", smallPrice);
        }
        return page;
    }

    public String gotoId(int gotoType, Integer gotoId) {
        //        1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀
        if (7 == gotoType || 8 == gotoType || 9 == gotoType || 10 == gotoType) {
            return "";
        }
        if (1 == gotoType) {
            Article article = Article.dao.findById(gotoId);
            if (article == null) {
                return "";
            }
            return article.getTitle();
        }
        if (2 == gotoType) {
            Good article = Good.dao.findById(gotoId);
            if (article == null) {
                return "";
            }
            return article.getGoodsName();
        }
        if (3 == gotoType) {
            GoodCategory article = GoodCategory.dao.findById(gotoId);
            if (article == null) {
                return "";
            }
            return article.getMobileName();
        }
        if (4 == gotoType) {
            return "";
        }
        if (5 == gotoType) {
            Store article = Store.dao.findById(gotoId);
            if (article == null) {
                return "";
            }
            return article.getStoreName();
        }
        if (6 == gotoType) {
            return "";
        }
        return "";
    }
}