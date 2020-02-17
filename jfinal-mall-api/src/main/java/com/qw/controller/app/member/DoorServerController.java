package com.qw.controller.app.member;

import cn.qw.base.RestController;
import com.qw.model.DoorServer;
import com.qw.service.frontend.order.DoorServerService;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

import java.util.Date;

/**
 * 上门服务进度
 */
public class DoorServerController extends RestController {

    /**
     * 获取上门安装需求
     *
     * @param orderId|订单ID|int|必填
     * @param goodId|商品ID|int|必填
     * @title 获取上门安装需求
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam appointEndTime|预约区间开始值|String|非必填
     * @respParam appointStartime|预约区间结束值|String|非必填
     * @respParam workerName|上门服务工人姓名|String|非必填
     * @respParam workerPhone|上门服务工人手机|String|非必填
     * @respParam serverStatus|上门服务状态（0：待安装，1：已安装，2：已分派，3已取消）|int|必填
     * @respParam doneTime|完成时间|String|非必填
     * @respParam appointTime|预约时间|String|非必填
     */
    public void detail() {
        Integer orderId = getParaToInt("orderId", 0);
        Integer goodId = getParaToInt("goodId", 0);
        if (orderId == 0 || goodId == 0) {
            renderParamError("订单ID和商品ID都不能为空");
            return;
        }
        Record detail = DoorServerService.me().getDetail(orderId, goodId);
        if (detail == null) {
            renderParamError("参数错误，该订单不存在上门服务");
            return;
        }
        renderJson(detail);
    }

    /**
     * 预约上门安装 post body json
     *
     * @param orderId|订单ID|int|必填
     * @param goodId|商品ID|int|必填
     * @param address|上门服务地址|String|必填
     * @param appointTimeBegin|预约区间开始值|String|必填
     * @param appointTimeEnd|预约区间结束值|String|必填
     * @title 预约上门安装
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void appoint() {
        // 获取参数
        Record form = getRecord();
        Integer orderId = form.getInt("orderId");
        Integer goodId = form.getInt("goodId");
        String address = form.getStr("address");
        Date appointTimeBegin = form.getDate("appointTimeBegin");
        Date appointTimeEnd = form.getDate("appointTimeEnd");
        // 参数校验
        if (orderId == null || orderId == 0) {
            renderParamNull("订单ID不能为空");
            return;
        }
        if (goodId == null || goodId == 0) {
            renderParamNull("商品ID不能为空");
            return;
        }
        if (StrKit.isBlank(address)) {
            renderParamNull("服务地址不能为空");
            return;
        }
        if (appointTimeBegin == null || appointTimeEnd == null) {
            renderParamNull("预约时间不能为空");
            return;
        }
        DoorServer doorServer = DoorServerService.me().getByOrderId(orderId, goodId);
        if (doorServer == null) {
            renderParamError("参数错误，该订单不存在上门服务数据");
            return;
        }
        doorServer.setIActive(0);
        doorServer.setAppointTime(new Date());
        doorServer.setIAddress(address);
        doorServer.setIStartTime(appointTimeBegin);
        doorServer.setIEndTime(appointTimeEnd);
        boolean update = doorServer.update();
        if (update) {
            renderSuccess("预约成功");
        } else {
            renderOperateError("预约失败");
        }
    }

    /**
     * 取消上门安装
     *
     * @param orderId|订单ID|int|必填
     * @param goodId|商品ID|int|必填
     * @title 取消上门安装
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     */
    public void cancel() {
        Integer orderId = getParaToInt("orderId", 0);
        Integer goodId = getParaToInt("goodId", 0);
        if (orderId == 0 || goodId == 0) {
            renderParamNull("订单ID和商品ID都不能为空");
            return;
        }

        Record detail = DoorServerService.me().getDetail(orderId, goodId);
        if (detail == null) {
            renderParamError("参数错误，该订单不存在上门服务");
            return;
        }
        // 0：待预约，1：已安装，2：已分派，3已取消
        Integer serverStatus = detail.getInt("serverStatus");
        if (serverStatus != 0) {
            renderParamError("订单状态无法取消预约");
            return;
        }
        Integer id = detail.getInt("id");
        boolean cancel = DoorServerService.me().cancel(id, orderId, goodId);
        if (cancel) {
            renderSuccess("取消成功");
        } else {
            renderOperateError("取消失败");
        }
    }

    /**
     * 获取预约时间下拉数据
     *
     * @param day|日期（为空返回日期数组，不为空返回时间数组）|String|非必填
     * @title 获取预约时间下拉数据
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam date|显示日期|String|必填
     * @respParam time|显示时间数组|array|必填
     * @respParam showContent|显示时间|String|必填
     * @respParam appointTimeBegin|预约开始时间|datetime|非必填
     * @respParam appointTimeEnd|预约结束时间|datetime|非必填
     */
    public void appointTimeParam() {
        String day = getPara("day");
        if (StrKit.isBlank(day)) {
            renderJson(DoorServerService.me().getAppointDay());
        } else {
            renderJson(DoorServerService.me().getAppointTime(day));
        }
    }
}