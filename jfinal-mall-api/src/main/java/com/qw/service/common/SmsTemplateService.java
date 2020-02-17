package com.qw.service.common;

import cn.qw.base.BaseService;
import cn.qw.kit.DateKit;
import com.qw.helper.SmsHelper;
import com.qw.model.*;
import com.qw.service.bakend.store.StoreService;
import com.qw.service.frontend.order.OrderService;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.TemplateData;
import com.jfinal.weixin.sdk.api.TemplateMsgApi;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息管理服务，根据业务，选择模板，发送对应的消息
 * 1. 短信
 * 2. 微信公众号推送
 * 3. APP 推送
 *
 * @author qw
 */
public class SmsTemplateService extends BaseService {

    private static SmsTemplateService service;

    private SmsTemplateService() {
    }

    public static synchronized SmsTemplateService me() {
        if (service == null) {
            service = new SmsTemplateService();
        }
        return service;
    }

    /**
     * 创建订单成功
     */
    public boolean order(Order order) {
        // 获取基本信息
        User user = User.dao.findById(order.getUserId());
        List<OrderGoods> good = OrderService.me().findOrderGood(order);
        String goodName = good.stream().map(r -> r.getGoodsName()).collect(Collectors.joining(";"));
        String templateId = "EKDEZzwTbZzrb53rXRAatqSwjVEUX_5njClU_lZcx8Y";

        // 构建消息
        TemplateData msg = TemplateData.New();
        msg.setTouser(user.getOpenId());
        msg.setTemplate_id(templateId);
        msg.setUrl(PropKit.get("host") + "/#/orderinfo?orderId=" + order.getOrderId() + "&tab1=0");

        // 开头说明
        msg.add("first", "尊敬的" + user.getNickname() + "，恭喜您成功下单", "#173177");
        msg.add("keyword1", order.getMasterOrderSn(), "#173177");
        msg.add("keyword2", goodName, "#173177");
        msg.add("keyword3", String.valueOf(order.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN)), "#173177");
        msg.add("keyword4", DateKit.dateToString(order.getCreateTime(), "yyyy年MM月dd日 HH:mm"), "#173177");
        msg.add("remark", "请尽快完成支付", "#173177");
        String build = msg.build();
        ApiResult send = TemplateMsgApi.send(build);
        return send.isSucceed();
    }

    /**
     * 支付成功通知
     */
    public boolean payWx(Order order) {
        // 获取基本信息
        User user = User.dao.findById(order.getUserId());
        List<OrderGoods> good = OrderService.me().findOrderGood(order);
        String goodName = good.stream().map(r -> r.getGoodsName()).collect(Collectors.joining(";"));
        String templateId = "QzayZDyQI_WyZujJqGfr7DSUgobYZjVjg2gw2IbioWo";

        // 构建消息
        TemplateData msg = TemplateData.New();
        msg.setTouser(user.getOpenId());
        msg.setTemplate_id(templateId);
        msg.setUrl(PropKit.get("host") + "/#/orderinfo?orderId=" + order.getOrderId() + "&tab1=0");

        // 开头说明
        msg.add("first", "尊敬的" + user.getNickname() + "，您的订单已支付成功！", "#173177");
        msg.add("keyword1", goodName, "#173177");
        msg.add("keyword2", order.getMasterOrderSn(), "#173177");
        msg.add("keyword3", String.valueOf(order.getOrderAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN)), "#173177");
        msg.add("remark", "感谢您的光临~", "#173177");
        String build = msg.build();
        try {
            ApiResult send = TemplateMsgApi.send(build);
            return send.isSucceed();
        }catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 提现三个状态（-审核成功 -审核失败 -打款成功）
     */
    public boolean withdrow(PointWithdraw withdraw) {
        String templateId = "ZvBlSLLjyAUw28O_rXU5dVux-fjxcmc7kEAQ8GbDe_4";

        User user = User.dao.findById(withdraw.getUserId());
        // 构建消息
        TemplateData msg = TemplateData.New();
        msg.setTouser(user.getOpenId());
        msg.setTemplate_id(templateId);
        msg.setUrl(PropKit.get("host") + "/#/record");

        // 1待审核 2待打款 3已完成 5审核拒绝
        Integer withdrawStatus = withdraw.getWithdrawStatus();
        if (2 == withdrawStatus) {
            msg.add("first", "尊敬的" + user.getNickname() + "，您的提现系统已经审核完毕", "#173177");
            msg.add("keyword1", user.getNickname(), "#173177");
            msg.add("keyword2", DateKit.dateToString(withdraw.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), "#173177");
            msg.add("keyword3", String.valueOf(withdraw.getAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN)), "#173177");
            msg.add("remark", "提现金额会在24小时内到账，请知晓并耐心等待！", "#173177");
        } else if (3 == withdrawStatus) {
            msg.add("first", "尊敬的" + user.getNickname() + "，您的提现系统已经打款", "#173177");
            msg.add("keyword1", user.getNickname(), "#173177");
            msg.add("keyword2", DateKit.dateToString(withdraw.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), "#173177");
            msg.add("keyword3", String.valueOf(withdraw.getAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN)), "#173177");
            msg.add("remark", "具体到账时间以银行到账为准。请注意查看账户！", "#173177");
        } else if (5 == withdrawStatus) {
            msg.add("first", "尊敬的" + user.getNickname() + "，您的提现审核失败：" + withdraw.getRemark(), "#173177");
            msg.add("keyword1", user.getNickname(), "#173177");
            msg.add("keyword2", DateKit.dateToString(withdraw.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), "#173177");
            msg.add("keyword3", String.valueOf(withdraw.getAmount().setScale(2, BigDecimal.ROUND_HALF_DOWN)), "#173177");
            msg.add("remark", "请检查后重新发起申请。", "#173177");
        } else {
            throw new RuntimeException("提现通知异常，提现数据存在错误");
        }
        String build = msg.build();
        try {
            ApiResult send = TemplateMsgApi.send(build);
            return send.isSucceed();
        }catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 发送短信验证码
     *
     * @param phone
     * @param code
     */
    public void sendSmsCode(String phone, String code) {
        String telementCode = "SMS_176527352";
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        SmsHelper.send(phone, telementCode, map);
    }

    /**
     * 支付成功后，秒杀商品已经库存为0，通知客户自动退款
     */
    public void voidablePay(Order order) {
        SmsTemplate template = getByScene(7);
        if (template != null) {
            String consignee = order.getConsignee();
            String mobile = order.getMobile();
            String tplContent = template.getTplContent().replace("${name}", consignee);
            SmsHelper.send(mobile, tplContent);
        }
    }

    /**
     * 支付成功后通知商家短信
     */
    public boolean paidSms(String servicePhone, Order order) {
        SmsTemplate template = getByScene(4);
        if (template == null) {
            throw new RuntimeException("短信配置异常，付成功后通知商家短信发送失败");
        }
        String consignee = order.getConsignee();
        String mobile = order.getMobile();
        String tplContent = template.getTplContent().replace("${consignee}", consignee);
        tplContent = tplContent.replace("${phone}", mobile);
        try {
            SmsHelper.send(servicePhone, tplContent);
            return true;
        }catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    /**
     * 通知一元抢购中奖者 短信
     */
    public void lotteryWinSms(LotteryOrder lotteryOrder, LotteryGood lotteryGood) {
        SmsTemplate template = getByScene(8);
        if (template == null) {
            throw new RuntimeException("短信配置异常，拼团抽奖短信发送失败");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer date = calendar.get(Calendar.DATE);

        Good good = Good.dao.findById(lotteryGood.getGoodId());
        String goodName = good.getGoodsName();
        String storeAddress = StoreService.me().getStoreAddress(good.getStoreId());

        User user = User.dao.findById(lotteryOrder.getUserId());
        String userPhone = user.getMobile();
        //商品名称
        String tplContent = template.getTplContent().replace("${goodName}", goodName);
        //月
        tplContent = tplContent.replace("${month}", month.toString());
        //日
        tplContent = tplContent.replace("${day}", date.toString());
        //上门取件地址
        tplContent = tplContent.replace("${storeAddress}", storeAddress);
        SmsHelper.send(userPhone, tplContent);
    }

    private SmsTemplate getByScene(int scene) {
        Map<String, Object> search = new HashMap<>();
        search.put("send_scene", scene);
        return SmsTemplate.dao.searchFirst(search);
    }
}