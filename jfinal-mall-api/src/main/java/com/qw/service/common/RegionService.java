package com.qw.service.common;

import cn.qw.base.BaseService;
import com.qw.model.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * 区域管理
 * @author qw
 */
public class RegionService extends BaseService {
    private static RegionService service;

    private RegionService() {
    }

    public static synchronized RegionService me() {
        if (service == null) {
            service = new RegionService();
        }
        return service;
    }

    public String orderAddress(int pId, int cId, int dId, int tId) {
        StringBuilder sb = new StringBuilder();
        Region region = get(pId);
        if (region != null) {
            sb.append(region.getName());
        }
        region = get(cId);
        if (region != null) {
            sb.append(region.getName());
        }
        region = get(dId);
        if (region != null) {
            sb.append(region.getName());
        }
        region = get(tId);
        if (region != null) {
            sb.append(region.getName());
        }
        return sb.toString();
    }

    public List<Region> findByParentId(Integer parentId) {
        String sql = "SELECT * FROM oms_region where 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (parentId == 0) {
            sql += " AND level = ?";
            paras.add(1);
        } else {
            sql += " AND parent_id = ?";
            paras.add(parentId);
        }
        sql += " ORDER BY name ASC";
        List<Region> result = Region.dao.find(sql, paras.toArray());
        return result;
    }

    public String name(Integer id) {
        Region region = Region.dao.findById(id);
        if (region == null) {
            return "";
        }
        return region.getName();
    }

    /**
     * 递归获取名称
     * @param id
     * @return
     */
    public String getName(int id) {
        Region region = Region.dao.findById(id);
        if (region == null) {
            return "";
        }
        String result = region.getName();
        if (region.getParentId() != 0) {
            result = getName(region.getParentId()) + result;
        }
        return result;
    }

    private Region get(int id) {
        return Region.dao.findById(id);
    }
}