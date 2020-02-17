package com.qw.service.frontend.good;

import cn.qw.base.BaseService;
import com.qw.model.Comment;
import com.qw.model.Good;
import com.qw.model.Order;
import com.qw.model.OrderGoods;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentService extends BaseService {
    private static CommentService service;

    private CommentService() {
    }

    public static synchronized CommentService me() {
        if (service == null) {
            service = new CommentService();
        }
        return service;
    }

    public Comment findByRecId(Integer recId) {
        return Comment.dao.searchFirst(searchParam("rec_id", recId));
    }

    public boolean add(Order order, OrderGoods orderGood, String content, String img, BigDecimal goodRank, Integer isAnonymous, String ip) {
        Comment comment = new Comment();
        comment.setStoreId(order.getOrderId());
        comment.setOrderSn(order.getOrderSn());
        comment.setRecId(orderGood.getRecId());
        comment.setGoodsId(orderGood.getGoodsId().intValue());
        comment.setUserId(new Long(order.getUserId()));
        comment.setGoodsRank(goodRank);
        comment.setContent(content);
        comment.setImg(img);
        // 印象标签
        comment.setImpression("");
        comment.setAddTime(System.currentTimeMillis() / 1000);
        comment.setIpAddress(ip);
        comment.setIsAnonymous(isAnonymous);
        comment.setSpecKeyName(orderGood.getSpecKeyName());
        comment.setZanNum(0);
        comment.setReplyNum(0);
        comment.setParentId(0);
        return comment.saveOrUpdate(false);
    }

    public Comment genModel(Order order, Integer goodId, String content, String[] imgarr, BigDecimal descScore, Integer anonymous, String ip) {
        OrderGoods orderGood = OrderGoods.dao.searchFirst(searchParam(searchParam("goods_id", goodId), "order_id", order.getOrderId()));

        Comment comment = new Comment();
        comment.setStoreId(order.getOrderId());
        comment.setOrderSn(order.getOrderSn());
        comment.setRecId(orderGood.getRecId());
        comment.setGoodsId(orderGood.getGoodsId().intValue());
        comment.setUserId(new Long(order.getUserId()));
        comment.setGoodsRank(descScore);
        comment.setContent(content);
        comment.setImg(Arrays.toString(imgarr));
        // 印象标签
        comment.setImpression("");
        comment.setAddTime(System.currentTimeMillis() / 1000);
        comment.setIpAddress(ip);
        comment.setIsAnonymous(anonymous);
        comment.setSpecKeyName(orderGood.getSpecKeyName());
        comment.setZanNum(0);
        comment.setReplyNum(0);
        comment.setParentId(0);
        return comment;
    }


    public Record statistic(Good good) {
        String sql = "SELECT\n" +
                "\tIFNULL(sum(case when img !='' and img not like 'N;%' then 1 else 0 end), 0) as img_sum,\n" +
                "\tIFNULL(sum(case when goods_rank >= 4 and goods_rank <= 5 then 1 else 0 end), 0) as high_sum,\n" +
                "\tIFNULL(sum(case when goods_rank >= 3 and goods_rank <4 then 1 else 0 end), 0) as center_sum,\n" +
                "\tIFNULL(sum(case when goods_rank < 3 then 1 else 0 end), 0) as low_sum,\n" +
                "\tcount(comment_id) as total_sum\n" +
                "\tFROM butler_comment WHERE is_show = 1 AND goods_id = ? AND user_id > 0 AND deleted = 0";
        Record record = Db.findFirst(sql, good.getGoodsId());
        record.set("positiveRate", "99.99%");
        return record;
    }

    public boolean thumbsUp(Comment comment, Integer userId) {
        comment.setZanNum(comment.getZanNum() + 1);
        comment.setZanUserid(comment.getZanUserid() + userId + ",");
        return comment.update(false);
    }

    public Page<Record> pageList(int pageNumber, int pageSize, Integer goodId, Integer type) {
        String select = "SELECT c.*,u.nickname,u.head_pic";
        String from = " FROM butler_comment c";
        from += " LEFT JOIN butler_user u ON u.user_id = c.user_id";
        from += " WHERE 1 = 1";
        List<Object> paras = new ArrayList<>();
        if (type == 5) {
            from += " AND c.is_show = 1 and c.goods_id = ? and c.parent_id = 0 and c.img !='' and c.deleted = 0";
            paras.add(goodId);
        } else {
            String typeArr = "";
            if (type == 1) {
                typeArr = "0,1,2,3,4,5";
            } else if (type == 2) {
                typeArr = "4,5";
            } else if (type == 3) {
                typeArr = "3";
            } else if (4 == type) {
                typeArr = "0,1,2";
            }
            from += " AND c.is_show = 1 and c.goods_id = ? and c.parent_id = 0 and c.goods_rank in(" + typeArr + ") and c.deleted = 0";
            paras.add(goodId);
        }
        from += " ORDER BY c.comment_id desc";
        Page<Record> page = Db.paginate(pageNumber, pageSize, select, from, paras.toArray());
        List<Record> list = page.getList();
        for (Record r : list) {
            // 0:是；1不是
            Integer isAnonymous = r.getInt("is_anonymous");
            if (0 == isAnonymous) {
                r.set("nickname", "匿名");
            }
            /* 图片处理 */
            String img = r.getStr("img");
            r.remove("img");
            if (StrKit.notBlank(img)) {
                img = img.replace("[", "");
                img = img.replace("]", "");
                String[] split = img.split(",");
                r.set("imgs", Arrays.stream(split).filter(s -> StrKit.notBlank(s)).map(s -> PropKit.get("fileHost") + s.trim()).toArray());
            } else {
                r.set("imgs", new ArrayList<String>());
            }
        }
        return page;
    }

    /**
     * 获取最近2条评论
     */
    public List<Record> findLatest(Good good) {
        String sql = "SELECT c.*,u.head_pic,u.nickname FROM butler_comment c LEFT JOIN butler_user u ON c.user_id = u.user_id WHERE c.goods_id = ? AND c.is_show = 1 AND c.parent_id = 0 LIMIT 2";
        List<Record> list = Db.find(sql, good.getGoodsId());
        for (Record r : list) {
            // 0:是；1不是
            Integer isAnonymous = r.getInt("is_anonymous");
            if (0 == isAnonymous) {
                r.set("nickname", "匿名");
            }

            String head_pic = r.getStr("head_pic");
            if (StrKit.notBlank(head_pic) && !head_pic.startsWith("http")) {
                r.set("head_pic", PropKit.get("fileHost") + head_pic);
            }

            String img = r.getStr("img");
            if (StrKit.notBlank(img)) {
                img = img.replace("[", "");
                img = img.replace("]", "");
                String[] split = img.split(",");
                r.set("imgs", Arrays.stream(split).filter(s -> StrKit.notBlank(s)).map(s -> PropKit.get("fileHost") + s.trim()).toArray());
            } else {
                r.set("imgs", new ArrayList<String>());
            }
            r.remove("img");
        }
        return list;
    }
}