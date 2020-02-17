package com.qw.controller.web.order;

import cn.qw.base.RestController;
import com.qw.model.DoorServer;
import com.qw.model.ServiceWorker;
import com.qw.service.bakend.order.DoorServiceService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import java.util.Date;

/**
 * 上门服务
 */
@RequiresAuthentication
public class DoorServiceController extends RestController {

    /**
     * 上门安装服务 分页列表
     */
    public void pageList() {
        int pageNumber = getParaToInt("pageNumber", 1);
        int pageSize = getParaToInt("pageSize", 10);

        String i_name = getPara("i_name");
        String master_order_sn = getPara("master_order_sn");
        String order_sn = getPara("order_sn");

        Page<Record> pageList = DoorServiceService.me().pageList(pageNumber, pageSize, i_name, master_order_sn, order_sn);
        renderJson(pageList);
    }

    /**
     * 分配上门服务人员
     */
    public void assignWork() {
        // 获取参数
        int id = getParaToInt("i_id", 0);
        int workerId = getParaToInt("workerId", 0);
        String address = getPara("address");
        Date dateStart = getParaToDate("dateStart");
        Date dateEnd = getParaToDate("dateEnd");
        // 校验参数
        DoorServer doorServer = DoorServer.dao.findById(id);
        if (doorServer == null) {
            renderParamError("服务记录不存在");
            return;
        }
        // 状态（0：待安装，1：已安装，2：已分派，3已取消）
        Integer iActive = doorServer.getIActive();
        if (iActive == 1) {
            renderParamError("已完成的无法分配服务员");
            return;
        }
        ServiceWorker worker = ServiceWorker.dao.findById(workerId);
        if (worker == null) {
            renderParamNull("服务员ID不能为空");
            return;
        }
        if (dateStart == null || dateEnd == null) {
            renderParamError("预约时间不能为空");
            return;
        }
        if (!StrKit.notBlank(address)) {
            renderParamNull("服务地址不能为空");
            return;
        }

        doorServer.setWorkerId(workerId);
        doorServer.setIName(worker.getName());
        doorServer.setIPhone(worker.getPhone());
        doorServer.setIStartTime(dateStart);
        doorServer.setIEndTime(dateEnd);
        doorServer.setIAddress(address);
        doorServer.setIActive(2);
        doorServer.setAppointTime(new Date());
        doorServer.update(false);

        renderSuccess("分配成功");
    }

    /**
     * 取消分配服务人员
     */
    public void cancelAssign() {
        int id = getParaToInt("i_id", 0);
        DoorServer doorServer = DoorServer.dao.findById(id);
        if (doorServer == null) {
            renderParamError("服务记录不存在");
            return;
        }
        // 状态（0：待安装，1：已安装，2：已分派，3已取消）
        Integer iActive = doorServer.getIActive();
        if (iActive != 2) {
            renderParamError("不是已分派状态，无法取消分配");
            return;
        }
        doorServer.setIActive(3);
        doorServer.update(false);
        renderSuccess("分配成功");
    }

    /**
     * 完成上门服务
     */
    public void done() {
        int id = getParaToInt("i_id", 0);
        DoorServer doorServer = DoorServer.dao.findById(id);
        if (doorServer == null) {
            renderParamError("服务记录不存在");
            return;
        }
        // 状态（0：待安装，1：已安装，2：已分派，3已取消）
        Integer iActive = doorServer.getIActive();
        if (iActive != 2) {
            renderParamError("未分配的记录，无法完成");
            return;
        }
        doorServer.setIActive(1);
        doorServer.update(false);
        renderSuccess("保存成功");
    }
}