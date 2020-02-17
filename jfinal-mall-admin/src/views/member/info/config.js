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
        label: "推荐人",
        width: 100,
        props: "promName"
    }, {
        label: "等级",
        width: 100,
        props: "level_name"
    }, {
        label: "状态",
        width: 100,
        props: "status",
        formatter: (row) => {
            if (row.status === 1) {
                return '待审核'
            }
            if (row.status === 2) {
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