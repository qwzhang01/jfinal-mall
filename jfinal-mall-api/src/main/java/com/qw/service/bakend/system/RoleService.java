package com.qw.service.bakend.system;

import cn.qw.base.BaseService;
import cn.qw.kit.ArrayKit;
import com.qw.model.Operate;
import com.qw.model.Role;
import com.qw.model.Staff;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleService extends BaseService {
    private static RoleService service;

    private RoleService() {
    }

    public static synchronized RoleService me() {
        if (service == null) {
            service = new RoleService();
        }
        return service;
    }

    /**
     * 分页列表
     */
    public Page<Record> pageList(int pageNumber, int pageSize, String key) {
        String select = "SELECT * ";
        String from = " FROM oms_role WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (StrKit.notBlank(key)) {
            from += " AND roleName LIKE ?";
            paras.add("%" + key + "%");
        }
        from += " ORDER BY id DESC";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            Integer id = r.getInt("id");
            r.set("operationIds", operationIds(id));
        }
        return page;
    }

    /**
     * 获取角色详情，包括角色权限信息
     */
    public Record detail(int id) {
        Role role = Role.dao.findById(id);
        List<Integer> operationIds = operationIds(role.getId());
        Record record = role.toRecord();
        record.set("operationIds", operationIds);
        return record;
    }

    /**
     * 获取所有用户角色信息，同时判断当前用户是否已经是否拥有该角色的信息
     */
    public List<Record> findUserRole(int staffId) {
        String sql = "SELECT role.*,\n" +
                "(SELECT COUNT(*) FROM oms_staff_role sr WHERE sr.roleId = role.id AND sr.adminId = " + staffId + ") AS isChosen\n" +
                "FROM oms_role role\n" +
                "ORDER BY id ASC";

        return Db.find(sql);
    }

    public String findRoleName(int staffId) {
        String sql = "SELECT roleName FROM oms_role WHERE EXISTS (SELECT * FROM oms_staff_role WHERE oms_staff_role.roleId = oms_role.id AND oms_staff_role.adminId = ?)";
        List<String> roleName = Db.query(sql, staffId);
        if (roleName == null || roleName.size() <= 0) {
            return "";
        }
        return ArrayKit.join(roleName.toArray(), ",");
    }

    /**
     * 获取有用户的角色
     */
    public List<Role> findUserRole(Staff user) {
        String sql = "SELECT * FROM oms_role WHERE EXISTS (SELECT * FROM oms_staff_role WHERE oms_staff_role.roleId = oms_role.id AND oms_staff_role.adminId = ?)";
        return Role.dao.find(sql, user.getId());
    }

    /**
     * 获取角色权限字符串
     */
    public List<String> getRoleOperationList(Integer roleId) {
        if (roleId == null) {
            throw new RuntimeException("roleId must not be null");
        }
        return buildOperatorStr(getListByRoleId(roleId));
    }

    /**
     * 获取某一用户的所有权限信息
     */
    public List<Operate> getListByStaffId(int staffId) {
        String sql = "SELECT p.* FROM oms_staff_role sr, oms_role_operation rp, oms_operate p WHERE sr.roleId = rp.roleId AND rp.operateId = p.id AND sr.adminId = ? ORDER BY sortNo ASC, id ASC";
        return Operate.dao.find(sql, staffId);
    }

    /**
     * 获取某一角色的所有权限信息
     */
    private List<Operate> getListByRoleId(int roleId) {
        String sql = "SELECT p.* from oms_role_operation rp, oms_operate p WHERE rp.operateId = p.id AND rp.roleId = ?";
        return Operate.dao.find(sql, roleId);
    }

    /**
     * 生成权限字符串
     */
    public List<String> buildOperatorStr(List<Operate> operates) {
        List<String> permissions = new ArrayList<>();
        for (Operate operate : operates) {
            String menu = operate.getMenu();
            if (!permissions.contains(menu)) {
                permissions.add(menu);
            }
            String submenu = operate.getSubMenu();
            String permission;
            if (StrKit.notBlank(submenu)) {
                if (!permissions.contains(submenu)) {
                    permissions.add(submenu);
                }
                permission = submenu + "-";
            } else {
                permission = menu + "-";
            }
            permissions.add(permission + operate.getOperate());
        }
        return permissions;
    }

    /**
     * 生成权限树
     */
    public List<Record> operationTree() {
        String sql = "SELECT * FROM oms_operate ORDER BY id ASC";
        List<Operate> list = Operate.dao.find(sql);
        Map<String, List<Operate>> map = list.stream().collect(Collectors.groupingBy(o -> o.getMenu()));
        int[] index = {-1};
        return map.keySet().stream().map(m -> {
            Record r = new Record();
            r.set("id", index[0]--);
            r.set("label", m);
            List<Operate> sub = map.get(m);
            Map<String, List<Operate>> subMap = sub.stream().collect(Collectors.groupingBy(o -> o.getSubMenu()));
            r.set("children", subMap.keySet().stream().map(k -> {
                Record rSub = new Record();
                rSub.set("id", index[0]--);
                rSub.set("label", k);
                rSub.set("children", subMap.get(k).stream().map(ok -> {
                    Record rOperator = new Record();
                    rOperator.set("id", ok.getId());
                    rOperator.set("label", ok.getOperate());
                    return rOperator;
                }).collect(Collectors.toList()));
                return rSub;
            }).collect(Collectors.toList()));
            return r;
        }).collect(Collectors.toList());
    }

    /**
     * 获取某一角色的所有权限ID
     */
    private List<Integer> operationIds(int roleId) {
        String sql = "SELECT operateId FROM oms_role_operation WHERE roleId = ?";
        return Db.query(sql, roleId);
    }

    /**
     * 保存角色的权限信息
     */
    public boolean savePermission(int roleId, Integer[] operatorIds) {
        String delOldSql = "DELETE FROM oms_role_operation WHERE roleId = ?";
        String insertSql = "INSERT INTO oms_role_operation (roleId, operateId) VALUES ";
        for (int i = 0; i < operatorIds.length; i++) {
            if (i != 0) {
                insertSql += ",";
            }
            insertSql += "(" + roleId + "," + operatorIds[i] + ")";
        }

        String finalInsertSql = insertSql;
        return Db.tx(() -> {
            Db.update(delOldSql, roleId);
            int update = Db.update(finalInsertSql);
            return operatorIds.length == update;
        });
    }

    /**
     * 删除角色
     */
    public boolean delete(Role role) {
        // 删除用户角色
        String sqlDelUserRole = "DELETE FROM oms_staff_role WHERE roleId = ?";
        // 删除角色权限
        String sqlDelRoleOper = "DELETE FROM oms_role_operation WHERE roleId = ?";
        // 删除角色
        return Db.tx(() -> {
            Db.update(sqlDelUserRole, role.getId());
            Db.update(sqlDelRoleOper, role.getId());
            return role.delete();
        });
    }
}