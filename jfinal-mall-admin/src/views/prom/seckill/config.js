const config = {
    // 导航菜单
    nav: [
        { name: '即将开场', id: 0 },
        { name: '秒杀中...', id: 1 },
        { name: '活动结束', id: 2 }
    ],
    // 搜索key
    search: [
        {
            title: '活动名称',
            titleStyle: {
                width: '120px'
            },
            type: 'input',
            placeholder: '请输入活动名称',
            style: {
                width: '250px',
                margin: '0 0.5rem'
            },
            value: 'name'
        }
    ],
    // 表格key
    tableKey: [
        {
            name: '活动名称',
            type: 'name',
            width: '300'
        },
        {
            name: '开始时间',
            type: 'beginTime',
            width: '150'
        },
        {
            name: '结束时间',
            type: 'endTime',
            width: '150'
        },
        {
            name: '每人参加上限',
            type: 'buyLimit',
            width: '120'
        }
    ],
    /**
     * ==============
     *  抢购入场
     * =============
     */

    activityTableKey: [
        {
            name: '图片',
            type: 'imgPath',
            width: '80',
        }, {
            name: '商品名称',
            type: 'title',
            width: '140',
        }, {
            name: '规格',
            type: 'key_name',
            width: '110',
        }, {
            name: '商品价格',
            type: 'goodPrice',
            width: '',
        }, {
            name: '抢购价格',
            type: 'price',
            width: '',
        }, {
            name: '抢购积分',
            type: 'point',
            width: '',
        }, {
            name: '积分门槛',
            type: 'pointLimit',
            width: '',
        }, {
            name: '抢购库存',
            type: 'goodNum',
            width: '',
        }, {
            name: '每人限购',
            type: 'buyLimit',
            width: ''
        }, {
            name: '已购数量',
            type: 'buyNum',
            width: '',
        }, {
            name: '下单数量',
            type: 'orderNum',
            width: '',
        }, {
            name: '虚假抢购',
            type: 'fakeBuyNum',
            width: '',
        }, {
            name: '商品类型',
            type: 'isFake',
            width: '',
        }, {
            name: '商品状态',
            type: 'offSaleStatus',
            width: '',
        }
    ],
    // 搜索key
    activitySearchKey: [
        {
            title: '商品名称',
            titleStyle: {
                width: '120px'
            },
            type: 'input',
            placeholder: '请输入商品名称',
            style: {
                width: '250px',
            },
            value: 'goods'
        }

    ],

    // form表单配置
    activityformKey: [
        {
            type: 'search',
            inputType: 'text',
            name: '商品名字',
            val: 'goodsName',
            test: 'input',
            placeholder: '请选择商品名称'
        },
        {
            type: 'input',
            inputType: 'number',
            name: '抢购价格',
            val: 'rob_price',
            test: 'decimal',
            placeholder: '请输入抢购价格',
            modification: '元'
        },
        {
            type: 'input',
            inputType: 'number',
            name: '抢购积分',
            val: 'point',
            test: 'decimal',
            placeholder: '请输入抢购积分',
            modification: ''
        },
        {
            type: 'input',
            inputType: 'number',
            name: '积分门槛',
            val: 'pointLimit',
            test: 'decimal',
            placeholder: '请输入积分门槛',
            modification: ''
        },
        {
            type: 'input',
            name: '抢购库存',
            inputType: 'number',
            val: 'rob_number',
            test: 'int2',
            placeholder: '请输入参加抢购库存',
            modification: ''
        },
        {
            type: 'input',
            name: '每人限购',
            test: 'int',
            inputType: 'number',
            placeholder: '请输入每人限购',
            val: 'limit_number',
            modification: ''
        },
        {
            type: 'input',
            name: '虚拟抢购',
            inputType: 'number',
            test: 'int2',
            placeholder: '请输入虚拟抢购数量',
            val: 'fictitious_rob_number',
            modification: ''
        },

        {
            type: 'radio',
            name: '是否虚假商品',
            test: '',
            inputType: '',
            val: 'isFake',
            radioArr: [
                { label: '真实秒杀', index: "1" },
                { label: '虚假秒杀', index: "2" }
            ],
            modification: ''
        }
    ]
}

export default config