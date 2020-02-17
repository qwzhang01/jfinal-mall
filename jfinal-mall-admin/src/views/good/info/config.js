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
        storeName: "",
        goodName: "",
        brandName: "",
        catName: "",
        saleStatus: ""
    },
    colomus: [
        {
            label: "商品图片",
            width: 120,
            props: "imgPath",
            scope: "img"
        },
        {
            label: "商品名称",
            width: 180,
            props: "goodName"
        },
        {
            label: "分类/SPU",
            width: 250,
            scope: "cat"
        },
        {
            label: "价格",
            width: 150,
            scope: "price"
        },
        {
            label: "状态",
            width: 120,
            scope: "status"
        },
        {
            label: "积分",
            width: 120,
            scope: "point"
        },
        {
            label: "排序",
            width: 60,
            props: "sort"
        },
        {
            label: "SKU",
            width: 60,
            scope: "sku"
        },
        {
            label: "店铺/品牌",
            width: 160,
            scope: "store"
        }
    ],
    skuColomus: [
        {
            label: "规格",
            props: "key_name",
        }, {
            label: "价格",
            props: "price",
        }, {
            label: "市场价格",
            props: "market_price",
        }, {
            label: "供货价格",
            props: "supply_price",
        }, {
            label: "库存",
            props: "store_count",
        }
    ]
}
export default config