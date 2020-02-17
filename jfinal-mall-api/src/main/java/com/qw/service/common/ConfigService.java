package com.qw.service.common;

import cn.qw.base.BaseService;
import com.qw.conf.QuantityConf;
import com.qw.model.Config;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 配置信息
 * @author qw
 */
public class ConfigService extends BaseService {
    private static ConfigService service;
    private Cache cache;

    private ConfigService() {
        this.cache = Redis.use(QuantityConf.PARAM_CATCHE);
    }

    public static synchronized ConfigService me() {
        if (service == null) {
            service = new ConfigService();
        }
        return service;
    }

    /**
     * 获取参数分页数据
     */
    public Page<Record> pageList(int pageNumber, int pageSize, String key) {
        String select = "SELECT * ";
        String from = " FROM oms_config WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from += " AND (name LIKE ? OR value LIKE ? OR inc_type LIKE ? OR `desc` LIKE ?)";
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from += " ORDER BY inc_type ASC, id ASC";
        return Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
    }

    /**
     * 根据类型键 获取list类型的k-v列表
     */
    public List<Record> findList(String incType) {
        List<Record> result = cache.get(CACHE_NAME + "-findList-" + incType);
        if (result != null) {
            return result;
        }
        List<Config> list = Config.dao.search(searchParam("inc_type", incType));
        if (list == null || list.size() <= 0) {
            throw new RuntimeException("系统配置错误，参数不存在");
        }
        result = list.stream().map(c -> {
            Record record = new Record();
            record.set("key", c.getName());
            record.set("value", c.getValue());
            return record;
        }).collect(Collectors.toList());
        cache.setex(CACHE_NAME + "-findList-" + incType, 60 * 60 * 24, result);
        return result;
    }

    /**
     * 根据类型，获取map数据结构的k-v
     *
     * @param incType
     * @return
     */
    public Map<String, String> findMap(String incType) {
        Map<String, String> result = cache.get(CACHE_NAME + "-findMap-" + incType);
        if (result != null) {
            return result;
        }
        List<Config> list = Config.dao.search(searchParam("inc_type", incType));
        if (list == null || list.size() <= 0) {
            throw new RuntimeException("系统配置错误，参数不存在");
        }
        result = list.stream().collect(Collectors.toMap(k -> k.getName(), v -> v.getValue()));
        cache.setex(CACHE_NAME + "-findMap-" + incType, 60 * 60 * 24, result);
        return result;
    }

    /**
     * 获得基本配置信息
     */
    public String findBasic(String name) {
        Map<String, String> basic = findMap("basic");
        return basic.get(name);
    }

    /**
     * 根据类型以及对应键获取值
     */
    public String findValue(String incType, String name) {
        Map<String, String> basic = findMap(incType);
        return basic.get(name);
    }

    public boolean update(String inc_type, String key, String value) {
        Map<String, Object> param = searchParam("inc_type", inc_type);
        param.put("name", key);
        Config config = Config.dao.searchFirst(param);
        config.setValue(String.valueOf(value));
        boolean update = config.update(false);
        if (update) {
            removeCache(inc_type);
        }
        return update;
    }

    /**
     * 清除所有缓存
     */
    private void removeCache(String incType) {
        cache.del(CACHE_NAME + "-findMap-" + incType, CACHE_NAME + "-findList-" + incType);
    }
}