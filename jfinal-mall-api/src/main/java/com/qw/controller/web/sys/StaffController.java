package com.qw.controller.web.sys;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import cn.qw.kit.CryptKit;
import cn.qw.kit.DateKit;
import cn.qw.plugin.event.EventKit;
import cn.qw.render.CaptchaRender;
import cn.qw.shiro.ShiroMethod;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.qw.model.Staff;
import com.qw.service.bakend.system.RoleService;
import com.qw.service.bakend.system.StaffService;
import com.qw.shiro.ShiroInterceptor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * 系统用户
 */
@RequiresAuthentication
public class StaffController extends RestController {
    /**
     * 验证码
     */
    @Clear()
    public void image() {
        CaptchaRender captchaRender = new CaptchaRender();
        setSessionAttr(CaptchaRender.CAPTCHA_KEY, captchaRender.getMd5RandonCode());
        render(captchaRender);
    }

    @Clear()
    public void login() {
        // 加盐
        String salt = HashKit.generateSalt(5);

        String username = getPara("username");
        String password = getPara("password");
        if (!StrKit.notBlank(username, password)) {
            renderParamNull("用户名、密码都不能为空");
            return;
        }
        Object objMd5RandomCode = getSessionAttr(CaptchaRender.CAPTCHA_KEY);
        String md5RandomCode = null;
        if (objMd5RandomCode != null) {
            md5RandomCode = objMd5RandomCode.toString();
            removeSessionAttr(CaptchaRender.CAPTCHA_KEY);
        }
        String captchaCode = getPara("safecode_iput");
        if (!CaptchaRender.validate(md5RandomCode, captchaCode)) {
            renderParamError("验证码错误");
            return;
        }
        Staff admin = StaffService.me().getByUserName(username);
        if (admin == null) {
            renderParamError("用户不存在");
            return;
        }
        Integer loginTimes = CacheKit.get("com.qw.ErrorLogin", username);
        int times = loginTimes != null ? loginTimes.intValue() : 0;
        if (times > 5) {
            renderParamError("您已输入错误5次，已被暂时锁定，请15分钟再试。");
            return;
        }
        if (!CryptKit.qwCry(password).equals(admin.getPassword())) {
            if (times < 5) {
                renderParamError("用户名或密码错误，错误5次后用户将被锁定15分钟。");
            } else {
                renderParamError("您已输入错误5次，已被暂时锁定，请15分钟后再试。");
            }
            CacheKit.put("com.qw.ErrorLogin", username, times + 1);
            return;
        }
        // 处理登录逻辑，保存token
        Map<String, Object> login = StaffService.me().login(admin);
        renderJson(login);
    }

    public void logout() {
        StaffService.me().logout();
        renderSuccess("退出成功");
    }

    /**
     * 获取后台用户详情，包括用户简单信息、路由信息、权限信息
     */
    public void detail() {
        Integer userId = AppIdKit.getUserId();
        Staff admin = Staff.dao.findById(userId);
        if (admin == null) {
            renderParamError("用户不存在");
            return;
        }
        Map<String, Object> login = StaffService.me().detail(admin);
        renderJson(login);
    }

    @RequiresPermissions("系统用户-查看")
    public void pageList() {
        String key = getPara("key");

        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        Page<Record> pageList = StaffService.me().pageList(pageNumber, pageSize, key);
        renderJson(pageList);
    }

    @RequiresPermissions(value = {"系统用户-新增", "系统用户-编辑"}, logical = Logical.OR)
    public void form() {
        Integer id = getParaToInt("id", 0);
        String username = getPara("username");
        String name = getPara("name");
        String phone = getPara("phone");
        Integer[] roleIds = getParaValuesToInt("roleId");

        Staff staff = Staff.dao.findById(id);
        if (staff == null) {
            staff = new Staff();
            staff.setPassword(CryptKit.qwCry("000000"));
        }
        staff.setUsername(username);
        staff.setName(name);
        staff.setPhone(phone);
        boolean validUsername = StaffService.me().validUsername(staff);
        if (!validUsername) {
            renderParamError("用户名重复");
            return;
        }
        boolean form = StaffService.me().form(staff, roleIds);
        if (form) {
            renderSuccess("保存成功");
        } else {
            renderOperateError("保存失败");
        }
    }

    @RequiresPermissions(value = {"系统用户-查看"}, logical = Logical.OR)
    public void get() {
        Integer id = getParaToInt("id");
        Staff staff = Staff.dao.findById(id);
        if (staff == null) {
            renderParamError("参数不存在");
            return;
        }
        Record record = staff.toRecord();
        record.remove("password");
        record.set("role", RoleService.me().findUserRole(id));
        record.set("roleId", new ArrayList<Integer>());
        renderJson(record);
    }

    public void changePw() {
        String password = getPara("password");
        String confirmPassword = getPara("confirmPassword");
        if (!StrKit.notBlank(password, confirmPassword)) {
            renderParamNull("密码不能为空");
            return;
        }
        if (!password.equals(confirmPassword)) {
            renderParamError("两次输入密码不一致");
            return;
        }
        Staff staff = Staff.dao.findById(AppIdKit.getUserId());
        if (staff == null) {
            renderParamError("用户信息不存在");
            return;
        }
        staff.setPassword(CryptKit.qwCry(password));
        if (staff.update(false)) {
            // 修改成功后退出登录
            if (ShiroMethod.authenticated()) {
                SecurityUtils.getSubject().logout();
            }
            renderSuccess("修改成功");
        } else {
            renderOperateError("修改失败");
        }
    }

    // 系统修改密码
    @RequiresPermissions(value = {"系统用户-重置密码"}, logical = Logical.OR)
    public void resetPw() {
        int userId = getParaToInt("userId", 0);
        Staff staff = Staff.dao.findById(userId);
        String ps = DateKit.dateToString(new Date(), "yyyyMMddHHmmss");
        staff.setPassword(CryptKit.qwCry(ps));
        if (staff.update(false)) {
            renderSuccess("密码已重置为“" + ps + "”，请妥善保管");
        } else {
            renderOperateError("保存失败");
        }
    }

    @RequiresPermissions(value = {"系统用户-删除"}, logical = Logical.OR)
    @Before(Tx.class)
    public void delete() {
        int id = getParaToInt("id", 0);
        Staff staff = Staff.dao.findById(id);
        if (staff == null) {
            renderParamError("参数不存在");
            return;
        }
        StaffService.me().delRole(staff);
        boolean delete = staff.delete();
        if (delete) {
            renderSuccess("删除成功");
        } else {
            renderOperateError("删除失败");
        }
    }
}