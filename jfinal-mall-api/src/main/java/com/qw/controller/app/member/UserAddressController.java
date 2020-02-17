package com.qw.controller.app.member;

import cn.qw.base.RestController;
import cn.qw.kit.AppIdKit;
import com.qw.model.Region;
import com.qw.model.UserAddress;
import com.qw.service.common.RegionService;
import com.qw.service.frontend.member.UserAddressService;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 收货地址
 */
public class UserAddressController extends RestController {

    /**
     * @title 获取收货地址
     * @respBody {"status":"0","data":"", "msg":"请求成功"}
     * @respParam address|地址|String|必填
     * @respParam address_id|地址ID|String|必填
     * @respParam city|要求人下单总数|String|必填
     * @respParam cityed|要求人下单总数|String|必填
     * @respParam consignee|要求人下单总数|String|必填
     * @respParam country|要求人下单总数|String|必填
     * @respParam district|要求人下单总数|String|必填
     * @respParam districted|要求人下单总数|String|必填
     * @respParam email|要求人下单总数|String|必填
     * @respParam is_default|要求人下单总数|String|必填
     * @respParam minId|要求人下单总数|String|必填
     * @respParam mobile|要求人下单总数|String|必填
     * @respParam province|要求人下单总数|String|必填
     * @respParam provinceed|要求人下单总数|String|必填
     * @respParam twon|要求人下单总数|String|必填
     * @respParam twoned|要求人下单总数|String|必填
     * @respParam user_id|要求人下单总数|String|必填
     * @respParam zipcode|要求人下单总数|String|必填
     */
    public void getAddress() {
        // 获取用户地址
        List<UserAddress> address = UserAddressService.me().getAddress();
        if (address == null) {
            renderSuccess("获取成功");
            return;
        }
        List<Record> list = address.stream().map(d -> {
            Record record = d.toRecord();
            record.set("provinceed", RegionService.me().name(d.getProvince()));
            record.set("cityed", RegionService.me().name(d.getCity()));
            record.set("districted", RegionService.me().name(d.getDistrict()));
            record.set("twoned", RegionService.me().name(d.getTwon()));
            record.set("minId", d.getTwon() != 0 ? d.getTwon() : d.getDistrict() != 0 ? d.getDistrict() : d.getCity() != 0 ? d.getCity() : d.getProvince() != 0 ? d.getProvince() : "");
            return record;
        }).collect(Collectors.toList());

        renderJson(list);
    }

    /**
     * @param addressSonId|最低子级id|int|必填
     * @param mobile|电话|String|必填
     * @param is_default|默认收货地址(0/1)|int|必填
     * @param consignee|收货人|String|必填
     * @param address|详细地址|String|必填
     * @title 增加地址
     */
    @Before({Tx.class})
    public void addAddress() {
        // 最低子级id
        int addressSonId = getParaToInt("addressSonId", 0);
        // 电话
        String mobile = getPara("mobile");
        // 默认收货地址
        int is_default = getParaToInt("is_default", 0);// 0/1
        // 收货人
        String consignee = getPara("consignee", "");
        // 详细地址
        String address = getPara("address");

        if (!StrKit.notBlank(consignee, mobile, address)) {
            renderParamNull("收货人、电话号码、地址详情都不能为空");
            return;
        }
        Region region = Region.dao.findById(addressSonId);
        if (region == null) {
            renderParamError("地区信息错误，数据不存在");
            return;
        }
        UserAddress userAddress = new UserAddress();
        userAddress.setAddress(address);
        userAddress.setConsignee(consignee);
        userAddress.setMobile(mobile);
        userAddress.setIsDefault(is_default);
        userAddress.setUserId(AppIdKit.getUserId());

        userAddress = UserAddressService.me().fixRegion(userAddress, region);

        if (userAddress.save(false)) {
            if (is_default == 1) {
                UserAddressService.me().cancelDefault(userAddress);
            }
            renderSuccess("添加成功！");
        } else {
            renderOperateError("添加失败！");
        }
    }

    /**
     * @param address_id|地址|int|必填
     * @param addressSonId|最低子级id|int|必填
     * @param mobile|电话|String|必填
     * @param is_default|默认收货地址(0/1)|int|必填
     * @param consignee|收货人|String|必填
     * @param address|详细地址|String|必填
     * @title 修改地址
     */
    @Before({Tx.class})
    public void modifyAddress() {
        int addressId = getParaToInt("address_id", 0);
        // 最低子级id
        int addressSonId = getParaToInt("addressSonId", 0);
        // 电话
        String mobile = getPara("mobile");
        // 默认收货地址
        int is_default = getParaToInt("is_default", 0);// 0/1是默认
        // 收货人
        String consignee = getPara("consignee", "");
        // 详细地址
        String address = getPara("address");

        UserAddress userAddress = UserAddress.dao.findById(addressId);
        if (userAddress == null) {
            renderParamError("参数缺失，请检查");
            return;
        }
        if (!StrKit.notBlank(consignee, mobile, address)) {
            renderParamNull("收货人、电话号码、地址详情都不能为空");
            return;
        }
        if (!userAddress.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("只能修改自己的收货地址");
            return;
        }
        Region region = Region.dao.findById(addressSonId);
        if (region == null) {
            renderParamError("地区信息错误，数据不存在");
            return;
        }
        userAddress = UserAddressService.me().clearRegion(userAddress, region);
        userAddress = UserAddressService.me().fixRegion(userAddress, region);

        userAddress.setAddress(address);
        userAddress.setConsignee(consignee);
        userAddress.setMobile(mobile);
        userAddress.setIsDefault(is_default);

        if (userAddress.update(false)) {
            if (is_default == 1) {
                UserAddressService.me().cancelDefault(userAddress);
            }
            renderSuccess("修改成功！");
        } else {
            renderOperateError("修改失败！");
        }
    }

    /**
     * @param address_id|地址|int|必填
     * @title 删除地址
     */
    public void delAddress() {
        Integer addressId = getParaToInt("address_id", 0);
        UserAddress address = UserAddress.dao.findById(addressId);
        if (address == null) {
            renderParamNull("地址信息不存在");
            return;
        }
        if (!address.getUserId().equals(AppIdKit.getUserId())) {
            renderParamError("只能删除自己的收货地址");
            return;
        }
        boolean delete = address.delete();
        if (delete) {
            renderSuccess("删除成功！");
        } else {
            renderParamError("删除失败！");
        }
    }
}