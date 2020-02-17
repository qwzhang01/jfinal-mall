const config = {
    page: {
        // 页码
        pageNumber: 1,
        // 每页显示条数
        pageSize: 10,
        // 总页面
        totalPage: 1,
        // 总条数
        totalRow: 0
    },
    search: {
        activityType: "1",
        goodName: "",
        payStatus: "",
        orderStatus: "",
        mastOrderNo: "",
        orderNo: "",
        mobile: "",
        storeName: "",
        nickname: "",
        leaderName: "",
        rangeDate: []
    },
    colomus: [
        {
            label: "用户昵称",
            width: 130,
            props: "nickname"
        },
        {
            label: "用户手机",
            width: 130,
            props: "mobile"
        },
        {
            label: "推广人",
            width: 130,
            props: "leaderName"
        },
        {
            label: "商品信息",
            props: "title",
            width: 380
        },
        {
            label: "商品价格",
            width: 100,
            props: "goodPrice"
        },
        {
            label: "支付金额",
            width: 100,
            props: "orderAmount"
        },
        {
            label: "消耗积分",
            width: 100,
            props: "point_as_money"
        },
        {
            label: "支付状态",
            width: 100,
            props: "payStatus"
        },
        {
            label: "订单类型",
            width: 100,
            props: "orderStatus"
        },
        {
            label: "促销类型",
            width: 100,
            props: "activityType"
        },
        {
            label: "支付时间",
            width: 150,
            props: "payTime"
        },
        {
            label: "支付方式",
            width: 120,
            props: "payName"
        },
        {
            label: "下单时间",
            width: 150,
            props: "orderTime"
        },
        {
            label: "用户留言",
            props: "user_note",
            width: 100
        },
    ],
    dict: {
        orderStatus: [
            "正常订单",
            "",
            "",
            "取消订单",
            "完成订单",
            "退款订单"
        ],
        payStatus: ["待支付", "已支付", "部分支付", "已退款", "拒绝退款"],
        activityType: ["", "普通订单", "秒杀订单", "拼团抽奖"]
    }
}
export default config