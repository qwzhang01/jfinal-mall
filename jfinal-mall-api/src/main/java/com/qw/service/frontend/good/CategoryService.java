package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import com.qw.model.Good;
import com.qw.model.GoodCategory;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;
import java.util.stream.Collectors;

public class CategoryService extends BaseService {
    private static CategoryService service;

    private CategoryService() {
    }

    public static synchronized CategoryService me() {
        if (service == null) {
            service = new CategoryService();
        }
        return service;
    }

    /**
     * 获取所有分类，按照父子关系排列
     */
    public List<Record> all(Integer isShow) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT * FROM butler_good_category WHERE 1 = 1";
        if (isShow != null) {
            sql += " AND is_show = ?";
            params.add(isShow);
        }
        sql += " ORDER BY mobile_name ASC";
        List<GoodCategory> list = GoodCategory.dao.find(sql, params.toArray());
        Map<Long, List<GoodCategory>> pMap = list.stream().collect(Collectors.groupingBy(c -> c.getParentId()));
        return catTree(pMap, 0);
    }

    /**
     * 循环修复 两个path，保证数据正确性
     *
     * @param pId
     */
    private void fix(int pId) {
        String sql = "SELECT * FROM butler_good_category where parent_id = ?";
        List<GoodCategory> list = GoodCategory.dao.find(sql, pId);
        if (list == null || list.size() <= 0) {
            return;
        }
        list.stream().forEach(goodCategory -> {
            Long parentId = goodCategory.getParentId();
            if (parentId != 0) {
                GoodCategory pGoodCategory = GoodCategory.dao.findById(parentId);
                String parentIdPath = pGoodCategory.getParentIdPath();
                String namePath = pGoodCategory.getNamePath();
                parentIdPath = parentIdPath + goodCategory.getId() + "_";
                namePath = namePath + "/" + goodCategory.getMobileName();
                goodCategory.setParentIdPath(parentIdPath);
                goodCategory.setNamePath(namePath);
            } else {
                goodCategory.setParentIdPath(goodCategory.getId() + "_");
                goodCategory.setNamePath(goodCategory.getMobileName());
            }
            goodCategory.update(false);
        });

        list.stream().forEach(s -> fix(s.getId().intValue()));
    }

    private List<Record> catTree(Map<Long, List<GoodCategory>> pMap, long parentId) {
        List<GoodCategory> levelOneList = pMap.get(parentId);
        if (levelOneList == null) {
            return null;
        }
        return levelOneList.stream().sorted(Comparator.comparing(GoodCategory::getSortOrder)).map(o -> {
            Record r = new Record();
            r.set("id", o.getId());
            r.set("name", o.getName());
            if (StrKit.notBlank(o.getImage())) {
                r.set("icon", PropKit.get("fileHost") + o.getImage());
                r.set("image", PropKit.get("fileHost") + o.getImage());
            }
            List<Record> children = catTree(pMap, o.getId());
            r.set("children", children);
            if (children != null && children.size() > 0) {
                r.set("isLeaf", false);
                r.set("hasChildren", true);
            } else {
                r.set("isLeaf", false);
                r.set("hasChildren", false);
            }
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取父亲爷爷辈的分类，按照父子关系排列
     */
    public List<Record> parentTree() {
        String sql = "SELECT * FROM butler_good_category WHERE is_show = 1";
        List<GoodCategory> list = GoodCategory.dao.find(sql);
        Map<Long, List<GoodCategory>> pMap = list.stream().collect(Collectors.groupingBy(c -> c.getParentId()));
        return catParentTree(pMap, 0L);
    }

    private List<Record> catParentTree(Map<Long, List<GoodCategory>> pMap, long parentId) {
        List<GoodCategory> levelOneList = pMap.get(parentId);
        if (levelOneList == null) {
            return null;
        }
        return levelOneList.stream().sorted(Comparator.comparing(GoodCategory::getSortOrder)).map(o -> {
            Record r = new Record();
            r.set("id", o.getId());
            r.set("name", o.getName());
            String parentIdPath = o.getParentIdPath();
            r.set("categoryIds", Arrays.stream(parentIdPath.split("_")).filter(i -> StrKit.notBlank(i)).filter(i -> !"0".equals(i)).toArray(String[]::new));
            if (StrKit.notBlank(o.getImage())) {
                r.set("icon", PropKit.get("fileHost") + o.getImage());
                r.set("image", PropKit.get("fileHost") + o.getImage());
            }
            if (parentId == 0) {
                List<Record> children = catParentTree(pMap, o.getId());
                r.set("children", children);
            }
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取首页导航
     */
    public List<Record> homeNav() {
        String sql = "SELECT id, mobile_name name, CONCAT('" + PropKit.get("fileHost") + "', image) as image FROM butler_good_category WHERE is_show = 1 AND parent_id = 0 ORDER BY sort_order DESC";
        List<Record> list = Db.find(sql);
        Record index = new Record();
        index.set("id", 0);
        index.set("image", "");
        index.set("name", "精选");
        list.add(0, index);
        return list;
    }


    public List<Record> findModule() {
        String sql = "SELECT id, mobile_name AS title, icon AS image, media_type AS jump_type, ad_link AS relate_id FROM butler_category"
                + " WHERE is_show = 1 AND `level` = 1"
                + " ORDER BY sort_order DESC";
        List<Record> list = Db.find(sql);
        for (Record r : list) {
            // 图片加前缀
            String image = r.getStr("image");
            if (StrKit.notBlank(image)) {
                r.set("image", PropKit.get("fileHost") + image);
            }
            // 跳转类型：6.模块 5.web链接 4.商品分类
            Integer jumpType = r.getInt("jump_type");
            if (jumpType != null && (jumpType == 4 || jumpType == 6)) {
                String relateId = r.getStr("relate_id");
                if (relateId.contains("_")) {
                    String[] relateIdArray = relateId.split("_");
                    List<String> relateIdList = Arrays.stream(relateIdArray).filter(k -> StrKit.notBlank(k) && !"0".equals(k)).collect(Collectors.toList());
                    r.set("relate_id", relateIdList.get(relateIdList.size() - 1));
                    r.set("level", getLevel(r.get("relate_id")));
                }
            }
        }


        return list;
    }

    public Integer getLevel(String relateId) {
        String sql = "SELECT IFNULL(`level`,0) FROM butler_good_category WHERE id = ? LIMIT 1";
        return Db.queryInt(sql, relateId);
    }

    /**
     * 判断分类是为父类ID
     */
    public boolean isParentId(Integer id) {
        String sql = "SELECT * FROM butler_good_category WHERE parent_id = ?";
        List<GoodCategory> list = GoodCategory.dao.find(sql, id);
        if (list.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasLinkGood(Integer id) {
        String sql = "SELECT * FROM butler_good WHERE cat_id1 = ? OR cat_id2 = ? OR cat_id3 = ?";
        List<Good> list = Good.dao.find(sql, id, id, id);
        if (list.size() != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean save(long id, String name, long parentId, int isHot, long isShow, String image, String icon) {
        GoodCategory goodCategory = GoodCategory.dao.findById(id);
        if (goodCategory == null) {
            goodCategory = new GoodCategory();
        }
        goodCategory.setName(name);
        goodCategory.setMobileName(name);
        goodCategory.setParentId(parentId);
        goodCategory.setIsShow(isShow);
        goodCategory.setImage(image);
        goodCategory.setIcon(icon);
        goodCategory.setIsHot(isHot);
        goodCategory.saveOrUpdate(false);
        if (parentId != 0) {
            GoodCategory pGoodCategory = GoodCategory.dao.findById(parentId);
            String parentIdPath = pGoodCategory.getParentIdPath();
            String namePath = pGoodCategory.getNamePath();
            parentIdPath = parentIdPath + goodCategory.getId() + "_";
            namePath = namePath + "/" + goodCategory.getMobileName();
            goodCategory.setParentIdPath(parentIdPath);
            goodCategory.setNamePath(namePath);
        } else {
            goodCategory.setParentIdPath(goodCategory.getId() + "_");
            goodCategory.setNamePath(goodCategory.getMobileName());
        }
        return goodCategory.update(false);
    }
}