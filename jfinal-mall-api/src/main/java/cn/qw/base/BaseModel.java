package cn.qw.base;

import cn.qw.kit.AppIdKit;
import cn.qw.kit.IdKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.ehcache.CacheKit;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BaseModel<M extends BaseModel<M>> extends Model<M> {

    private static final long serialVersionUID = 1L;

    public BaseModel() {
    }

    /**
     * 默认有四大字段，新增
     *
     * @return
     */
    @Override
    public boolean save() {
        return save(true);
    }

    /**
     * 没有四大字段
     *
     * @param has
     * @return
     */
    public boolean save(boolean has) {
        String[] keyName = TableMapping.me().getTable(getClass()).getPrimaryKey();
        if (keyName.length == 1 && get(keyName[0]) == null) {
            Method[] methods = getClass().getSuperclass().getDeclaredMethods();
            for (Method method : methods) {
                if (("get" + keyName[0]).equals(method.getName())) {
                    Class<?> clazz = method.getReturnType();
                    if ("java.lang.String".equals(clazz.getName())) {
                        set(keyName[0], IdKit.uuid());
                    } else if ("java.lang.Integer".equals(clazz.getName())) {
                        break;// 如果主键是Integer类型，则默认为自增长
                    }
                    break;
                }
            }
        }
        if (has) {
            set("create_time", new Date());
            if (StrKit.isBlank(getStr("created_by"))) {
                set("created_by", AppIdKit.getUserId());
            }
        }
        return super.save();
    }

    /**
     * 默认有四大字段，修改
     *
     * @return
     */
    @Override
    public boolean update() {
        return update(true);
    }

    /**
     * 没有四大字段
     *
     * @param has
     * @return
     */
    public boolean update(boolean has) {
        if (has) {
            set("update_time", new Date());
            if (StrKit.isBlank(getStr("update_by"))) {
                set("update_by", AppIdKit.getUserId());
            }
        }
        return super.update();
    }

    /**
     * 默认有四大字段，新增或修改
     */
    public boolean saveOrUpdate() {
        return saveOrUpdate(true);
    }

    /**
     * 没有四大字段
     *
     * @param has
     * @return
     */
    public boolean saveOrUpdate(boolean has) {
        String[] keyName = TableMapping.me().getTable(getClass()).getPrimaryKey();
        if (keyName.length == 1 && get(keyName[0]) == null) {
            return save(has);
        } else {
            return update(has);
        }
    }

    /**
     * 从缓存中通过Id查找
     *
     * @param cacheName
     * @param key
     * @param id
     * @return
     */
    public M findByIdAndCache(String cacheName, Object key, String id) {
        M result = CacheKit.get(cacheName, key);
        if (result == null) {
            result = findById(id);
            CacheKit.put(cacheName, key, result);
        }
        return result;
    }

    /**
     * 查找出表中第一个值
     *
     * @param key
     * @param value
     * @return
     */
    public M searchFirst(String cacheName, Object cacheKey, String key, Object value) {
        List<M> mList = searchByCache(cacheName, cacheKey, key, value, "");
        return mList != null && mList.size() > 0 ? mList.get(0) : null;
    }

    /**
     * 查找key和value的值放入list集合中
     *
     * @param key
     * @param value
     * @return
     */
    public List<M> search(String key, Object value) {
        return search(key, value, "");
    }

    /**
     * 按顺序查找出符合 key条件的值
     *
     * @param key
     * @param value
     * @param orderBy
     * @return
     */
    public List<M> search(String key, Object value, String orderBy) {
        String tableName = TableMapping.me().getTable(getClass()).getName();
        String sql = "select * from " + tableName + " where " + key + "=? " + orderBy;
        return find(sql, value);
    }

    /**
     * 根据缓存查询符合 key条件的值
     *
     * @param cacheName
     * @param cacheKey
     * @param key
     * @param value
     * @param orderBy
     * @return
     */
    public List<M> searchByCache(String cacheName, Object cacheKey, String key, Object value, String orderBy) {
        List<M> result = CacheKit.get(cacheName, cacheKey);
        if (result == null) {
            result = search(key, value, orderBy);
            CacheKit.put(cacheName, key, result);
        }
        return result;
    }

    /**
     * 搜索表中第一个符合条件的值
     *
     * @param key
     * @param value
     * @return
     */
    public M searchFirst(String key, Object value) {
        List<M> mList = search(key, value, "");
        return mList != null && mList.size() > 0 ? mList.get(0) : null;
    }

    /**
     * 搜索表中第一个符合条件的值并缓存
     *
     * @param cacheName
     * @param cacheKey
     * @param key
     * @param value
     * @return
     */
    public M searchFirstByCache(String cacheName, Object cacheKey, String key, Object value) {
        List<M> mList = searchByCache(cacheName, cacheKey, key, value, "");
        return mList != null && mList.size() > 0 ? mList.get(0) : null;
    }

    /**
     * 搜索表中第一个符合条件的值并缓存
     *
     * @param maps
     * @return
     */
    public M searchFirst(Map<String, Object> maps) {
        List<M> mList = search(maps, "");
        return mList != null && mList.size() > 0 ? mList.get(0) : null;
    }

    /**
     * 搜索表中第一个符合条件的值并缓存
     *
     * @param maps
     * @return
     */
    public M searchFirstByCache(String cacheName, Object key, Map<String, Object> maps) {
        List<M> mList = searchByCache(cacheName, key, maps, "");
        return mList != null && mList.size() > 0 ? mList.get(0) : null;
    }

    /**
     * 查询的值放到list集合中
     *
     * @param maps
     * @return
     */
    public List<M> search(Map<String, Object> maps) {
        return search(maps, "");
    }

    /**
     * 按照条件查询，将查询的值放入list集合中
     *
     * @param maps
     * @param orderBy
     * @return
     */
    public List<M> search(Map<String, Object> maps, String orderBy) {
        String tableName = TableMapping.me().getTable(getClass()).getName();
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ").append(tableName).append(" where 1=1 ");
        List<Object> values = new ArrayList<Object>();
        for (Entry<String, Object> entry : maps.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(" and ").append(entry.getKey()).append("=?");
                values.add(entry.getValue());
            }
        }
        sb.append(" ").append(orderBy);
        return find(sb.toString(), values.toArray());
    }

    /**
     * 查询缓存
     *
     * @param cacheName
     * @param key
     * @param maps
     * @return
     */
    public List<M> searchByCache(String cacheName, Object key, Map<String, Object> maps) {
        return this.searchByCache(cacheName, key, maps, "");
    }

    /**
     * 查询缓存
     *
     * @param cacheName
     * @param key
     * @param maps
     * @param orderBy
     * @return
     */
    public List<M> searchByCache(String cacheName, Object key, Map<String, Object> maps, String orderBy) {
        List<M> result = CacheKit.get(cacheName, key);
        if (result == null) {
            result = search(maps, orderBy);
            CacheKit.put(cacheName, key, result);
        }
        return result;
    }

    /**
     * 进行分页查询
     *
     * @param pageNumber
     * @param pageSize
     * @param maps
     * @return
     */
    public Page<M> searchPaginate(int pageNumber, int pageSize, Map<String, Object> maps) {
        return this.searchPaginate(pageNumber, pageSize, maps, "");
    }

    /**
     * 进行分页查询按照排列显示
     *
     * @param pageNumber
     * @param pageSize
     * @param maps
     * @param orderBy
     * @return
     */
    public Page<M> searchPaginate(int pageNumber, int pageSize, Map<String, Object> maps, String orderBy) {
        String tableName = TableMapping.me().getTable(getClass()).getName();
        StringBuilder sb = new StringBuilder();
        sb.append("from ").append(tableName).append(" where 1=1 ");
        List<Object> values = new ArrayList<Object>();
        for (Entry<String, Object> entry : maps.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(" and ").append(entry.getKey()).append("=? ");
                values.add(entry.getValue());
            }
        }
        sb.append(orderBy);
        return paginate(pageNumber, pageSize, "select *", sb.toString(), values.toArray());
    }

    /**
     * 在缓存中查找对应的值分页显示
     *
     * @param cacheName
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param maps
     * @return
     */
    public Page<M> searchPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize,
                                         Map<String, Object> maps) {
        return this.searchPaginateByCache(cacheName, key, pageNumber, pageSize, maps, "");
    }

    /**
     * 在缓存中查找对应的值并安顺序进行显示
     *
     * @param cacheName
     * @param key
     * @param pageNumber
     * @param pageSize
     * @param maps
     * @param orderBy
     * @return
     */
    public Page<M> searchPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize,
                                         Map<String, Object> maps, String orderBy) {
        Page<M> result = CacheKit.get(cacheName, key);
        if (result == null) {
            result = searchPaginate(pageNumber, pageSize, maps, orderBy);
            CacheKit.put(cacheName, key, result);
        }
        return result;
    }

    /**
     * 获取所有的
     *
     * @param orderBy
     * @return
     */
    public List<M> getList(String orderBy) {
        String tableName = TableMapping.me().getTable(getClass()).getName();
        String sql = "select * from " + tableName + " " + orderBy;
        return find(sql);
    }
}
