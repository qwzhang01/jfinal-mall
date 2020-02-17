package com.qw.service.frontend.cms;

import cn.qw.base.BaseService;
import com.qw.model.HomeSet;
import com.qw.service.frontend.good.GoodService;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * 获取banner
     */
    public List<Record> findBanner() {
        List<HomeSet> list = find(1);
        return list.stream().map(h -> {
            Record r = new Record();
            r.set("gotoType", h.getGotoType());
            r.set("gotoId", h.getGotoId());
            r.set("img", PropKit.get("fileHost") + h.getImg());
            r.set("webUrl", h.getWebUrl());
            r.set("backgroundColor", h.getBackgroundColor());
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取金刚区按钮
     */
    public List<Record> findDiamond() {
        List<HomeSet> list = find(2);
        return list.stream().map(h -> {
            Record r = new Record();
            r.set("gotoType", h.getGotoType());
            r.set("gotoId", h.getGotoId());
            r.set("title", h.getTitle());
            r.set("webUrl", h.getWebUrl());
            r.set("img", PropKit.get("fileHost") + h.getImg());
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取广告
     */
    public Record findAdvertise() {
        Record result = new Record();
        result.set("bigAdvertise", findBigAdvertise());
        List<Record> smallAdvertise = findSmallAdvertise();
        if (smallAdvertise != null && smallAdvertise.size() > 0) {
            String backgroundColor = smallAdvertise.get(0).getStr("backgroundColor");
            result.set("smallAdvertiseBackgroundColor", backgroundColor);
        }
        result.set("smallAdvertise", smallAdvertise.stream().map(m -> m.remove("backgroundColor")).collect(Collectors.toList()));
        return result;
    }

    /**
     * 获取大广告
     */
    private Record findBigAdvertise() {
        List<HomeSet> list = find(4);
        if (list != null && list.size() > 0) {
            HomeSet homeSet = list.get(0);
            Record record = new Record();
            record.set("img", PropKit.get("fileHost") + homeSet.getImg());
            record.set("gotoType", homeSet.getGotoType());
            record.set("webUrl", homeSet.getWebUrl());
            record.set("gotoId", homeSet.getGotoId());
            return record;
        }
        return null;
    }

    /**
     * 获取小广告
     */
    private List<Record> findSmallAdvertise() {
        List<HomeSet> list = find(5);
        return list.stream().map(h -> {
            Record r = new Record();
            r.set("backgroundColor", h.getBackgroundColor());
            r.set("gotoType", h.getGotoType());
            r.set("gotoId", h.getGotoId());
            r.set("webUrl", h.getWebUrl());
            r.set("img", PropKit.get("fileHost") + h.getImg());
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取首页专场
     */
    public List<Record> specialShow() {
        List<HomeSet> list = find(6);
        if (list == null || list.size() <= 0) {
            return null;
        }
        // 根据父ID分组
        Map<Integer, List<HomeSet>> map = list.stream().collect(Collectors.groupingBy(l -> l.getParentId()));
        // 获取父ID是0的，也就是专题介绍
        List<HomeSet> parentSpecialShow = map.get(0);
        if (parentSpecialShow == null || parentSpecialShow.size() <= 0) {
            return null;
        }
        return parentSpecialShow.stream().filter(h -> map.get(h.getId()) != null && map.get(h.getId()).size() > 0).map(h -> {
            Record r = new Record();
            r.set("gotoType", h.getGotoType());
            r.set("gotoId", h.getGotoId());
            r.set("img", PropKit.get("fileHost") + h.getImg());
            r.set("webUrl", h.getWebUrl());
            // 获取专题对应的商品信息
            r.set("good", GoodService.me().findHome(map.get(h.getId())));
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 根据位置、跳转类型查询
     */
    private List<HomeSet> find(int position) {
        if (position == 0) {
            return null;
        }
        // goto_type 类型（1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀）
        // position 位置（1banner2金刚区3资讯区4大广告5小广告6专场）
        String sql = "SELECT * FROM butler_home_set WHERE is_show = 1";
        List<Object> paras = new ArrayList<>();
        if (position != 0) {
            sql += " AND position = ?";
            paras.add(position);
        }
        sql += " ORDER BY sort_id DESC";
        return HomeSet.dao.find(sql, paras.toArray());
    }
}
