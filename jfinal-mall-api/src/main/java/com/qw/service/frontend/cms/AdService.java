package com.qw.service.frontend.cms;

import cn.qw.base.BaseService;
import com.alibaba.fastjson.JSON;
import com.qw.service.frontend.good.CategoryService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AdService extends BaseService {
    private static AdService service;

    private AdService() {
    }

    public static synchronized AdService me() {
        if (service == null) {
            service = new AdService();
        }
        return service;
    }

    public List<Record> indexBanner() {
        // TODO 先写死
        int pid = 20;
        int size = 5;
        long date = System.currentTimeMillis() / 1000;
        return findAd(pid, date, size);
    }

    public List<Record> indexFlagshipCategory() {
        // TODO 先写死
        int pid = 22;
        int size = 4;
        long date = System.currentTimeMillis() / 1000;
        return findAd(pid, date, size);
    }

    private List<Record> findAd(int pid, long date, int size) {
        String sql = "SELECT ad_name AS name, ad_code AS image, media_type AS jump_type, ad_link AS relate_id FROM butler_ad"
                + " WHERE enabled = 1 AND pid = ? AND start_time < ? AND end_time > ? ORDER BY `orderby` DESC LIMIT ?";

        List<Record> list = Db.find(sql, pid, date, date, size);
        calJump(list);
        return list;
    }

    private void calJump(List<Record> list) {
        for (Record r : list) {
            String image = r.getStr("image");
            if (StrKit.notBlank(image)) {
                // 补全地址
                r.set("image", PropKit.get("fileHost") + image);
            }
            // 补全跳转地址
            Integer jumpType = r.getInt("jump_type");
            String relateId = r.getStr("relate_id");
            if (StrKit.isBlank(relateId) || "javascript:;".equals(relateId)) {
                r.set("relate_id", "0");
            }
            if (jumpType != null && jumpType == 4) {
                if (relateId.contains("_")) {
                    String[] relateIdArray = relateId.split("_");
                    List<String> relateIdList = Arrays.stream(relateIdArray).filter(k -> StrKit.notBlank(k) && !"0".equals(k)).collect(Collectors.toList());
                    r.set("relate_id", relateIdList.get(relateIdList.size() - 1));
                    r.set("level", CategoryService.me().getLevel(r.get("relate_id")));
                }
            }
        }
    }

    public List<Record> indexFlagshipMajor() {
        // TODO 先写死
        long now = System.currentTimeMillis() / 1000;
        int adGroupId = 1;

        return findAdGroup(adGroupId, now);
    }

    public List<Record> indexFlagshipVice() {
        // TODO 先写死
        long now = System.currentTimeMillis() / 1000;
        int adGroupId = 8;

        return findAdGroup(adGroupId, now);
    }

    private List<Record> findAdGroup(int adGroupId, long now) {
        String groupSql = "SELECT group_title AS title, title_background_image AS background_image, ad_group_info FROM butler_ad_group"
                + " WHERE group_id = ? AND enabled = 1 AND start_time <= ? AND end_time >= ? ORDER BY `orderby` DESC, group_id ASC LIMIT 1";
        Record group = Db.findFirst(groupSql, adGroupId, now, now);
        String tourInfo = group.getStr("ad_group_info");
        List<TourInfo> tourInfoList = JSON.parseArray(tourInfo, TourInfo.class);
        // TODO 临时处理，先把第一个广告大图取掉
        // tourInfoList = tourInfoList.subList(1, tourInfoList.size() - 1);
        Integer[] adIds = tourInfoList.stream().sorted(Comparator.comparing(TourInfo::getSort)).map(k -> k.getId()).toArray(Integer[]::new);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < adIds.length; i++) {
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append(" ,?");
            }
        }

        List<Record> list = new ArrayList<>();
        for (int i = 0; i < adIds.length; i++) {
            String adSql = "SELECT pid, ad_name AS name, ad_code AS image, media_type AS jump_type, ad_link AS relate_id"
                    + " FROM butler_ad WHERE ad_id = ? and enabled = 1 LIMIT 1";
            Record record = Db.findFirst(adSql, adIds[i]);
            if (record != null) {
                list.add(record);
            }
        }
        calJump(list);
        return list;
    }
}

class TourInfo {
    private Integer id;
    private Integer sort;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}