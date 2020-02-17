package com.qw.service.bakend.member;

import cn.qw.base.BaseService;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户管理
 * @author qw
 */
public class UserService extends BaseService {
    private static UserService service;

    private UserService() {
    }

    public static synchronized UserService me() {
        if (service == null) {
            service = new UserService();
        }
        return service;
    }

    public Page<Record> pageList(String key, String firstLeader, int pageNumber, int pageSize) {
        StringBuilder select = new StringBuilder("SELECT u.user_id userId, u.mobile,u.nickname, u.realname realName, u.head_pic headPic, FROM_UNIXTIME(u.reg_time,'%Y-%m-%d') regTime");
        select.append(",up.nickname promName");
        select.append(",ub.usable_remain, ub.withdrawable_remain, ub.consume_total, ub.withdrawable_total");
        select.append(",le.level_name");
        StringBuilder from = new StringBuilder(" FROM butler_user u");
        from.append(" LEFT JOIN butler_user up ON u.first_leader = up.user_id");
        from.append(" LEFT JOIN butler_point_sum ub ON u.user_id = ub.user_id");
        from.append(" LEFT JOIN butler_user_level le ON u.level_id = le.level_id");
        from.append(" WHERE 1 = 1");

        List<Object> paras = new ArrayList<>();
        if (!StrKit.isBlank(key)) {
            from.append(" AND u.mobile LIKE ? OR u.nickname LIKE ?");
            paras.add("%" + key + "%");
            paras.add("%" + key + "%");
        }
        if (StrKit.notBlank(firstLeader)) {
            from.append(" AND up.nickname LIKE ?");
            paras.add("%" + firstLeader + "%");
        }
        from.append(" ORDER BY u.user_id DESC");
        Page<Record> page = Db.paginate(pageNumber, pageSize, select.toString(), from.toString(), paras.toArray());
        List<Record> records = page.getList();
        records.stream().forEach(r -> {
            String headPic = r.getStr("headPic");
            if (StrKit.notBlank(headPic) && !headPic.startsWith("http")) {
                r.set("headPic", PropKit.get("fileHost") + headPic);
            }
        });
        return page;
    }
}