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
        payStatus: 1,
        orderStatus: "",
        shipStatus: '',
        mastOrderNo: "",
        orderNo: "",
        mobile: "",
        storeName: "",
        nickname: "",
        leaderName: "",
        rangeDate: [],
        payDateRange: []
    },
    colomus: [
        {
            label: "用户昵称",
            width: 100,
            props: "nickname"
        },
        {
            label: "用户手机",
            width: 100,
            props: "mobile"
        },
        {
            label: "商品信息",
            props: "title",
            width: 180
        },
        {
            label: "收货信息",
            props: "shippingAddress",
            width: 200
        },
        {
            label: "用户留言",
            props: "user_note",
            width: 100
        },
        {
            label: "订单金额",
            width: 80,
            props: "orderAmount"
        },
        {
            label: "支付时间",
            width: 130,
            props: "payTime"
        }
    ],
    dict: {
        orderStatus: [
            "待确认",
            "已确认",
            "已收货",
            "已取消",
            "已完成",
            "已作废"
        ],
        payStatus: ["待支付", "已支付", "部分支付", "已退款", "拒绝退款"],
        activityType: ["", "普通订单", "秒杀订单", "拼团抽奖"],
        shipStatus: ['未发货', '已发货', '已收货']
    },
    form: {
        orderId: '',
        shippingCode: '',
        expressCompanyId: '',
        shippingPrice: ''
    }
}
export default config