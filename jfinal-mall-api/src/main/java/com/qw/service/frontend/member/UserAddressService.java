package com.qw.service.frontend.member;

import cn.qw.base.BaseService;
import cn.qw.kit.AppIdKit;
import com.qw.model.Region;
import com.qw.model.UserAddress;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

/**
 * 用户地址
 * @author qw
 */
public class UserAddressService extends BaseService {
    private static UserAddressService service;

    private UserAddressService() {
    }

    public static synchronized UserAddressService me() {
        if (service == null) {
            service = new UserAddressService();
        }
        return service;
    }

    /**
     * 获取用户地址信息
     */
    public List<UserAddress> getAddress() {
        Integer userId = AppIdKit.getUserId();
        return UserAddress.dao.search(searchParam("user_id", userId));
    }

    public UserAddress fixRegion(UserAddress userAddress, Region region) {
        Integer level = region.getLevel();
        if (level == 4) {
            userAddress.setTwon(region.getId().intValue());
        }
        if (level == 3) {
            userAddress.setDistrict(region.getId().intValue());
        }
        if (level == 2) {
            userAddress.setCity(region.getId().intValue());
        }
        if (level == 1) {
            userAddress.setProvince(region.getId().intValue());
            return userAddress;
        } else {
            region = Region.dao.findById(region.getParentId());
            return fixRegion(userAddress, region);
        }
    }

    public UserAddress clearRegion(UserAddress userAddress, Region region) {
        if (region.getLevel() == 3) {
            userAddress.setTwon(0);
        }
        if (region.getLevel() == 2) {
            userAddress.setTwon(0);
            userAddress.setDistrict(0);
        }
        if (region.getLevel() == 1) {
            userAddress.setTwon(0);
            userAddress.setDistrict(0);
            userAddress.setCity(0);
        }
        return userAddress;
    }

    public void cancelDefault(UserAddress userAddress) {
        String sql = "UPDATE butler_user_address SET is_default = 0 WHERE is_default = 1 AND user_id = ? AND address_id <> ?";
        Db.update(sql, userAddress.getUserId(), userAddress.getAddressId());
    }

    /**
     * 获取用户默认收货地址
     * 1. 候选获取默认
     * 2. 如果没有设置默认地址，选择最新添加的地址
     */
    public UserAddress getDefault() {
        Integer userId = AppIdKit.getUserId();
        String sql = "SELECT * FROM butler_user_address WHERE user_id = ? AND is_default = 1 LIMIT 1";
        UserAddress address = UserAddress.dao.findFirst(sql, userId);
        if (address == null) {
            sql = "SELECT * FROM butler_user_address WHERE user_id = ? ORDER BY address_id DESC LIMIT 1";
            address = UserAddress.dao.findFirst(sql, userId);
        }
        return address;
    }
}