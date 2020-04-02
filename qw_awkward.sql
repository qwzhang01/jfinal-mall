/*
Navicat MySQL Data Transfer

Source Server         : 192.168.31.113
Source Server Version : 50727
Source Host           : 192.168.31.113:3306
Source Database       : butler_dev

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-04-02 19:09:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for butler_article
-- ----------------------------
DROP TABLE IF EXISTS `butler_article`;
CREATE TABLE `butler_article` (
  `article_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `cat_id` smallint(5) NOT NULL DEFAULT '0' COMMENT '类别ID',
  `title` varchar(150) NOT NULL DEFAULT '' COMMENT '文章标题',
  `content` longtext NOT NULL,
  `author` varchar(30) NOT NULL DEFAULT '' COMMENT '文章作者',
  `author_email` varchar(60) NOT NULL DEFAULT '' COMMENT '作者邮箱',
  `keywords` varchar(255) NOT NULL DEFAULT '' COMMENT '关键字',
  `article_type` tinyint(1) unsigned NOT NULL DEFAULT '2',
  `is_open` tinyint(1) unsigned NOT NULL DEFAULT '1',
  `add_time` int(10) unsigned NOT NULL DEFAULT '0',
  `file_url` varchar(255) NOT NULL DEFAULT '' COMMENT '附件地址',
  `open_type` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `link` varchar(255) NOT NULL DEFAULT '' COMMENT '链接地址',
  `description` mediumtext COMMENT '文章摘要',
  `click` int(11) DEFAULT '0' COMMENT '浏览量',
  `publish_time` int(11) DEFAULT NULL COMMENT '文章预告发布时间',
  `thumb` varchar(200) NOT NULL DEFAULT '' COMMENT '文章缩略图',
  PRIMARY KEY (`article_id`) USING BTREE,
  KEY `cat_id` (`cat_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='富文本内容';

-- ----------------------------
-- Table structure for butler_article_cat
-- ----------------------------
DROP TABLE IF EXISTS `butler_article_cat`;
CREATE TABLE `butler_article_cat` (
  `cat_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `cat_name` varchar(20) DEFAULT NULL COMMENT '类别名称',
  `cat_type` smallint(6) DEFAULT '0' COMMENT '默认分组',
  `parent_id` smallint(6) DEFAULT '0' COMMENT '夫级ID',
  `show_in_nav` tinyint(1) DEFAULT '0' COMMENT '是否导航显示',
  `sort_order` smallint(6) DEFAULT '50' COMMENT '排序',
  `cat_desc` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `keywords` varchar(30) DEFAULT NULL COMMENT '搜索关键词',
  `cat_alias` varchar(20) DEFAULT NULL COMMENT '别名',
  PRIMARY KEY (`cat_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_brand
-- ----------------------------
DROP TABLE IF EXISTS `butler_brand`;
CREATE TABLE `butler_brand` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '品牌表',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT '品牌名称',
  `logo` varchar(80) NOT NULL DEFAULT '' COMMENT '品牌logo',
  `desc` text NOT NULL COMMENT '品牌描述',
  `url` varchar(200) NOT NULL DEFAULT '' COMMENT '品牌地址（字段作废）',
  `category_id` int(11) NOT NULL DEFAULT '0' COMMENT '分类ID',
  `sort` int(11) unsigned NOT NULL DEFAULT '50' COMMENT '排序',
  `is_hot` int(11) NOT NULL DEFAULT '0' COMMENT '是否推荐（0否 1是）',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0正常 1审核中 2审核失败 审核状态',
  `cat_name` varchar(128) DEFAULT '' COMMENT '品牌分类（字段作废）',
  `cat_id1` int(11) DEFAULT '0' COMMENT '一级分类id（字段作废）',
  `cat_id2` int(11) DEFAULT '0' COMMENT '二级分类id（字段作废）',
  `cat_id3` int(11) DEFAULT '0' COMMENT '三级分类id（字段作废）',
  `store_id` int(11) DEFAULT '0' COMMENT '商家ID（字段作废）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `category_id` (`category_id`) USING BTREE,
  KEY `sort` (`sort`) USING BTREE,
  KEY `is_hot` (`is_hot`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_cart
-- ----------------------------
DROP TABLE IF EXISTS `butler_cart`;
CREATE TABLE `butler_cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '购物车信息ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `session_id` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'session',
  `item_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格ID',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `goods_sn` varchar(100) NOT NULL DEFAULT '' COMMENT '商品货号',
  `goods_name` varchar(100) NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `spec_key` varchar(64) DEFAULT '' COMMENT '商品规格key 对应butler_spec_goods_price 表',
  `spec_key_name` varchar(64) DEFAULT '' COMMENT '商品规格组合名称',
  `add_time` int(11) DEFAULT '0' COMMENT '加入购物车的时间',
  `store_id` int(10) DEFAULT '0' COMMENT '商家店铺ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `session_id` (`session_id`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1237 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='购物车表';

-- ----------------------------
-- Table structure for butler_comment
-- ----------------------------
DROP TABLE IF EXISTS `butler_comment`;
CREATE TABLE `butler_comment` (
  `comment_id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id自增',
  `goods_id` int(8) NOT NULL DEFAULT '0' COMMENT '商品id',
  `order_id` int(8) NOT NULL DEFAULT '0' COMMENT '订单id',
  `rec_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单商品id',
  `store_id` int(10) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `user_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `content` text COMMENT '评论内容',
  `add_time` int(11) unsigned DEFAULT NULL COMMENT '评论时间',
  `ip_address` varchar(15) NOT NULL DEFAULT '' COMMENT '评论ip地址',
  `is_show` int(1) unsigned NOT NULL DEFAULT '1' COMMENT '是否显示;0:不显示；1:显示',
  `img` text COMMENT '晒单图片',
  `spec_key_name` varchar(255) NOT NULL DEFAULT '',
  `goods_rank` decimal(2,1) NOT NULL DEFAULT '0.0' COMMENT '商品评价等级，好 中 差',
  `zan_num` int(10) DEFAULT '0' COMMENT '点赞人数',
  `zan_userid` varchar(255) NOT NULL DEFAULT '',
  `reply_num` int(10) DEFAULT NULL COMMENT '评论回复数',
  `is_anonymous` int(1) DEFAULT '0' COMMENT '是否匿名评价0:是；1不是',
  `impression` varchar(50) DEFAULT NULL COMMENT '印象标签',
  `deleted` int(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '假删除标识;1:删除,0:未删除',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '上级评论ID',
  `order_sn` varchar(20) DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`comment_id`) USING BTREE,
  KEY `id_value` (`goods_id`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=222 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品评价表';

-- ----------------------------
-- Table structure for butler_coupon
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon`;
CREATE TABLE `butler_coupon` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `activity_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券活动规则ID',
  `name` varchar(60) NOT NULL DEFAULT '' COMMENT '优惠券名字',
  `order_price` int(11) NOT NULL DEFAULT '0' COMMENT '面值/优惠券金 (单位：分) 使用条件为：阶梯满减的券，该字段值为0',
  `sale_price` int(11) NOT NULL DEFAULT '0' COMMENT '券的售卖价格 (单位：分)',
  `img` varchar(300) DEFAULT NULL COMMENT '优惠券的图片地址',
  `num` int(11) DEFAULT NULL COMMENT '发放数量',
  `issuer` int(11) DEFAULT '0' COMMENT '发行人(发放主体) 1：平台发放(成本全部由平台承担)、2：平台发放(成本由平台与店铺共同承担)、3：店铺发放(成本全部由店铺承担)',
  `is_show` int(11) DEFAULT NULL COMMENT '是否公开显示 1：公开显示、2：不公开显示',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发放类型 0下单赠送 1 按用户发放 2 免费领取 3 线下发放 (作废)',
  `use_type` tinyint(1) DEFAULT '0' COMMENT '使用范围：0全店通用1指定商品可用2指定分类商品可用 (作废)',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券金额 (作废)',
  `condition` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '使用条件 (作废)',
  `createnum` int(11) DEFAULT '10' COMMENT '发放数量 (作废)',
  `send_num` int(11) DEFAULT '0' COMMENT '已领取数量 (作废)',
  `use_num` int(11) DEFAULT '0' COMMENT '已使用数量 (作废)',
  `send_start_time` int(11) DEFAULT NULL COMMENT '发放开始时间 (作废) ',
  `send_end_time` int(11) DEFAULT NULL COMMENT '发放结束时间 (作废)',
  `use_start_time` int(11) DEFAULT NULL COMMENT '使用开始时间 (作废) ',
  `use_end_time` int(11) DEFAULT NULL COMMENT '使用结束时间 (作废)',
  `add_time` int(11) DEFAULT NULL COMMENT '添加时间 (作废)',
  `store_id` int(10) DEFAULT '0' COMMENT '商家店铺ID',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1有效2无效 (作废)',
  `coupon_info` varchar(255) DEFAULT NULL COMMENT '优惠券描述说明',
  `start_send_time` datetime DEFAULT NULL COMMENT '发放开始时间',
  `end_send_time` datetime DEFAULT NULL COMMENT '发放结束时间',
  `effective_type` int(11) DEFAULT NULL COMMENT '使用有效时间类型(1绝对时间，2相对时间)',
  `effective_duration` int(11) DEFAULT '0' COMMENT '有效时长(单位小时，相对时间的时候有值)',
  `start_use_time` datetime DEFAULT NULL COMMENT '使用开始时间 ',
  `end_use_time` datetime DEFAULT NULL COMMENT '使用结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE,
  KEY `use_end_time` (`use_end_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券表';

-- ----------------------------
-- Table structure for butler_coupon_activity
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_activity`;
CREATE TABLE `butler_coupon_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `name` varchar(60) NOT NULL COMMENT '优惠券活动名称',
  `type` int(11) NOT NULL DEFAULT '2' COMMENT '发放方式 1：按用户发放、2：用户免费领取、3：发放+领取、4：大客户团购 (用钱购买的购物卡，可以多次使用，有对应记录流水的表)、5：购买代金券(有对应的字段记录券的价格)',
  `rule_img` varchar(300) DEFAULT NULL COMMENT '优惠券活动的图片地址',
  `detail` varchar(500) DEFAULT NULL COMMENT '优惠券活动说明',
  `use_threshold` int(11) NOT NULL DEFAULT '1' COMMENT '使用门槛 1：现金券(不限额度)、2：满减券(满多少减多少)、3：折扣券(打x折扣)',
  `use_condition` int(11) NOT NULL DEFAULT '0' COMMENT '使用条件  1：无条件使用(不限额直接使用券)、2：固定金额不叠加满减、3：固定金额叠加满减、4：阶梯满减、5：无条件折扣',
  `num_limit` int(11) NOT NULL DEFAULT '1' COMMENT '限制数量 1：每人仅限一张、2：每人每天限一张',
  `is_conjunction` int(11) NOT NULL DEFAULT '0' COMMENT '是否叠加 1：同种券可以叠加使用、2：同种券不可以叠加使用、3：与其他券可以叠加使用、4：与其他券不可以叠加使用',
  `is_return` int(11) NOT NULL COMMENT '退款是否返还优惠券 1：返还、2：不返还',
  `is_show` int(11) NOT NULL DEFAULT '0' COMMENT '是否公开显示 1：公开显示、2：不公开显示',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券活动规则 表';

-- ----------------------------
-- Table structure for butler_coupon_business
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_business`;
CREATE TABLE `butler_coupon_business` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `activity_id` int(11) NOT NULL COMMENT '优惠券活动ID',
  `use_type` int(11) NOT NULL COMMENT '使用范围 1：针对单品、2：针对分类、3：针对品牌、4：针对全平台所有商品、5:：针对制定店铺',
  `business_id` int(11) DEFAULT NULL COMMENT '对应的业务id',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券使用范围表';

-- ----------------------------
-- Table structure for butler_coupon_ladder
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_ladder`;
CREATE TABLE `butler_coupon_ladder` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `activity_id` int(11) NOT NULL COMMENT '优惠券ID',
  `minPrice` int(11) NOT NULL COMMENT '购买最小价格 (单位：分)',
  `maxPrice` int(11) NOT NULL COMMENT '购买最大价格 (单位：分)',
  `order_price` int(11) NOT NULL COMMENT '阶梯满减类型-优惠券 面额 (单位：分)',
  `create_time` int(11) NOT NULL COMMENT '创建时间',
  `update_time` int(11) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券满减-阶梯型满减 表';

-- ----------------------------
-- Table structure for butler_coupon_list
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_list`;
CREATE TABLE `butler_coupon_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `coupon_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券ID',
  `cid` int(8) NOT NULL DEFAULT '0' COMMENT '优惠券 对应coupon表id (作废)',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '发放类型 0下单赠送 1 按用户发放 2 免费领取 3 线下发放 (作废)',
  `uid` int(8) NOT NULL DEFAULT '0' COMMENT '用户id',
  `order_id` int(8) NOT NULL DEFAULT '0' COMMENT '订单id',
  `coupon_status` int(11) NOT NULL COMMENT '状态 1已领取未使用、2已使用、3已过期、4已退回、5已作废',
  `get_order_id` int(11) DEFAULT NULL COMMENT '送券订单ID (作废)',
  `use_time` int(11) NOT NULL DEFAULT '0' COMMENT '使用时间 (作废)',
  `code` varchar(10) DEFAULT '' COMMENT '优惠券兑换码',
  `send_time` int(11) NOT NULL DEFAULT '0' COMMENT '发放时间 (作废)',
  `store_id` int(10) DEFAULT '0' COMMENT '商家店铺ID (作废)',
  `status` tinyint(4) DEFAULT '0' COMMENT '0未使用1已使用2已过期 (作废)',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标识;1:删除,0未删除 (作废)',
  `create_time` datetime NOT NULL COMMENT '领取时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `cid` (`cid`) USING BTREE,
  KEY `code` (`code`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE,
  KEY `uid` (`uid`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_coupon_order_record
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_order_record`;
CREATE TABLE `butler_coupon_order_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `coupon_list_id` int(11) NOT NULL COMMENT '优惠券ID',
  `consume_money` int(11) NOT NULL COMMENT '优惠券消费金额 (本次)',
  `residue_money` int(11) NOT NULL COMMENT '代金券剩余面额 表 (本次)',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券消费记录（对于可以分多次使用的优惠券）';

-- ----------------------------
-- Table structure for butler_coupon_position
-- ----------------------------
DROP TABLE IF EXISTS `butler_coupon_position`;
CREATE TABLE `butler_coupon_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `activity_id` int(11) NOT NULL COMMENT '优惠券活动ID',
  `show_type` int(11) NOT NULL DEFAULT '0' COMMENT '显示类型 1：平台广告区域、2：店铺首页、3：商品详情页、4:：朋友圈、5：二维码、6：购物车',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券显示位置表';

-- ----------------------------
-- Table structure for butler_delivery_doc
-- ----------------------------
DROP TABLE IF EXISTS `butler_delivery_doc`;
CREATE TABLE `butler_delivery_doc` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '发货单ID',
  `order_id` int(11) unsigned NOT NULL COMMENT '订单ID',
  `order_sn` varchar(64) NOT NULL DEFAULT '',
  `user_id` int(11) unsigned NOT NULL COMMENT '用户ID',
  `admin_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '管理员ID',
  `consignee` varchar(64) NOT NULL DEFAULT '' COMMENT '收货人',
  `zipcode` varchar(6) DEFAULT NULL COMMENT '邮编',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '联系手机',
  `country` int(11) unsigned NOT NULL COMMENT '国ID',
  `province` int(11) unsigned NOT NULL COMMENT '省ID',
  `city` int(11) unsigned NOT NULL COMMENT '市ID',
  `district` int(11) unsigned NOT NULL COMMENT '区ID',
  `address` varchar(255) NOT NULL DEFAULT '',
  `shipping_code` varchar(32) DEFAULT NULL COMMENT '物流code',
  `shipping_name` varchar(64) DEFAULT NULL COMMENT '快递名称',
  `shipping_price` decimal(10,2) DEFAULT '0.00' COMMENT '运费',
  `invoice_no` varchar(255) NOT NULL DEFAULT '' COMMENT '物流单号',
  `tel` varchar(64) DEFAULT NULL COMMENT '座机电话',
  `note` text COMMENT '管理员添加的备注信息',
  `best_time` int(11) DEFAULT NULL COMMENT '友好收货时间',
  `create_time` int(11) NOT NULL COMMENT '创建时间',
  `is_del` tinyint(1) DEFAULT '0' COMMENT '是否已经删除',
  `store_id` int(11) DEFAULT '0' COMMENT '店铺商家id',
  `send_type` tinyint(1) DEFAULT '0' COMMENT '发货方式0自填快递1在线预约2电子面单3无需物流',
  `store_address_consignee` varchar(64) NOT NULL DEFAULT '' COMMENT '店铺发货人',
  `store_address_mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '发货人手机',
  `store_address_province_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '发货省',
  `store_address_city_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '发货市',
  `store_address_district_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '发货县/区',
  `store_address` varchar(255) NOT NULL DEFAULT '' COMMENT '发货地址',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='发货单';

-- ----------------------------
-- Table structure for butler_door_server
-- ----------------------------
DROP TABLE IF EXISTS `butler_door_server`;
CREATE TABLE `butler_door_server` (
  `i_id` int(220) NOT NULL AUTO_INCREMENT COMMENT '安装任务ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID',
  `goods_id` int(11) NOT NULL COMMENT '产品id',
  `i_name` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '安装人员姓名',
  `i_phone` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '安装人员联系方式',
  `i_address` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '安装的地址',
  `i_start_time` datetime DEFAULT NULL COMMENT '预约时间',
  `i_end_time` datetime DEFAULT NULL COMMENT '安装结束时间',
  `i_active` int(11) NOT NULL COMMENT '状态（0：待安装，1：已安装，2：已分派，3已取消）',
  `operation` varchar(255) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '分配任务人员',
  `i_remarks` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '安装备注',
  `i_name_comment` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '对安装人员的评价',
  `time` datetime DEFAULT NULL COMMENT '操作时间',
  `worker_id` int(11) NOT NULL DEFAULT '0' COMMENT '上门服务人员ID',
  `appoint_time` datetime DEFAULT NULL COMMENT '预约时间',
  `done_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`i_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='上门服务流程记录';

-- ----------------------------
-- Table structure for butler_feedback
-- ----------------------------
DROP TABLE IF EXISTS `butler_feedback`;
CREATE TABLE `butler_feedback` (
  `msg_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '默认自增ID',
  `parent_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '回复留言ID',
  `user_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `user_name` varchar(60) NOT NULL DEFAULT '',
  `msg_title` varchar(200) NOT NULL DEFAULT '' COMMENT '留言标题',
  `msg_type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '留言类型',
  `msg_status` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '处理状态',
  `msg_content` text NOT NULL COMMENT '留言内容',
  `msg_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '留言时间',
  `message_img` varchar(255) NOT NULL DEFAULT '',
  `order_id` int(11) unsigned NOT NULL DEFAULT '0',
  `msg_area` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`msg_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_flash_info
-- ----------------------------
DROP TABLE IF EXISTS `butler_flash_info`;
CREATE TABLE `butler_flash_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '秒杀活动名称',
  `beginTime` datetime NOT NULL COMMENT '秒杀开始时间',
  `endTime` datetime NOT NULL COMMENT '秒杀结束时间',
  `buyLimit` int(11) NOT NULL DEFAULT '0' COMMENT '一个人在一个活动中的购买限制数量，0没有限制',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `begin_time` (`beginTime`) USING BTREE,
  KEY `end_time` (`endTime`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=320 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='秒杀活动信息表';

-- ----------------------------
-- Table structure for butler_flash_sale
-- ----------------------------
DROP TABLE IF EXISTS `butler_flash_sale`;
CREATE TABLE `butler_flash_sale` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `flashId` int(11) NOT NULL COMMENT '秒杀活动ID',
  `goodId` int(11) unsigned NOT NULL COMMENT '商品ID',
  `skuId` int(11) NOT NULL COMMENT '商品SKUID',
  `price` decimal(10,2) NOT NULL COMMENT '秒杀价格',
  `goodNum` int(11) NOT NULL DEFAULT '0' COMMENT '秒杀商品库存',
  `buyLimit` int(11) NOT NULL DEFAULT '0' COMMENT '每人限购数',
  `buyNum` int(11) NOT NULL DEFAULT '0' COMMENT '购买人数',
  `orderNum` int(11) NOT NULL DEFAULT '0' COMMENT '下单数量',
  `fakeBuyNum` int(11) NOT NULL DEFAULT '0' COMMENT '虚假购买数量',
  `point` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `pointLimit` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分限制（个人拥有最少这么多积分才可以购买）',
  `imgPath` varchar(200) NOT NULL DEFAULT '' COMMENT '秒杀图片地址',
  `offSaleStatus` int(1) NOT NULL DEFAULT '0' COMMENT '1上架2下架',
  `isFake` int(1) NOT NULL DEFAULT '0' COMMENT '1真实2虚假',
  `sorted` int(11) NOT NULL DEFAULT '0' COMMENT '排序字段',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `flashId` (`flashId`) USING BTREE,
  KEY `goodId` (`goodId`) USING BTREE,
  KEY `skuId` (`skuId`) USING BTREE,
  KEY `price` (`price`) USING BTREE,
  KEY `buyLimit` (`buyLimit`) USING BTREE,
  KEY `goodNum` (`goodNum`) USING BTREE,
  KEY `fakeBuyNum` (`fakeBuyNum`) USING BTREE,
  KEY `offSaleStatus` (`offSaleStatus`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9854 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='秒杀商品信息';

-- ----------------------------
-- Table structure for butler_freight_config
-- ----------------------------
DROP TABLE IF EXISTS `butler_freight_config`;
CREATE TABLE `butler_freight_config` (
  `config_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '配置id',
  `first_unit` double(16,4) NOT NULL DEFAULT '0.0000' COMMENT '首(重：体积：件）',
  `first_money` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '首(重：体积：件）运费',
  `continue_unit` double(16,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '继续加（件：重量：体积）区间',
  `continue_money` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '继续加（件：重量：体积）的运费',
  `template_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '运费模板ID',
  `is_default` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否是默认运费配置.0不是，1是',
  `store_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '店铺ID',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='运费配置表';

-- ----------------------------
-- Table structure for butler_freight_region
-- ----------------------------
DROP TABLE IF EXISTS `butler_freight_region`;
CREATE TABLE `butler_freight_region` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '模板id',
  `config_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '运费模板配置ID',
  `region_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'region表id',
  `store_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '店铺ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_freight_template
-- ----------------------------
DROP TABLE IF EXISTS `butler_freight_template`;
CREATE TABLE `butler_freight_template` (
  `template_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '运费模板ID',
  `template_name` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '模板名称',
  `type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 件数；1 商品重量；2 商品体积',
  `is_enable_default` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否启用使用默认运费配置,0:不启用，1:启用',
  `store_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '店铺Id',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='运费模板表';

-- ----------------------------
-- Table structure for butler_good
-- ----------------------------
DROP TABLE IF EXISTS `butler_good`;
CREATE TABLE `butler_good` (
  `goods_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `cat_id1` int(11) NOT NULL DEFAULT '0' COMMENT '一级分类id',
  `cat_id2` int(11) DEFAULT '0' COMMENT '二级分类',
  `cat_id3` int(11) DEFAULT '0' COMMENT '三级分类',
  `good_category_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品分类ID',
  `goods_sn` varchar(60) NOT NULL DEFAULT '' COMMENT '商品编号',
  `goods_name` varchar(120) NOT NULL DEFAULT '' COMMENT '商品名称',
  `click_count` int(10) NOT NULL DEFAULT '0' COMMENT '点击数',
  `brand_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '品牌id',
  `collect_sum` int(10) DEFAULT '0' COMMENT '商品收藏数',
  `comment_count` smallint(5) DEFAULT '0' COMMENT '商品评论数',
  `weight` int(11) NOT NULL DEFAULT '0' COMMENT '商品重量克为单位',
  `volume` double(10,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '商品体积。单位立方米',
  `exchange_integral` int(11) NOT NULL DEFAULT '0' COMMENT 'COMMENT ''积分兑换：0不参与积分兑换',
  `keywords` varchar(255) NOT NULL DEFAULT '' COMMENT '商品关键词',
  `goods_remark` varchar(420) NOT NULL DEFAULT '' COMMENT '商品简单描述',
  `goods_content` text COMMENT '商品详细描述',
  `mobile_content` text COMMENT '手机端商品详情',
  `original_img` varchar(255) NOT NULL DEFAULT '' COMMENT '商品上传原始图',
  `is_virtual` int(3) NOT NULL DEFAULT '0' COMMENT '是否为虚拟商品 1是，0否',
  `virtual_indate` int(11) DEFAULT '0' COMMENT '虚拟商品有效期',
  `virtual_limit` smallint(6) DEFAULT '0' COMMENT '虚拟商品购买上限',
  `virtual_refund` int(1) DEFAULT '1' COMMENT '是否允许过期退款， 1是，0否',
  `is_on_sale` int(1) NOT NULL DEFAULT '1' COMMENT '0下架1上架2违规下架',
  `is_free_shipping` int(1) NOT NULL DEFAULT '0' COMMENT '是否包邮0否1是',
  `on_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '商品上架时间',
  `sort` smallint(4) unsigned NOT NULL DEFAULT '1' COMMENT '商品排序',
  `is_recommend` int(1) NOT NULL DEFAULT '0' COMMENT '是否推荐(1是，0否)',
  `is_new` int(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否新品',
  `is_hot` int(1) DEFAULT '0' COMMENT '是否热卖',
  `give_integral` int(11) NOT NULL DEFAULT '0' COMMENT '购买商品赠送积分',
  `sales_sum` int(11) DEFAULT '0' COMMENT '商品销量',
  `prom_type` int(1) DEFAULT '0' COMMENT '0默认1抢购2团购3优惠促销4预售5虚拟(5其实没用)6拼团',
  `prom_id` int(11) DEFAULT '0' COMMENT '优惠活动id',
  `store_id` int(11) DEFAULT '0' COMMENT '商家店铺ID',
  `spu` varchar(128) DEFAULT '' COMMENT 'SPU',
  `sku` varchar(128) DEFAULT '' COMMENT 'SKU',
  `goods_state` int(1) DEFAULT '1' COMMENT '0待审核1审核通过2审核失败',
  `goods_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '审核失败原因',
  `close_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '违规下架原因',
  `template_id` int(11) unsigned DEFAULT '0' COMMENT '运费模板ID',
  `video` varchar(100) NOT NULL DEFAULT '' COMMENT '视频地址',
  `door_service_status` int(1) NOT NULL DEFAULT '1' COMMENT '是否需要上门服务（1.无需上门服务;2.可选上门服务;3.必须上门服务）',
  `store_promote_rate` decimal(4,4) NOT NULL DEFAULT '0.0000' COMMENT '冗余店铺推广比例',
  `is_earn_point` int(1) DEFAULT '0' COMMENT '0否 1是',
  `point_as_money` decimal(10,2) DEFAULT '0.00' COMMENT '最多可以使用多少额度积分抵扣金额',
  PRIMARY KEY (`goods_id`) USING BTREE,
  KEY `goods_sn` (`goods_sn`) USING BTREE,
  KEY `cat_id` (`cat_id1`) USING BTREE,
  KEY `goods_name` (`goods_name`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE,
  KEY `exchange_integral` (`exchange_integral`) USING BTREE,
  KEY `keywords` (`keywords`)
) ENGINE=InnoDB AUTO_INCREMENT=3584 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_good_category
-- ----------------------------
DROP TABLE IF EXISTS `butler_good_category`;
CREATE TABLE `butler_good_category` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品分类id',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '商品分类名称（作废）',
  `mobile_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '手机端显示的商品分类名',
  `parent_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '父id',
  `parent_id_path` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '家族图谱',
  `name_path` varchar(200) NOT NULL DEFAULT '' COMMENT '连带父级的分类，用/分割',
  `icon` varchar(200) NOT NULL DEFAULT '' COMMENT 'ICON 路径，分类的ICON图标',
  `image` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '分类图片，做宣传显示',
  `is_show` int(11) unsigned NOT NULL DEFAULT '1' COMMENT '是否显示(0否 1是)',
  `is_hot` int(11) NOT NULL DEFAULT '0' COMMENT '是否推荐为热门分类(0否 1是)',
  `sort_order` int(11) unsigned NOT NULL DEFAULT '50' COMMENT '顺序排序',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `parent_id` (`parent_id`) USING BTREE,
  KEY `parent_id_path` (`parent_id_path`) USING BTREE,
  KEY `name_path` (`name_path`) USING BTREE,
  KEY `mobile_name` (`mobile_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1193 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品分类表';

-- ----------------------------
-- Table structure for butler_good_collect
-- ----------------------------
DROP TABLE IF EXISTS `butler_good_collect`;
CREATE TABLE `butler_good_collect` (
  `collect_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `goods_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',
  `cat_id3` mediumint(8) NOT NULL DEFAULT '0',
  `add_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '添加时间',
  PRIMARY KEY (`collect_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=370 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_good_images
-- ----------------------------
DROP TABLE IF EXISTS `butler_good_images`;
CREATE TABLE `butler_good_images` (
  `img_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '图片id 自增',
  `goods_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',
  `image_url` varchar(255) NOT NULL DEFAULT '' COMMENT '图片地址',
  `img_sort` tinyint(1) unsigned zerofill DEFAULT '0' COMMENT '商品缩略图排序,最小的拍最前面',
  `album_id` int(11) DEFAULT NULL COMMENT '相册ID',
  PRIMARY KEY (`img_id`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17332 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_good_sku
-- ----------------------------
DROP TABLE IF EXISTS `butler_good_sku`;
CREATE TABLE `butler_good_sku` (
  `item_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '规格商品id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `goods_mark` varchar(200) NOT NULL DEFAULT '' COMMENT '商品规格备注',
  `sku` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT 'SKU编码',
  `spec_img` varchar(200) NOT NULL DEFAULT '' COMMENT '规格商品主图',
  `key` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '规格键名',
  `key_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '规格键名中文',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '销售价格',
  `supply_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '供货商供应价格',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '市场价格',
  `store_count` int(11) NOT NULL DEFAULT '10' COMMENT '库存数量',
  `disAbled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否可用（0否1是）',
  PRIMARY KEY (`item_id`) USING BTREE,
  KEY `gk` (`goods_id`,`key`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE,
  KEY `key` (`key`) USING BTREE,
  KEY `store_count` (`store_count`)
) ENGINE=InnoDB AUTO_INCREMENT=8415 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品SKU信息';

-- ----------------------------
-- Table structure for butler_good_visit
-- ----------------------------
DROP TABLE IF EXISTS `butler_good_visit`;
CREATE TABLE `butler_good_visit` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `share_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '邀请人ID',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `visit_id` (`id`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `share_user_id` (`share_user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=44554 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品浏览历史表-计算用户推荐可以用';

-- ----------------------------
-- Table structure for butler_home_set
-- ----------------------------
DROP TABLE IF EXISTS `butler_home_set`;
CREATE TABLE `butler_home_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(50) NOT NULL DEFAULT '' COMMENT '标题',
  `goto_type` int(11) NOT NULL DEFAULT '0' COMMENT '类型（1资讯2商品详情3商品分类4WEB地址5店铺首页6专题页7推荐店铺8邀请有礼9拼团抽奖10秒杀）',
  `goto_id` int(11) NOT NULL DEFAULT '0' COMMENT '跳转页面对应业务ID',
  `position` int(11) NOT NULL DEFAULT '0' COMMENT '位置（1banner2金刚区3资讯区4打广告5小广告6专场',
  `sort_id` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `img` varchar(200) NOT NULL DEFAULT '' COMMENT '图片地址',
  `web_url` varchar(200) NOT NULL DEFAULT '' COMMENT '跳转WEB地址',
  `background_color` varchar(50) NOT NULL DEFAULT '' COMMENT '背景颜色',
  `is_show` int(11) NOT NULL DEFAULT '1' COMMENT '是否显示(1显示2不显示)',
  `parent_id` int(11) NOT NULL DEFAULT '0' COMMENT '子块对应的父块的ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='首页设置';

-- ----------------------------
-- Table structure for butler_lottery
-- ----------------------------
DROP TABLE IF EXISTS `butler_lottery`;
CREATE TABLE `butler_lottery` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `title` varchar(60) NOT NULL DEFAULT '' COMMENT '一元抢购抽奖活动标题',
  `start_time` datetime NOT NULL COMMENT '一元抢购抽奖活动开始时间',
  `end_time` datetime NOT NULL COMMENT '一元抢购抽奖活动结束时间',
  `max_num` int(10) NOT NULL DEFAULT '0' COMMENT '参团最大人数限制',
  `min_num` int(10) NOT NULL DEFAULT '0' COMMENT '开团最少人数底限',
  `type` int(10) NOT NULL DEFAULT '1' COMMENT '开团方式：1 定时开、2 人满开',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '一元抢购抽奖活动创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '一元抢购抽奖活动修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='一元抢购抽奖活动信息表';

-- ----------------------------
-- Table structure for butler_lottery_good
-- ----------------------------
DROP TABLE IF EXISTS `butler_lottery_good`;
CREATE TABLE `butler_lottery_good` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `lottery_id` int(10) NOT NULL DEFAULT '0' COMMENT '一元抢购抽奖活动ID',
  `good_id` int(10) NOT NULL COMMENT '商品ID',
  `spec_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格iD',
  `title` varchar(200) NOT NULL DEFAULT '' COMMENT '商品的活动标题',
  `img_path` varchar(150) NOT NULL DEFAULT '' COMMENT '商品图片',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '参与活动的商品价格',
  `num` int(10) NOT NULL DEFAULT '1' COMMENT '参与活动的商品数量',
  `orderNum` int(10) NOT NULL DEFAULT '0' COMMENT '下单人数',
  `buy_num` int(11) NOT NULL DEFAULT '0' COMMENT '已经被卖掉的数量',
  `description` varchar(200) NOT NULL DEFAULT '' COMMENT '活动描述',
  `status` int(10) NOT NULL DEFAULT '0' COMMENT '活动状态：1待审核，2正常，3审核拒绝，4下架商品',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `sort_id` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='一元抢购抽奖活动_商品表';

-- ----------------------------
-- Table structure for butler_lottery_order
-- ----------------------------
DROP TABLE IF EXISTS `butler_lottery_order`;
CREATE TABLE `butler_lottery_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '自增长id',
  `lottery_good_id` int(10) NOT NULL DEFAULT '0' COMMENT '一元抢购抽奖活动商品ID',
  `activity_num` int(10) NOT NULL COMMENT '活动场数',
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_id` int(11) NOT NULL COMMENT '一元抢购抽奖活动 订单ID',
  `is_win` int(10) NOT NULL DEFAULT '2' COMMENT '是否中奖: 1 中奖、2 没中',
  `open_time` datetime DEFAULT NULL COMMENT '开奖时间(人满开/定时开都用这个字段)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='一元抢购抽奖活动_订单表';

-- ----------------------------
-- Table structure for butler_order
-- ----------------------------
DROP TABLE IF EXISTS `butler_order`;
CREATE TABLE `butler_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号',
  `user_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `master_order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '主订单号',
  `order_status` int(1) NOT NULL DEFAULT '0' COMMENT '订单流程状态.0正常进行中的订单，3取消订单，4完成状态，5退款订单-有点类似于订单类型，标识订单处于哪个流程中',
  `pay_status` int(1) NOT NULL DEFAULT '0' COMMENT '支付状态.0待支付，1已支付，2部分支付，3已退款，4拒绝退款 -支付流程中的订单状态流转',
  `shipping_status` int(1) NOT NULL DEFAULT '0' COMMENT '发货状态0未发货 1已发货 2已收货 -发货流程中订单流转状态',
  `is_comment` int(1) NOT NULL DEFAULT '0' COMMENT '是否评价（0：未评价；1：已评价）',
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收货人',
  `country` int(11) NOT NULL DEFAULT '0' COMMENT '国家',
  `province` int(11) NOT NULL DEFAULT '0' COMMENT '省份',
  `city` int(11) NOT NULL DEFAULT '0' COMMENT '城市',
  `district` int(11) NOT NULL DEFAULT '0' COMMENT '县区',
  `twon` int(11) NOT NULL DEFAULT '0' COMMENT '乡镇',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `zipcode` varchar(60) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机',
  `email` varchar(60) NOT NULL DEFAULT '' COMMENT '邮件',
  `shipping_code` varchar(32) NOT NULL DEFAULT '' COMMENT '物流单号',
  `shipping_name` varchar(120) NOT NULL DEFAULT '' COMMENT '物流名称(物流公司ID)',
  `shipping_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '邮费',
  `shipping_time` int(11) unsigned DEFAULT '0' COMMENT '最新发货时间',
  `pay_code` varchar(32) NOT NULL DEFAULT '' COMMENT '支付方式 1支付宝支付2微信支付',
  `pay_name` varchar(120) NOT NULL DEFAULT '' COMMENT '支付方式名称',
  `transaction_id` varchar(100) DEFAULT '' COMMENT '第三方平台交易流水号',
  `order_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应付款金额',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总价',
  `point_as_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `store_id` int(10) NOT NULL DEFAULT '0' COMMENT '店铺ID',
  `add_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '下单时间',
  `confirm_time` int(10) DEFAULT '0' COMMENT '收货确认时间',
  `pay_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '支付时间',
  `cancel_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '取消时间',
  `remind` int(1) NOT NULL DEFAULT '2' COMMENT '2：未提醒发货 3：提醒发货',
  `activity_type` int(2) DEFAULT '1' COMMENT '活动类型：1 普通订单、2 秒杀活动订单、3 一元抢购抽奖订单',
  `user_note` varchar(200) DEFAULT '' COMMENT '用户备注',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE,
  UNIQUE KEY `order_sn` (`order_sn`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `master_order_sn` (`master_order_sn`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE,
  KEY `add_time` (`add_time`) USING BTREE,
  KEY `order_status` (`order_status`) USING BTREE,
  KEY `pay_status` (`pay_status`) USING BTREE,
  KEY `shipping_status` (`shipping_status`) USING BTREE,
  KEY `is_comment` (`is_comment`) USING BTREE,
  KEY `point_as_money` (`point_as_money`)
) ENGINE=InnoDB AUTO_INCREMENT=29589 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单';

-- ----------------------------
-- Table structure for butler_order_comment
-- ----------------------------
DROP TABLE IF EXISTS `butler_order_comment`;
CREATE TABLE `butler_order_comment` (
  `order_commemt_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单评论索引id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `describe_score` decimal(2,1) NOT NULL DEFAULT '0.0' COMMENT '描述相符分数（0~5）',
  `seller_score` decimal(2,1) NOT NULL DEFAULT '0.0' COMMENT '卖家服务分数（0~5）',
  `logistics_score` decimal(2,1) NOT NULL DEFAULT '0.0' COMMENT '物流服务分数（0~5）',
  `commemt_time` int(10) unsigned DEFAULT NULL COMMENT '评分时间',
  `deleted` int(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '不删除0；删除：1',
  PRIMARY KEY (`order_commemt_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单评分表';

-- ----------------------------
-- Table structure for butler_order_div_rate
-- ----------------------------
DROP TABLE IF EXISTS `butler_order_div_rate`;
CREATE TABLE `butler_order_div_rate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `store_level_name` varchar(50) NOT NULL DEFAULT '''''' COMMENT '店铺等级',
  `butler_commission_rate` decimal(4,4) NOT NULL DEFAULT '0.0000' COMMENT '平台抽成比例',
  `promote_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '推广人员ID',
  `promote_user_level_name` varchar(50) NOT NULL DEFAULT '''''' COMMENT '推广用户等级',
  `promote_commission_rate` decimal(4,4) NOT NULL DEFAULT '0.0000' COMMENT '推广抽成比例',
  `store_promote` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '店铺推广金额',
  `first_reward` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '首单奖励',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3651 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单分成信息表';

-- ----------------------------
-- Table structure for butler_order_goods
-- ----------------------------
DROP TABLE IF EXISTS `butler_order_goods`;
CREATE TABLE `butler_order_goods` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id自增',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_sn` varchar(20) NOT NULL DEFAULT '' COMMENT '订单编号（没有定订单ID的时候做外健用，redis中用）',
  `goods_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品id',
  `skuId` int(11) NOT NULL DEFAULT '0' COMMENT 'SKU-ID',
  `goods_name` varchar(100) NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_sn` varchar(50) NOT NULL DEFAULT '' COMMENT '商品货号',
  `goods_num` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `spec_key` varchar(200) NOT NULL DEFAULT '' COMMENT '商品规格key',
  `spec_key_name` varchar(200) NOT NULL DEFAULT '' COMMENT '规格对应的中文名字',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '本店销售价格',
  `cost_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品成本价',
  `supply_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '供应价格',
  `final_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品实际购买价格（不包括积分抵扣的金额），但是包含商品个数',
  `point_as_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `give_integral` int(11) NOT NULL DEFAULT '0' COMMENT '购买商品赠送积分',
  `activity_id` int(2) NOT NULL DEFAULT '0' COMMENT '活动ID(秒杀ID、一元抢购ID)',
  `activity_type` int(2) NOT NULL DEFAULT '1' COMMENT '活动类型：1 普通订单、2 秒杀活动订单、3 一元抢购抽奖订单',
  `door_service_status` int(1) NOT NULL DEFAULT '1' COMMENT '是否需要上门服务（1.无需上门服务;3.需要上门服务）',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`rec_id`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE,
  KEY `goods_id` (`goods_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5175 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单商品表';

-- ----------------------------
-- Table structure for butler_point_effective
-- ----------------------------
DROP TABLE IF EXISTS `butler_point_effective`;
CREATE TABLE `butler_point_effective` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `type` int(11) NOT NULL COMMENT '类型（1入账 2出账）',
  `business_type` int(11) NOT NULL DEFAULT '0' COMMENT '业务类型（1注册 2分享转发 3 取消订单 4提现 5消费 6下线首单奖励 7邀请用户注册 8购买商品赠送 9提现审核拒绝退回 10线下录入）',
  `is_withdraw` int(11) NOT NULL DEFAULT '1' COMMENT '是否可以提现（1可以2不可以）',
  `business_id` int(11) NOT NULL DEFAULT '0' COMMENT '业务类型对应的ID',
  `businessCode` varchar(100) NOT NULL DEFAULT '' COMMENT '业务编号（秒杀没有ID引起的）',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分值',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `business_type` (`business_type`) USING BTREE,
  KEY `is_withdraw` (`is_withdraw`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9685 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='有效积分流水';

-- ----------------------------
-- Table structure for butler_point_freeze
-- ----------------------------
DROP TABLE IF EXISTS `butler_point_freeze`;
CREATE TABLE `butler_point_freeze` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `type` int(11) DEFAULT NULL COMMENT '类型（1核销入账 2领取出账）',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `type` (`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5855 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='冻结积分流水';

-- ----------------------------
-- Table structure for butler_point_invest
-- ----------------------------
DROP TABLE IF EXISTS `butler_point_invest`;
CREATE TABLE `butler_point_invest` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `type` int(11) DEFAULT NULL COMMENT '类型（1我的投资 2我的下线投资）',
  `verification_status` int(11) DEFAULT NULL COMMENT '核销状态（1未核销 2 部分核销 3已核销）',
  `order_id` int(11) DEFAULT NULL COMMENT '订单(订单商品)ID',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '投资金额',
  `verification_amount` decimal(10,2) DEFAULT '0.00' COMMENT '核销金额',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `verification_status` (`verification_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=162 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='投资积分记录';

-- ----------------------------
-- Table structure for butler_point_sum
-- ----------------------------
DROP TABLE IF EXISTS `butler_point_sum`;
CREATE TABLE `butler_point_sum` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `mine_invest_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '我的投资汇总',
  `subline_invest_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '我的下线投资汇总',
  `mine_invest_remain` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '我的投资剩余积分',
  `subline_invest_remain` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '我的下线投资剩余积分',
  `freeze_remain` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '待领取的剩余积分（剩余冻结积分）',
  `usable_remain` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '剩余可用积分（包含可提现积分）',
  `withdrawable_remain` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '剩余可提现积分',
  `withdrawable_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '提现总额',
  `consume_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '消费积分总和',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=527 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户积分汇总';

-- ----------------------------
-- Table structure for butler_point_withdraw
-- ----------------------------
DROP TABLE IF EXISTS `butler_point_withdraw`;
CREATE TABLE `butler_point_withdraw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `withdraw_sn` varchar(50) DEFAULT NULL COMMENT '提现编号',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `withdraw_status` int(11) DEFAULT NULL COMMENT '提现状态（1待审核 2待打款 3已完成 5审核拒绝',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '积分值',
  `actual_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '扣除手续费以后的实际金额',
  `remark` varchar(50) DEFAULT NULL COMMENT '备注',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `bankName` varchar(50) DEFAULT NULL COMMENT '开户行',
  `bankCard` varchar(50) DEFAULT NULL COMMENT '银行卡号',
  `auth_user_id` int(11) DEFAULT NULL COMMENT '审核人ID',
  `auth_time` datetime DEFAULT NULL COMMENT '审核时间',
  `pay_user_id` int(11) DEFAULT NULL COMMENT '支付人ID',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_sn` varchar(100) NOT NULL DEFAULT '' COMMENT '支付编号（第三方支付回调后的单号，方便查询）',
  `pay_type` int(1) NOT NULL DEFAULT '0' COMMENT '支付方式（1人工打款 2微信红包 3微信零钱 4微信转银行卡 5支付宝余额 6支付宝转银行卡）',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `withdraw_status` (`withdraw_status`) USING BTREE,
  KEY `withdraw_sn` (`withdraw_sn`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2972 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='积分提现记录';

-- ----------------------------
-- Table structure for butler_prom_goods
-- ----------------------------
DROP TABLE IF EXISTS `butler_prom_goods`;
CREATE TABLE `butler_prom_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` varchar(60) NOT NULL DEFAULT '' COMMENT '促销活动名称',
  `type` int(2) NOT NULL DEFAULT '0' COMMENT '促销类型:0直接打折,1减价优惠,2固定金额出售,3买就赠优惠券',
  `expression` varchar(100) NOT NULL DEFAULT '' COMMENT '优惠体现',
  `description` text COMMENT '活动描述',
  `start_time` int(11) NOT NULL COMMENT '活动开始时间',
  `end_time` int(11) NOT NULL COMMENT '活动结束时间',
  `status` int(1) DEFAULT '1' COMMENT '1正常，0管理员关闭',
  `is_end` int(1) DEFAULT '0' COMMENT '是否已结束',
  `group` varchar(255) DEFAULT NULL COMMENT '适用范围',
  `store_id` int(10) DEFAULT '0' COMMENT '商家店铺id',
  `orderby` int(10) DEFAULT '0' COMMENT '排序',
  `prom_img` varchar(150) DEFAULT NULL COMMENT '活动宣传图片',
  `recommend` int(1) DEFAULT '0' COMMENT '是否推荐',
  `buy_limit` int(10) DEFAULT NULL COMMENT '每人限购数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_prom_order
-- ----------------------------
DROP TABLE IF EXISTS `butler_prom_order`;
CREATE TABLE `butler_prom_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(60) NOT NULL DEFAULT '' COMMENT '活动名称',
  `type` int(2) NOT NULL DEFAULT '0' COMMENT '活动类型 0满额打折,1满额优惠金额,2满额送积分,3满额送优惠券',
  `money` float(10,2) DEFAULT '0.00' COMMENT '最小金额',
  `expression` varchar(100) DEFAULT NULL COMMENT '优惠体现',
  `description` text COMMENT '活动描述',
  `start_time` datetime DEFAULT NULL COMMENT '活动开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '活动结束时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '1正常，0管理员关闭',
  `group` varchar(255) DEFAULT NULL COMMENT '适用范围',
  `prom_img` varchar(255) DEFAULT NULL COMMENT '活动宣传图',
  `store_id` int(11) DEFAULT '0' COMMENT '商家店铺id',
  `orderby` int(10) DEFAULT '0' COMMENT '排序',
  `recommend` tinyint(4) DEFAULT '0' COMMENT '是否推荐',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_recharge
-- ----------------------------
DROP TABLE IF EXISTS `butler_recharge`;
CREATE TABLE `butler_recharge` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '会员ID',
  `nickname` varchar(50) DEFAULT NULL COMMENT '会员昵称',
  `order_sn` varchar(50) NOT NULL DEFAULT '' COMMENT '充值单号',
  `account` decimal(10,2) DEFAULT '0.00' COMMENT '充值金额',
  `ctime` int(11) DEFAULT NULL COMMENT '充值时间',
  `pay_time` int(11) DEFAULT NULL COMMENT '支付时间',
  `pay_code` varchar(20) DEFAULT NULL,
  `pay_name` varchar(80) DEFAULT NULL COMMENT '支付方式',
  `pay_status` int(1) DEFAULT '0' COMMENT '充值状态0:待支付 1:充值成功 2:交易关闭',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方平台交易流水号',
  `remark` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_return_goods
-- ----------------------------
DROP TABLE IF EXISTS `butler_return_goods`;
CREATE TABLE `butler_return_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退货申请表id自增',
  `rec_id` int(11) NOT NULL DEFAULT '0' COMMENT '对应订单商品表ID',
  `order_id` int(11) DEFAULT '0' COMMENT '订单id',
  `order_sn` varchar(50) DEFAULT '' COMMENT '订单编号',
  `goods_id` int(11) DEFAULT '0' COMMENT '商品id',
  `goods_num` int(6) DEFAULT '1' COMMENT '退货数量',
  `type` int(1) DEFAULT '0' COMMENT '0仅退款 1退货退款  2换货 3维修',
  `reason` varchar(255) DEFAULT NULL COMMENT '退换货退款申请原因',
  `describe` text COMMENT '问题描述',
  `evidence` varchar(255) DEFAULT '1' COMMENT '申请凭据',
  `imgs` text COMMENT '上传图片路径',
  `status` int(1) DEFAULT '0' COMMENT '-2用户取消-1不同意0待审核1通过2已发货3待退款4换货完成5退款完成6申诉仲裁',
  `remark` varchar(255) DEFAULT '' COMMENT '卖家审核进度说明',
  `user_id` int(11) DEFAULT '0' COMMENT '用户id',
  `store_id` int(10) DEFAULT '0' COMMENT '商家店铺ID',
  `spec_key` varchar(64) DEFAULT '' COMMENT '商品规格',
  `consignee` varchar(20) DEFAULT NULL COMMENT '客户姓名',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `address` varchar(250) DEFAULT NULL COMMENT '收货地址',
  `refund_integral` int(10) DEFAULT '0' COMMENT '退还积分',
  `refund_deposit` decimal(10,2) DEFAULT '0.00' COMMENT '退还预存款',
  `refund_money` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `refund_type` int(1) DEFAULT '0' COMMENT '0退到用户余额 1支付原路退回',
  `refund_mark` varchar(255) DEFAULT NULL COMMENT '管理员退款备注',
  `refund_time` int(11) DEFAULT '0' COMMENT '退款时间',
  `addtime` int(11) DEFAULT '0' COMMENT '售后申请时间',
  `checktime` int(11) DEFAULT '0' COMMENT '卖家审核时间',
  `receivetime` int(11) DEFAULT '0' COMMENT '卖家收货时间',
  `canceltime` int(11) DEFAULT '0' COMMENT '用户取消时间',
  `seller_delivery` text COMMENT '换货服务，卖家重新发货信息',
  `delivery` text COMMENT '用户发货信息',
  `gap` decimal(10,2) DEFAULT '0.00' COMMENT '退款差额',
  `gap_reason` varchar(255) DEFAULT NULL COMMENT '差额原因',
  `is_receive` int(1) DEFAULT '0' COMMENT '申请售后时是否已收货0未收货1已收货',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `order_id` (`order_id`) USING BTREE,
  KEY `rec_id` (`rec_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_search_word
-- ----------------------------
DROP TABLE IF EXISTS `butler_search_word`;
CREATE TABLE `butler_search_word` (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '搜索表ID',
  `keywords` varchar(255) NOT NULL DEFAULT '' COMMENT '搜索关键词，商品关键词',
  `pinyin_full` varchar(255) NOT NULL DEFAULT '' COMMENT '拼音全拼',
  `pinyin_simple` varchar(255) NOT NULL DEFAULT '' COMMENT '拼音简写',
  `search_num` int(8) NOT NULL DEFAULT '0' COMMENT '搜索次数',
  `goods_num` int(8) NOT NULL DEFAULT '0' COMMENT '商品数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='搜索关键词表-分析热搜可以用';

-- ----------------------------
-- Table structure for butler_service_worker
-- ----------------------------
DROP TABLE IF EXISTS `butler_service_worker`;
CREATE TABLE `butler_service_worker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL COMMENT '姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='上门服务人员-之后合并在 staff表中';

-- ----------------------------
-- Table structure for butler_shipping
-- ----------------------------
DROP TABLE IF EXISTS `butler_shipping`;
CREATE TABLE `butler_shipping` (
  `shipping_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '物流公司id',
  `shipping_name` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司名称',
  `shipping_code` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司编码',
  `shipping_desc` varchar(255) NOT NULL DEFAULT '' COMMENT '物流描述',
  `shipping_logo` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司logo',
  `template_width` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '运单模板宽度',
  `template_height` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '运单模板高度',
  `template_offset_x` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '运单模板左偏移量',
  `template_offset_y` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '运单模板上偏移量',
  `template_img` varchar(255) NOT NULL DEFAULT '' COMMENT '运单模板图片',
  `template_html` text NOT NULL COMMENT '打印项偏移校正',
  PRIMARY KEY (`shipping_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_shipping_area
-- ----------------------------
DROP TABLE IF EXISTS `butler_shipping_area`;
CREATE TABLE `butler_shipping_area` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shipping_code` varchar(50) NOT NULL DEFAULT '' COMMENT '物流公司',
  `is_close` tinyint(1) DEFAULT '1' COMMENT '是否关闭：1开启，0关闭',
  `store_id` int(11) DEFAULT '0' COMMENT '商家id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `shipping_id` (`shipping_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_sms_template
-- ----------------------------
DROP TABLE IF EXISTS `butler_sms_template`;
CREATE TABLE `butler_sms_template` (
  `tpl_id` mediumint(8) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `sms_sign` varchar(50) NOT NULL DEFAULT '' COMMENT '短信签名',
  `sms_tpl_code` varchar(100) NOT NULL DEFAULT '' COMMENT '短信模板ID',
  `tpl_content` varchar(512) NOT NULL DEFAULT '' COMMENT '发送短信内容',
  `send_scene` varchar(100) NOT NULL DEFAULT '' COMMENT '短信发送场景',
  `add_time` int(11) NOT NULL COMMENT '添加时间',
  PRIMARY KEY (`tpl_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='短信模板配置表';

-- ----------------------------
-- Table structure for butler_spec
-- ----------------------------
DROP TABLE IF EXISTS `butler_spec`;
CREATE TABLE `butler_spec` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '规格表',
  `name` varchar(30) NOT NULL DEFAULT '' COMMENT '规格名称',
  `categoryId` int(11) NOT NULL DEFAULT '0' COMMENT '分类ID',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `categoryId` (`categoryId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_spec_item
-- ----------------------------
DROP TABLE IF EXISTS `butler_spec_item`;
CREATE TABLE `butler_spec_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '规格项id',
  `spec_id` int(11) NOT NULL DEFAULT '0' COMMENT '规格id',
  `item` varchar(30) NOT NULL DEFAULT '' COMMENT '规格项',
  `store_id` int(11) DEFAULT '0' COMMENT '商家id（作废）',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `spec_id` (`spec_id`) USING BTREE,
  KEY `item` (`item`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2094 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_stock_log
-- ----------------------------
DROP TABLE IF EXISTS `butler_stock_log`;
CREATE TABLE `butler_stock_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  `goods_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
  `goods_spec` varchar(50) DEFAULT NULL COMMENT '商品规格',
  `order_sn` varchar(30) DEFAULT NULL COMMENT '订单编号',
  `store_id` int(11) DEFAULT NULL COMMENT '商家ID',
  `muid` int(11) DEFAULT NULL COMMENT '操作用户ID',
  `stock` int(11) DEFAULT NULL COMMENT '更改库存',
  `ctime` int(11) DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_store
-- ----------------------------
DROP TABLE IF EXISTS `butler_store`;
CREATE TABLE `butler_store` (
  `store_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '店铺索引id',
  `store_name` varchar(50) NOT NULL DEFAULT '' COMMENT '店铺名称',
  `grade_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺等级(作废)',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '会员名称(作废)',
  `seller_name` varchar(50) DEFAULT '' COMMENT '店主卖家用户名(作废)',
  `sc_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺分类(作废)',
  `company_name` varchar(50) DEFAULT '' COMMENT '店铺公司名称(作废)',
  `region_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺所在区域ID',
  `province_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '店铺所在省份ID(作废)',
  `city_id` mediumint(8) NOT NULL DEFAULT '0' COMMENT '店铺所在城市ID(作废)',
  `district` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '店铺所在地区ID(作废)',
  `store_address` varchar(100) NOT NULL DEFAULT '' COMMENT '详细地区',
  `longitude` decimal(10,7) DEFAULT '0.0000000' COMMENT '商家地址经度',
  `latitude` decimal(10,7) DEFAULT '0.0000000' COMMENT '商家地址纬度',
  `store_zip` varchar(10) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `store_state` int(1) NOT NULL DEFAULT '2' COMMENT '店铺状态，0关闭，1开启，2审核中',
  `store_close_info` varchar(255) DEFAULT '' COMMENT '店铺关闭原因',
  `store_sort` int(11) NOT NULL DEFAULT '0' COMMENT '店铺排序(作废)',
  `store_rebate_paytime` varchar(10) NOT NULL DEFAULT '' COMMENT '店铺结算类型(作废)',
  `store_time` int(11) DEFAULT NULL COMMENT '开店时间',
  `store_end_time` int(11) DEFAULT '0' COMMENT '店铺有效截止时间',
  `store_logo` varchar(255) DEFAULT '' COMMENT '店铺logo',
  `store_banner` varchar(255) DEFAULT '' COMMENT '店铺横幅',
  `store_avatar` varchar(150) DEFAULT '' COMMENT '店铺头像',
  `seo_keywords` varchar(255) DEFAULT '' COMMENT '店铺seo关键字(作废)',
  `seo_description` varchar(255) DEFAULT '' COMMENT '店铺seo描述(作废)',
  `store_aliwangwang` varchar(64) DEFAULT '' COMMENT '阿里旺旺(作废)',
  `store_qq` varchar(50) DEFAULT '' COMMENT 'QQ(作废)',
  `store_phone` varchar(20) DEFAULT '' COMMENT '商家电话(作废)',
  `store_zy` text COMMENT '主营商品(作废)',
  `store_domain` varchar(50) DEFAULT '' COMMENT '店铺二级域名(作废)',
  `store_recommend` tinyint(1) NOT NULL DEFAULT '0' COMMENT '推荐，0为否，1为是，默认为0',
  `store_theme` varchar(50) NOT NULL DEFAULT 'default' COMMENT '店铺当前主题(作废)',
  `store_credit` int(10) NOT NULL DEFAULT '0' COMMENT '店铺信用',
  `store_desccredit` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '描述相符度分数',
  `store_servicecredit` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '服务态度分数',
  `store_deliverycredit` decimal(3,2) NOT NULL DEFAULT '0.00' COMMENT '发货速度分数',
  `store_collect` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '店铺收藏数量',
  `store_slide` text COMMENT '店铺幻灯片(作废)',
  `store_slide_url` text COMMENT '店铺幻灯片链接(作废)',
  `store_printdesc` varchar(500) DEFAULT '' COMMENT '打印订单页面下方说明文字(作废)',
  `store_sales` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '店铺销量',
  `store_presales` text COMMENT '售前客服(作废)',
  `store_aftersales` text COMMENT '售后客服(作废)',
  `store_workingtime` varchar(100) DEFAULT '' COMMENT '工作时间(作废)',
  `store_free_price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '超出该金额免运费，大于0才表示该值有效',
  `store_warning_storage` int(10) DEFAULT '0' COMMENT ' 库存预警数(作废)',
  `store_decoration_switch` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '店铺装修开关(0-关闭 装修编号-开启)（作废）',
  `store_decoration_only` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '开启店铺装修时，仅显示店铺装修(1-是 0-否（作废）',
  `is_own_shop` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否自营店铺 1是 0否（作废）',
  `bind_all_gc` tinyint(1) DEFAULT '0' COMMENT '自营店是否绑定全部分类 0否1是（作废）',
  `qitian` tinyint(1) DEFAULT '0' COMMENT '7天退换（作废）',
  `certified` tinyint(1) DEFAULT '0' COMMENT '正品保障（作废）',
  `returned` tinyint(1) DEFAULT '0' COMMENT '退货承诺（作废）',
  `store_free_time` varchar(10) DEFAULT '' COMMENT '商家配送时间（作废）',
  `mb_slide` text COMMENT '手机店铺 轮播图链接地址（作废）',
  `mb_slide_url` text COMMENT '手机版广告链接（作废）',
  `deliver_region` varchar(50) DEFAULT '' COMMENT '店铺默认配送区域（作废）',
  `cod` tinyint(1) DEFAULT '0' COMMENT '货到付款（作废）',
  `two_hour` tinyint(1) DEFAULT '0' COMMENT '两小时发货（作废）',
  `ensure` tinyint(1) DEFAULT '0' COMMENT '保证服务开关（作废）',
  `deposit` decimal(10,2) DEFAULT '0.00' COMMENT '保证金额（作废）',
  `deposit_icon` tinyint(1) DEFAULT '0' COMMENT '保证金显示开关（作废）',
  `store_money` decimal(12,2) DEFAULT '0.00' COMMENT '店铺可用资金（作废）',
  `pending_money` decimal(12,2) DEFAULT '0.00' COMMENT '待结算资金（作废）',
  `deleted` tinyint(1) unsigned zerofill NOT NULL DEFAULT '0' COMMENT '未删除0，已删除1（作废）',
  `goods_examine` tinyint(1) DEFAULT '0' COMMENT '店铺发布商品是否需要审核',
  `service_phone` varchar(20) DEFAULT '' COMMENT '客户下单, 接收下单提醒短信（作废）',
  `domain_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用二级域名.1:启用;0:关闭（作废）',
  `main_category_id` int(11) NOT NULL DEFAULT '0' COMMENT '主营商品分类ID（作废）',
  `butler_rate` decimal(4,4) NOT NULL DEFAULT '0.0500' COMMENT '平成抽取佣金比例',
  `store_type` int(11) NOT NULL DEFAULT '0' COMMENT '1 个人店铺 2 企业店铺',
  `store_desc` varchar(200) NOT NULL DEFAULT '' COMMENT '店铺简介',
  `id_front` varchar(200) NOT NULL DEFAULT '' COMMENT '身份证正面',
  `id_back` varchar(200) NOT NULL DEFAULT '' COMMENT '身份正反面',
  `id_hold` varchar(200) NOT NULL DEFAULT '' COMMENT '手持身份证',
  `business_license` varchar(200) NOT NULL DEFAULT '' COMMENT '营业执照照片',
  `unified_social_reference_code` varchar(50) NOT NULL DEFAULT '' COMMENT '营业执照编号',
  `validity_period_type` int(11) NOT NULL DEFAULT '1' COMMENT '营业执照有效期类型(1长期有效 2固定期限)',
  `start_validity_period` date DEFAULT NULL COMMENT '营业执照开始时间 ',
  `end_validity_period` date DEFAULT NULL COMMENT '营业执照结束时间',
  `card_num` varchar(50) NOT NULL DEFAULT '' COMMENT '银行卡号',
  `card_front` varchar(200) NOT NULL DEFAULT '' COMMENT '银行卡正面',
  `card_back` varchar(200) NOT NULL DEFAULT '' COMMENT '银行卡背面',
  PRIMARY KEY (`store_id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE,
  KEY `store_name` (`store_name`) USING BTREE,
  KEY `sc_id` (`sc_id`) USING BTREE,
  KEY `store_state` (`store_state`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=650 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='店铺数据表';

-- ----------------------------
-- Table structure for butler_store_address
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_address`;
CREATE TABLE `butler_store_address` (
  `store_address_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收货人',
  `province_id` int(11) NOT NULL DEFAULT '0' COMMENT '省份',
  `city_id` int(11) NOT NULL DEFAULT '0' COMMENT '城市',
  `district_id` int(11) NOT NULL DEFAULT '0' COMMENT '地区',
  `town_id` int(11) NOT NULL DEFAULT '0' COMMENT '乡镇',
  `address` varchar(250) NOT NULL DEFAULT '' COMMENT '地址',
  `zip_code` varchar(60) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机',
  `is_default` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '1为默认收货地址',
  `type` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '0为发货地址。1为收货地址',
  `store_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '店铺id',
  PRIMARY KEY (`store_address_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='店铺地址表';

-- ----------------------------
-- Table structure for butler_store_apply
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_apply`;
CREATE TABLE `butler_store_apply` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) NOT NULL DEFAULT '0' COMMENT '申请人会员ID',
  `agent_role_id` int(10) NOT NULL DEFAULT '0' COMMENT '代理商角色ID ',
  `is_fictitious_supplier` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否为虚拟供应商 0否 1是 （agent_role_id = 4时有效）',
  `agent_province_id` int(10) DEFAULT '0' COMMENT '代理商代理省',
  `agent_city_id` int(10) DEFAULT '0' COMMENT '代理商代理市',
  `agent_district_id` int(10) DEFAULT '0' COMMENT '代理商代理区',
  `contacts_name` varchar(20) DEFAULT NULL COMMENT '联系人姓名',
  `contacts_mobile` varchar(20) DEFAULT NULL COMMENT '联系人手机',
  `contacts_email` varchar(50) DEFAULT NULL COMMENT '联系人邮箱',
  `contacts_address` varchar(100) DEFAULT NULL COMMENT '联系人所在地址',
  `company_name` varchar(30) DEFAULT NULL COMMENT '公司名称',
  `company_type` tinyint(1) DEFAULT '1' COMMENT '公司性质',
  `company_website` varchar(50) DEFAULT NULL COMMENT '公司网址',
  `company_province` mediumint(8) DEFAULT '0' COMMENT '公司所在省份',
  `company_city` mediumint(8) DEFAULT '0' COMMENT '公司所在城市',
  `company_district` mediumint(8) DEFAULT '0' COMMENT '公司所在地区',
  `company_address` varchar(100) DEFAULT NULL COMMENT '公司详细地址',
  `company_telephone` varchar(20) DEFAULT NULL COMMENT '固定电话',
  `company_email` varchar(50) DEFAULT NULL COMMENT '电子邮箱',
  `company_fax` varchar(30) DEFAULT NULL COMMENT '传真',
  `company_zipcode` varchar(20) DEFAULT NULL COMMENT '邮政编码',
  `business_licence_number` varchar(20) DEFAULT NULL COMMENT '营业执照注册号/统一社会信用代码',
  `business_licence_cert` varchar(100) DEFAULT NULL COMMENT '营业执照电子版',
  `threeinone` tinyint(1) DEFAULT '1' COMMENT '是否为一证一码商家',
  `reg_capital` varchar(10) DEFAULT NULL COMMENT '注册资金',
  `legal_person` varchar(20) DEFAULT NULL COMMENT '法人代表',
  `legal_identity_cert` varchar(100) DEFAULT NULL COMMENT '法人身份证照片',
  `legal_identity` varchar(50) DEFAULT NULL COMMENT '法人身份证号',
  `power_attorney_cert` varchar(100) DEFAULT '' COMMENT '授权书图片',
  `business_date_start` varchar(20) DEFAULT NULL COMMENT '营业执照有效期',
  `business_date_end` varchar(20) DEFAULT NULL,
  `orgnization_code` varchar(20) DEFAULT NULL COMMENT '组织机构代码',
  `orgnization_cert` varchar(100) DEFAULT NULL COMMENT '组织机构代码证',
  `attached_tax_number` varchar(30) DEFAULT NULL COMMENT '纳税人识别号',
  `tax_rate` tinyint(2) DEFAULT '0' COMMENT '纳税类型税码',
  `taxpayer` tinyint(1) DEFAULT '1' COMMENT '纳税人类型',
  `taxpayer_cert` varchar(100) DEFAULT NULL COMMENT '一般纳税人资格证书',
  `business_scope` text COMMENT '营业执照经营范围',
  `store_name` varchar(30) DEFAULT NULL COMMENT '店铺名称',
  `seller_name` varchar(30) DEFAULT NULL COMMENT '卖家账号',
  `store_type` tinyint(1) DEFAULT '0' COMMENT '店铺性质',
  `store_address` varchar(100) DEFAULT NULL,
  `store_person_name` varchar(20) DEFAULT NULL COMMENT '店铺负责人姓名',
  `store_person_mobile` varchar(20) DEFAULT NULL COMMENT '店铺负责人手机',
  `store_person_qq` varchar(20) DEFAULT NULL COMMENT '店铺联系人QQ',
  `store_person_email` varchar(50) DEFAULT NULL COMMENT '店铺负责人邮箱',
  `store_person_cert` varchar(100) DEFAULT NULL COMMENT '店铺负责人身份证照片',
  `store_person_cert_back` varchar(100) DEFAULT NULL COMMENT '店铺负责人身份证照片反面',
  `store_person_identity` varchar(50) DEFAULT NULL COMMENT '店铺负责人身份证号',
  `bank_account_name` varchar(50) DEFAULT NULL COMMENT '结算银行名称',
  `bank_account_number` varchar(50) DEFAULT NULL COMMENT '结算银行账号',
  `bank_branch_name` varchar(50) DEFAULT NULL COMMENT '开户银行支行名称',
  `bank_card_cert` varchar(100) DEFAULT NULL COMMENT '银行卡正面照片',
  `bank_province` mediumint(8) DEFAULT '0' COMMENT '开户银行支行所在地',
  `bank_city` mediumint(8) DEFAULT '0' COMMENT '开户银行支行所在城市',
  `main_channel` tinyint(1) DEFAULT '0' COMMENT '近一年主营渠道',
  `sales_volume` varchar(255) DEFAULT NULL COMMENT '近一年销售额',
  `sku_num` mediumint(8) DEFAULT '1' COMMENT '可网售商品数量',
  `ec_experience` tinyint(1) DEFAULT '0' COMMENT '同类电子商务网站经验',
  `avg_price` decimal(10,2) DEFAULT '0.00' COMMENT '预计平均客单价',
  `ware_house` tinyint(1) DEFAULT '0' COMMENT '仓库情况',
  `entity_shop` tinyint(1) DEFAULT '0' COMMENT '有无实体店',
  `sc_name` varchar(50) DEFAULT NULL COMMENT '店铺分类名称',
  `sc_id` int(10) DEFAULT NULL COMMENT '店铺分类编号',
  `sc_bail` mediumint(8) DEFAULT '0' COMMENT '店铺分类保证金',
  `sg_id` tinyint(2) DEFAULT '0' COMMENT '店铺等级编号',
  `sg_name` varchar(30) DEFAULT NULL COMMENT '店铺等级名称',
  `store_class_ids` text COMMENT '申请分类佣金信息',
  `paying_amount` decimal(10,2) DEFAULT '0.00' COMMENT '付款金额',
  `paying_amount_cert` varchar(100) DEFAULT NULL COMMENT '付款凭证',
  `auditing_admin_id` int(10) DEFAULT '0' COMMENT '审核管理员ID',
  `apply_state` tinyint(2) DEFAULT '0' COMMENT '店铺申请状态 0未审核，1通过，2拒绝',
  `review_msg` varchar(255) DEFAULT NULL COMMENT '管理员审核信息',
  `add_time` int(11) DEFAULT '0' COMMENT '提交申请时间',
  `apply_type` tinyint(1) DEFAULT '0' COMMENT '申请类型默认0企业1个人',
  `bonus_balance_time` varchar(200) DEFAULT '' COMMENT '提成结算时间',
  `wages_balance_time` text COMMENT '工资结算时间',
  `store_balance_time` varchar(200) DEFAULT '' COMMENT '店铺结算时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商家申请表';

-- ----------------------------
-- Table structure for butler_store_collect
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_collect`;
CREATE TABLE `butler_store_collect` (
  `log_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) DEFAULT NULL,
  `store_id` int(10) DEFAULT NULL,
  `add_time` int(11) DEFAULT NULL COMMENT '收藏店铺时间',
  `store_name` varchar(100) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL COMMENT '收藏会员名称',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=319 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_store_goods_class
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_goods_class`;
CREATE TABLE `butler_store_goods_class` (
  `cat_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '索引ID',
  `cat_name` varchar(50) NOT NULL DEFAULT '' COMMENT '店铺商品分类名称',
  `parent_id` int(11) NOT NULL COMMENT '父级id',
  `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '店铺id',
  `cat_sort` int(11) NOT NULL DEFAULT '0' COMMENT '商品分类排序',
  `is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '分类显示状态',
  `is_nav_show` tinyint(1) DEFAULT '0' COMMENT '是否显示在导航栏',
  `is_recommend` tinyint(1) DEFAULT '0' COMMENT '是否首页推荐',
  `show_num` smallint(4) DEFAULT '4' COMMENT '首页此类商品显示数量',
  PRIMARY KEY (`cat_id`) USING BTREE,
  KEY `stc_parent_id` (`parent_id`,`cat_sort`) USING BTREE,
  KEY `store_id` (`store_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='店铺商品分类表';

-- ----------------------------
-- Table structure for butler_store_level
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_level`;
CREATE TABLE `butler_store_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `store_level_category_id` int(11) DEFAULT NULL COMMENT '商家等级配置ID',
  `store_id` int(11) DEFAULT NULL COMMENT '商家ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `store_id` (`store_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商家等级关联表';

-- ----------------------------
-- Table structure for butler_store_level_category
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_level_category`;
CREATE TABLE `butler_store_level_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `category_id` int(11) DEFAULT NULL COMMENT '商品分类ID',
  `floor_amount` decimal(10,2) DEFAULT NULL COMMENT '等级交易金额下限',
  `commission_rate` decimal(4,4) DEFAULT NULL COMMENT '平台抽成比率',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商家等级-分类关联表';

-- ----------------------------
-- Table structure for butler_store_level_set
-- ----------------------------
DROP TABLE IF EXISTS `butler_store_level_set`;
CREATE TABLE `butler_store_level_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `level_name` varchar(50) DEFAULT NULL COMMENT '等级名称',
  `level` int(11) DEFAULT '0' COMMENT '等级',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商家等级设置表';

-- ----------------------------
-- Table structure for butler_user
-- ----------------------------
DROP TABLE IF EXISTS `butler_user`;
CREATE TABLE `butler_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号码',
  `password` varchar(32) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(50) NOT NULL DEFAULT '' COMMENT '第三方返回昵称',
  `email` varchar(60) NOT NULL DEFAULT '' COMMENT '邮件',
  `sex` int(1) unsigned NOT NULL DEFAULT '0' COMMENT '0 保密 1 男 2 女',
  `birthday` int(11) NOT NULL DEFAULT '0' COMMENT '生日',
  `province_id` int(11) NOT NULL DEFAULT '0' COMMENT '所在省ID',
  `city_id` int(11) NOT NULL DEFAULT '0' COMMENT '所在市ID',
  `district_id` int(11) NOT NULL DEFAULT '0' COMMENT '所在区ID',
  `paypwd` varchar(200) NOT NULL DEFAULT '' COMMENT '支付密码',
  `reg_time` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '注册时间',
  `last_login` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '最后登录时间',
  `last_ip` varchar(15) NOT NULL DEFAULT '' COMMENT '最后登录ip',
  `qq` varchar(20) NOT NULL DEFAULT '' COMMENT 'QQ',
  `mobile_validated` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否验证手机',
  `head_pic` varchar(255) NOT NULL DEFAULT '' COMMENT '头像',
  `bank_name` varchar(150) DEFAULT NULL COMMENT '银行名称',
  `bank_card` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `realname` varchar(50) DEFAULT NULL COMMENT '用户真实姓名',
  `idcard` varchar(100) DEFAULT NULL COMMENT '身份证号',
  `email_validated` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否验证电子邮箱',
  `is_lock` tinyint(1) DEFAULT '0' COMMENT '是否被锁定冻结',
  `first_leader` int(11) DEFAULT '0' COMMENT '第一个上级',
  `open_id` varchar(50) NOT NULL DEFAULT '' COMMENT '微信openID',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) DEFAULT NULL,
  `level_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE KEY `mobile` (`mobile`) USING BTREE,
  KEY `first_leader` (`first_leader`) USING BTREE,
  KEY `nickname` (`nickname`) USING BTREE,
  KEY `sex` (`sex`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5070 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Table structure for butler_user_address
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_address`;
CREATE TABLE `butler_user_address` (
  `address_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) unsigned NOT NULL DEFAULT '0',
  `consignee` varchar(60) NOT NULL DEFAULT '' COMMENT '收货人',
  `email` varchar(60) NOT NULL DEFAULT '' COMMENT '邮箱地址',
  `country` int(11) NOT NULL DEFAULT '0' COMMENT '国家',
  `province` int(11) NOT NULL DEFAULT '0' COMMENT '省份',
  `city` int(11) NOT NULL DEFAULT '0' COMMENT '城市',
  `district` int(11) NOT NULL DEFAULT '0' COMMENT '地区',
  `twon` int(11) DEFAULT '0' COMMENT '乡镇',
  `address` varchar(250) NOT NULL DEFAULT '' COMMENT '地址',
  `zipcode` varchar(60) NOT NULL DEFAULT '' COMMENT '邮政编码',
  `mobile` varchar(60) NOT NULL DEFAULT '' COMMENT '手机',
  `is_default` int(1) DEFAULT '0' COMMENT '默认收货地址',
  PRIMARY KEY (`address_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1515 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_user_balance
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_balance`;
CREATE TABLE `butler_user_balance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `available_amount` decimal(10,4) DEFAULT NULL COMMENT '可用金额',
  `frozen_amount` decimal(10,4) DEFAULT NULL COMMENT '冻结金额',
  `total_amount` decimal(10,4) DEFAULT NULL COMMENT '总金额',
  `withdrawal_amount` decimal(10,4) DEFAULT NULL COMMENT '提现中金额',
  `platinum_bit` decimal(10,4) DEFAULT NULL COMMENT '白金币（与先进1比1的关系，只可消费，不能提现）',
  `achievement_amount` decimal(10,4) DEFAULT NULL COMMENT '推广业绩总额(订单销售总额)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户钱包余额表';

-- ----------------------------
-- Table structure for butler_user_balance_detail
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_balance_detail`;
CREATE TABLE `butler_user_balance_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `amount` decimal(10,4) DEFAULT NULL,
  `amount_type` int(11) DEFAULT NULL COMMENT '1平台推广佣金2店铺推广佣金3.特殊商品优惠返现4充值 5商品销售收入 6提现 7消费 8 推广首单奖励',
  `object_id` int(11) DEFAULT NULL COMMENT '关联对应业务的ID（1-orderId2-orderId3-orderId4-充值id 5 orderId 6提现id7-orderId）',
  `in_out_flag` int(11) DEFAULT NULL COMMENT '1收入 2支出',
  `frozen_flag` int(11) DEFAULT NULL COMMENT '订单支付但是没有结束的佣金1冻结 2正常，3取消',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `amount_type` (`amount_type`) USING BTREE,
  KEY `object_id` (`object_id`) USING BTREE,
  KEY `in_out_flag` (`in_out_flag`) USING BTREE,
  KEY `frozen_flag` (`frozen_flag`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2630 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户钱包收支明细表';

-- ----------------------------
-- Table structure for butler_user_level
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_level`;
CREATE TABLE `butler_user_level` (
  `level_id` smallint(4) unsigned NOT NULL AUTO_INCREMENT,
  `level_name` varchar(30) DEFAULT NULL COMMENT '头衔名称',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '等级必要金额',
  `discount` smallint(4) DEFAULT '0' COMMENT '折扣',
  `describe` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_user_message
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_message`;
CREATE TABLE `butler_user_message` (
  `rec_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` mediumint(8) unsigned NOT NULL DEFAULT '0' COMMENT '用户id',
  `message_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '消息id',
  `category` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '通知消息：0, 活动消息：1, 物流:2, 私信:3',
  `is_see` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否查看：0未查看, 1已查看',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户假删除标识,1:删除,0未删除',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '查看状态：0未查看，1已查看',
  PRIMARY KEY (`rec_id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `message_id` (`message_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for butler_user_msg_tpl
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_msg_tpl`;
CREATE TABLE `butler_user_msg_tpl` (
  `mmt_code` varchar(50) NOT NULL COMMENT '用户消息模板编号',
  `mmt_name` varchar(50) NOT NULL COMMENT '模板名称',
  `mmt_message_switch` tinyint(3) unsigned NOT NULL COMMENT '站内信接收开关',
  `mmt_message_content` varchar(255) NOT NULL COMMENT '站内信消息内容',
  `mmt_short_switch` tinyint(3) unsigned NOT NULL COMMENT '短信接收开关',
  `mmt_short_content` varchar(255) DEFAULT NULL COMMENT '短信接收内容',
  `mmt_short_sign` varchar(50) DEFAULT NULL COMMENT '短信签名',
  `mmt_short_code` varchar(50) DEFAULT NULL COMMENT '短信模板ID',
  `mmt_mail_switch` tinyint(3) unsigned NOT NULL COMMENT '邮件接收开关',
  `mmt_mail_subject` varchar(255) DEFAULT NULL COMMENT '邮件标题',
  `mmt_mail_content` text COMMENT '邮件内容',
  PRIMARY KEY (`mmt_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户消息模板';

-- ----------------------------
-- Table structure for butler_user_sign
-- ----------------------------
DROP TABLE IF EXISTS `butler_user_sign`;
CREATE TABLE `butler_user_sign` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `sign_total` int(11) NOT NULL DEFAULT '0' COMMENT '累计签到天数',
  `continue_sign_count` int(11) NOT NULL DEFAULT '0' COMMENT '连续签到天数',
  `date_month` varchar(50) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '签到时间，时间格式20170907',
  `create_time` datetime DEFAULT NULL,
  `created_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `date_month` (`date_month`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6016 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='用户签到表';

-- ----------------------------
-- Table structure for oms_config
-- ----------------------------
DROP TABLE IF EXISTS `oms_config`;
CREATE TABLE `oms_config` (
  `id` int(6) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '字典健',
  `value` varchar(500) NOT NULL DEFAULT '' COMMENT '字典值',
  `inc_type` varchar(20) NOT NULL DEFAULT '' COMMENT '字典类型（basic为k-v字典）',
  `desc` varchar(50) NOT NULL DEFAULT '' COMMENT '字典描述',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `inc_type` (`inc_type`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `value` (`value`) USING BTREE,
  KEY `desc` (`desc`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='全局字典信息表';

-- ----------------------------
-- Table structure for oms_express_company
-- ----------------------------
DROP TABLE IF EXISTS `oms_express_company`;
CREATE TABLE `oms_express_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '公司名称',
  `code` varchar(50) NOT NULL DEFAULT '' COMMENT '公司编码',
  `remark` varchar(500) NOT NULL DEFAULT '' COMMENT '说明',
  `type` varchar(50) NOT NULL DEFAULT '' COMMENT '类型',
  `nation_code` varchar(50) NOT NULL DEFAULT '' COMMENT '国家编码',
  `isDisable` int(1) NOT NULL DEFAULT '2' COMMENT '是否使用 1使用 2不使用',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `code` (`code`) USING BTREE,
  KEY `type` (`type`) USING BTREE,
  KEY `nation_code` (`nation_code`) USING BTREE,
  KEY `isDisable` (`isDisable`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=819 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_operate
-- ----------------------------
DROP TABLE IF EXISTS `oms_operate`;
CREATE TABLE `oms_operate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu` varchar(50) NOT NULL DEFAULT '' COMMENT '主模块名称',
  `subMenu` varchar(50) NOT NULL DEFAULT '' COMMENT '子模块名称',
  `operate` varchar(50) NOT NULL DEFAULT '' COMMENT '操作名称',
  `sortNo` varchar(10) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='权限信息表';

-- ----------------------------
-- Table structure for oms_region
-- ----------------------------
DROP TABLE IF EXISTS `oms_region`;
CREATE TABLE `oms_region` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '区域ID ',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '区域名称',
  `level` int(1) NOT NULL DEFAULT '0' COMMENT '区域级别：1 省/直辖市/自治区、2 市、3 区/县、4 镇/街道/乡',
  `parent_id` int(11) NOT NULL COMMENT '区域的父级ID ',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `name` (`name`) USING BTREE,
  KEY `level` (`level`) USING BTREE,
  KEY `parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47498 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_role
-- ----------------------------
DROP TABLE IF EXISTS `oms_role`;
CREATE TABLE `oms_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `desc` varchar(200) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `roleName` (`roleName`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_role_operation
-- ----------------------------
DROP TABLE IF EXISTS `oms_role_operation`;
CREATE TABLE `oms_role_operation` (
  `roleId` int(11) NOT NULL,
  `operateId` int(11) NOT NULL,
  PRIMARY KEY (`roleId`,`operateId`) USING BTREE,
  KEY `global_permission_role_ibfk1` (`operateId`) USING BTREE,
  CONSTRAINT `oms_role_operation_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `oms_role` (`id`),
  CONSTRAINT `oms_role_operation_ibfk_2` FOREIGN KEY (`operateId`) REFERENCES `oms_operate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_sms_log
-- ----------------------------
DROP TABLE IF EXISTS `oms_sms_log`;
CREATE TABLE `oms_sms_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '表id',
  `mobile` varchar(11) CHARACTER SET latin1 DEFAULT '' COMMENT '手机号',
  `session_id` varchar(128) CHARACTER SET latin1 DEFAULT '' COMMENT 'session_id',
  `add_time` int(11) DEFAULT '0' COMMENT '发送时间',
  `code` varchar(100) CHARACTER SET latin1 DEFAULT '' COMMENT '验证码',
  `seller_id` int(10) DEFAULT '0',
  `status` int(1) DEFAULT '0' COMMENT '1:发送成功,0:发送失败',
  `msg` varchar(255) DEFAULT NULL COMMENT '短信内容',
  `scene` int(1) DEFAULT '0' COMMENT '发送场景,1:用户注册,2:找回密码,3:客户下单,4:客户支付,5:商家发货,6:身份验证',
  `error_msg` text COMMENT '发送短信异常内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14765 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_staff
-- ----------------------------
DROP TABLE IF EXISTS `oms_staff`;
CREATE TABLE `oms_staff` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) COLLATE utf8_bin NOT NULL,
  `password` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '',
  `name` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  `phone` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UserName_UNIQUE` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=712 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- ----------------------------
-- Table structure for oms_staff_role
-- ----------------------------
DROP TABLE IF EXISTS `oms_staff_role`;
CREATE TABLE `oms_staff_role` (
  `roleId` int(11) NOT NULL,
  `adminId` int(11) NOT NULL,
  PRIMARY KEY (`roleId`,`adminId`) USING BTREE,
  KEY `RoleId` (`roleId`) USING BTREE,
  KEY `UserId` (`adminId`) USING BTREE,
  CONSTRAINT `oms_staff_role_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `oms_role` (`id`),
  CONSTRAINT `oms_staff_role_ibfk_2` FOREIGN KEY (`adminId`) REFERENCES `oms_staff` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for oms_wxbank
-- ----------------------------
DROP TABLE IF EXISTS `oms_wxbank`;
CREATE TABLE `oms_wxbank` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4837 DEFAULT CHARSET=utf8;
