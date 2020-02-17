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
        label: "姓名",
        width: 100,
        props: "nickname"
    }, {
        label: "手机号码",
        width: 100,
        props: "mobile"
    }, {
        label: "订单编码",
        width: 100,
        props: "specOrderCode"
    }, {
        label: "订单金额",
        width: 100,
        props: "specOrderAmount"
    }, {
        label: "状态",
        width: 100,
        props: "specStatus",
        formatter: (row) => {
            if (row.specStatus === 0) {
                return '未申请'
            }
            if (row.specStatus === 1) {
                return '待审核'
            }
            if (row.specStatus === 2) {
                return '已审核'
            }
            return ''
        }
    }],
    form: {
        userId: '',
        point: '',
        isWithdrow: 1
    }
}
export default config