package com.qw.controller.web.sys;

import cn.qw.base.RestController;
import com.qw.model.Role;
import com.qw.service.bakend.system.RoleService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.List;

/**
 * 后台角色管理
 */
@RequiresAuthentication
@RequiresPermissions("系统角色")
public class RoleController extends RestController {

    @RequiresPermissions("系统角色-查看")
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);
        String key = getPara("key");

        Page<Record> pageList = RoleService.me().pageList(pageNumber, pageSize, key);
        renderJson(pageList);
    }

    @RequiresPermissions("系统角色-查看")
    public void getById() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("ID不能为空");
            return;
        }
        renderJson(RoleService.me().detail(id));
    }

    @RequiresPermissions(value = {"系统角色-新增", "系统角色-编辑"}, logical = Logical.OR)
    public void save() {
        int id = getParaToInt("id", 0);
        String roleName = getPara("roleName");
        String desc = getPara("desc");
        if (!StrKit.notBlank(roleName, desc)) {
            renderParamError("参数不能为空");
            return;
        }
        Role role = new Role();
        if (id != 0) {
            role.setId(id);
        }
        role.setRoleName(roleName);
        role.setDesc(desc);
        boolean saveOrUpdate = role.saveOrUpdate(false);
        if (saveOrUpdate) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    @RequiresPermissions("系统角色-删除")
    public void delete() {
        int id = getParaToInt("id", 0);
        if (id == 0) {
            renderParamNull("ID不能为空");
            return;
        }
        Role role = Role.dao.findById(id);
        if (role == null) {
            renderParamError("角色不存在");
            return;
        }
        boolean delete = RoleService.me().delete(role);
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }

    /**
     * @title 获取用户角色信息
     */
    public void userRole() {
        int staffId = getParaToInt("staffId", 0);
        List<Record> list = RoleService.me().findUserRole(staffId);
        renderJson(list);
    }

    /**
     * 获取所有的权限树
     */
    @RequiresPermissions("系统角色-查看")
    public void operatorTree() {
        renderJson(RoleService.me().operationTree());
    }

    @RequiresPermissions("系统角色-设置权限")
    public void savePermission() {
        int roleId = getParaToInt("roleId", 0);
        Integer[] operatorIds = getParaValuesToInt("operatorId");
        if (roleId == 0) {
            renderParamNull("roleId不能为空");
            return;
        }
        if (operatorIds == null || operatorIds.length <= 0) {
            renderParamNull("operatorIds不能为空");
            return;
        }
        boolean savePermission = RoleService.me().savePermission(roleId, operatorIds);
        if (savePermission) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }
}