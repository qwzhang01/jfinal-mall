/* jshint esversion: 6 */
const config = {
    search: {
        key: '',
        type: '',
        business_type: '',
        is_withdraw: '',
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
    type: [{
        key: 1,
        name: '入账'
    }, {
        key: 2,
        name: '出账'
    }],
    businessType: [{
        key: 1,
        name: '注册'
    }, {
        key: 2,
        name: '分享转发'
    }, {
        key: 3,
        name: '取消订单'
    }, {
        key: 4,
        name: '提现'
    }, {
        key: 5,
        name: '消费'
    }, {
        key: 6,
        name: '下线首单奖励'
    }, {
        key: 7,
        name: '邀请用户注册'
    }, {
        key: 8,
        name: '购买商品赠送'
    }, {
        key: 9,
        name: '提现审核拒绝退回'
    }, {
        key: 10,
        name: '线下录入'
    }],
    isWithdraw: [{
        key: 1,
        name: '可提现'
    }, {
        key: 2,
        name: '不可提现'
    }],
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
            label: "类型",
            width: 80,
            props: "type",
            formatter: function (row, column, cellValue, index) {
                let statusArray = ["", "入账", "出账"];
                return statusArray[cellValue];
            }
        },
        {
            label: "来源",
            width: 80,
            props: "business_type",
            formatter: function (row, column, cellValue, index) {
                let statusArray = ["", "注册", "分享转发", "取消订单", "提现", "消费", "下线首单奖励", "邀请用户", "购买商品赠送", "提现审核拒绝退回", "线下录入"];
                return statusArray[cellValue];
            }
        },
        {
            label: "提现类型",
            width: 80,
            props: "is_withdraw",
            formatter: function (row, column, cellValue, index) {
                let statusArray = ["", "可提现", "不可提现"];
                return statusArray[cellValue];
            }
        },
        {
            label: "业务",
            width: 150,
            props: "businessName"
        },
        {
            label: "金额",
            width: 80,
            props: "amount"
        },
        {
            label: "时间",
            width: 100,
            props: "create_time"
        },
    ],
}
export default config