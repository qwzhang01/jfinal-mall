const config = {
    search: {
        key: '',
        firstLeader: ''
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
    colomus: [{
        label: "会员昵称",
        width: 100,
        props: "nickname"
    }, {
        label: "真实姓名",
        width: 100,
        props: "realName"
    }, {
        label: "手机号码",
        width: 100,
        props: "mobile"
    }, {
        label: "推广人",
        width: 100,
        props: "promName"
    }, {
        label: "会员等级",
        width: 100,
        props: "level_name"
    }, {
        label: "注册时间",
        width: 100,
        props: "regTime"
    }, {
        label: "可用积分",
        width: 100,
        props: "usable_remain"
    }, {
        label: "可提现积分",
        width: 100,
        props: "withdrawable_remain"
    }, {
        label: "消费总额",
        width: 100,
        props: "consume_total"
    }, {
        label: "提现总额",
        width: 100,
        props: "withdrawable_total"
    }],
    form: {
        userId: '',
        point: '',
        isWithdrow: 1
    }
}
export default config