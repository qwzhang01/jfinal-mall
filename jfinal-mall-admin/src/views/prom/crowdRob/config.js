/* jshint esversion: 6 */
const config = {
    // 导航菜单
    nav: [
        { name: '即将开场', id: 0 },
        { name: '进行中...', id: 1 },
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
            placeholder: '',
            style: {
                width: '300px',
            },
            value: 'title'
        }
    ],
    // 表格key
    tableKey: [
        {
            name: '活动名称',
            type: 'title',
            width: ''
        },
        {
            name: '开始时间',
            type: 'startTime',
            width: ''
        },
        {
            name: '结束时间',
            type: 'endTime',
            width: ''
        },
        {
            name: '开团方式',
            type: 'type',
            width: ''
        },
        {
            name: '成团人数',
            type: 'maxNum',
            width: ''
        },
        {
            name: '最小成团人数',
            type: 'minNum',
            width: ''
        }
    ],

    // 设置表单key
    setformKey: [
        {
            type: 'radio',
            inputType: '',
            name: '首页显示',
            val: 'isShow',
            radioArr: [
                { label: '显示', index: '1' },
                { label: '不显示', index: '2' }
            ]
        },
        {
            type: 'uploaderImg',
            name: '首页背景图',
            val: 'homeImg',
            test: ''

        },
        {
            type: 'uploaderImg',
            name: 'bannber图',
            val: 'bannerImg',
            test: '',

        },
        {
            type: 'uploaderImg',
            name: '规则图片',
            val: 'ruleImg',
            test: '',

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
            width: '200',
            fixed: false
        },
        {
            name: '商品名称',
            type: 'title',
            width: '',
            fixed: false
        }, {
            name: '商品数量',
            type: 'goodNum',
            width: '',
            fixed: false
        }, {
            name: '活动价格',
            type: 'price',
            width: '',
            fixed: false
        }, {
            name: '购买数量',
            type: 'buyNum',
            width: '',
            fixed: false
        }, {
            name: '下单数量',
            type: 'orderNum',
            width: '',
            fixed: false
        }, {
            name: '商品状态',
            type: 'saleStatus',
            width: '',
            fixed: false
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
            placeholder: '',
            style: {
                width: '250px',
            },
            value: 'goods'
        }

    ],
    // 表单key
    fromaddkey: [
        {
            type: 'search',
            inputType: 'text',
            name: '商品名称',
            val: 'goodsName',
            test: 'input',
            placeholder: '请输入商品关键词'
        },
        {
            type: 'input',
            inputType: 'number',
            name: '商品价格',
            val: 'rob_price',
            test: 'decimal',
            placeholder: '请输入商品价格',
            modification: '元'
        },
        {
            type: 'input',
            name: '商品数量',
            inputType: 'number',
            val: 'rob_number',
            test: 'int2',
            placeholder: '请输入商品数量',
            modification: ''
        },
        {
            type: 'input',
            name: '最多参与次数',
            test: 'int',
            inputType: 'number',
            placeholder: '请输入最多参与次数',
            val: 'limit_number',
            modification: ''
        },
    ]
}
export default config