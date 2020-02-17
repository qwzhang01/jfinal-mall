/* jshint esversion: 6 */
const config = {
    search: {
        key: '',
    },
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
    colomus: [
        {
            label: "用户昵称",
            width: 80,
            props: "nickname"
        },
        {
            label: "手机号码",
            width: 80,
            props: "mobile"
        },
        {
            label: "订单金额",
            width: 60,
            props: "invest_amount"
        },
        {
            label: "奖励上限",
            width: 60,
            props: "amount"
        }, {
            label: "剩余奖励",
            width: 60,
            props: "remain_amount"
        },
        {
            label: "订单编号",
            width: 100,
            props: "order_sn"
        },
        {
            label: "订单名称",
            width: 240,
            props: "title"
        },
        {
            label: "时间",
            width: 120,
            props: "create_time"
        }

    ],
};
export default config;