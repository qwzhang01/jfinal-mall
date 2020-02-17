package com.qw.service.bakend.system;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.ArrayKit;
import cn.qw.kit.CryptKit;
import cn.qw.shiro.ShiroMethod;
import com.qw.conf.QuantityConf;
import com.qw.model.Operate;
import com.qw.model.Staff;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import java.util.*;

/**
 * 系统用户管理
 */
public class StaffService extends BaseService {
    private static StaffService service;
    private Cache cache;

    private StaffService() {
        this.cache = Redis.use(QuantityConf.TOKEN_REDIS);
    }

    public static synchronized StaffService me() {
        if (service == null) {
            service = new StaffService();
        }
        return service;
    }

    public Staff getByUserName(String username) {
        return Staff.dao.searchFirst(searchParam("username", username));
    }

    public Map<String, Object> login(Staff admin) {

        // shire 登录
        UsernamePasswordToken token = new UsernamePasswordToken(admin.getUsername(), admin.getPassword());
        // token.setRememberMe(true);
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(token);

        // redis 保存登录信息
        String accessToken = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        String userKey = CACHE_NAME + admin.getId();
        // 登录保存时间为1天
        cache.setex(userKey, 60 * 60 * 8, CryptKit.cymd5(CryptKit.cyCry(accessToken)));

        Map<String, Object> map = new HashMap<>();
        map.put("accessToken", accessToken);
        map.put("name", admin.getName());
        map.put("phone", admin.getPhone());
        map.put("userId", admin.getId());
        return map;
    }

    public boolean logout() {
        if (ShiroMethod.authenticated()) {
            SecurityUtils.getSubject().logout();
        }
        Integer userId = AppIdKit.getUserId();
        String userKey = CACHE_NAME + userId;
        Long del = cache.del(userKey);
        return del == 1;
    }

    public boolean validToken(String accessToken, Integer userId) {
        if (userId == 0) {
            return false;
        }
        String userIdKey = CACHE_NAME + userId;
        boolean valid = CryptKit.cymd5(CryptKit.cyCry(accessToken)).equals(cache.get(userIdKey));
        if (!valid) {
            cache.del(userIdKey);
        }
        return valid;
    }

    public Page<Record> pageList(int pageNumber, int pageSize, String key) {
        String select = "SELECT id, name, phone, username";
        String from = " FROM oms_staff WHERE username <> 'admin'";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from += " AND(username LIKE ? OR name LIKE ? OR phone LIKE ?)";
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        from += " ORDER BY id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            r = setRoleName(r);
        }
        return page;
    }

    private Record setRoleName(Record r) {
        Integer id = r.getInt("id");
        String roleName = RoleService.me().findRoleName(id);
        r.set("roleName", roleName);
        return r;
    }

    public boolean form(Staff staff, Integer[] roleId) {
        return Db.tx(() -> staff.saveOrUpdate(false)
                && delRole(staff)
                && addRole(roleId, staff));
    }

    private boolean addRole(Integer[] roleId, Staff staff) {
        String sql = "INSERT INTO oms_staff_role (roleId, adminId) VALUES";
        sql += ArrayKit.join(Arrays.stream(roleId).map(rId -> "(" + rId + ", " + staff.getId() + ")").toArray());
        int update = Db.update(sql);
        return update == roleId.length;
    }

    public boolean delRole(Staff staff) {
        String sql = "DELETE FROM oms_staff_role WHERE adminId = ?";
        Db.update(sql, +staff.getId());
        return true;
    }

    public boolean validUsername(Staff staff) {
        String sql = "SELECT COUNT(*) FROM oms_staff WHERE username = ? AND id <> ?";
        int id = 0;
        if (staff.getId() != null) {
            id = staff.getId();
        }
        return Db.queryLong(sql, staff.getUsername(), id) == 0;
    }

    public Map<String, Object> detail(Staff admin) {
        Map<String, Object> staff = new HashMap<>();
        // 获取基本用户信息
        staff.put("name", admin.getName());
        staff.put("phone", admin.getPhone());
        staff.put("username", admin.getUsername());
        // 获取用户的路由信息（权限信息）
        // 获取用户的权限判断字符串
        List<Operate> operateList = RoleService.me().getListByStaffId(admin.getId());
        staff.put("permission", RoleService.me().buildOperatorStr(operateList));
        return staff;
    }
}