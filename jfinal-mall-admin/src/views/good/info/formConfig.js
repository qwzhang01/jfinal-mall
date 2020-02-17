const config = {
    goodCatProp: {
        expandTrigger: "hover",
        value: "id",
        label: "name"
    },
    goodForm: {
        /********** 基本信息 begin ************/
        goodsId: '',
        storeId: '',
        catId: [],
        catId3: '',
        goodsSn: '',
        brandId: '0',
        goodsName: '',
        goodsRemark: '',
        keywords: '',
        sort: '',
        originalImg: '',
        originalImgShow: '',
        // 商品画廊
        imgAlbum: [],
        imgShowAlbum: [],
        /**
         * 是否需要上门服务（1.无需上门服务;2.可选上门服务;3.必须上门服务）
         */
        doorServiceStatus: '',
        /********** 基本信息 end ************/

        /********** 促销信息 begin ***********/
        /**
         * 0下架1上架2违规下架
         */
        isOnSale: '',
        onSaleTime: '',

        isRecommend: '',
        isHot: '',
        /**
         * 是否包邮0否1是
         */
        isFreeShipping: '',
        pointAsMoney: '',
        // 0否1是
        isEarnPoint: '',
        giveIntegral: '',
        /********** 促销信息 end ***********/

        /**
         * 商品SKU 对应的列
         */
        skuColunm: [],
        skuData: [], // 规格的笛卡尔积
        /**
         * 商品SKU
         */
        skuItemList: [], // 选中的规格的ID
        skuItemObjList: [], // 选中的规格对象
        cartesianProduct: [], // 规格的笛卡尔积
        // [{
        //     skuComputerCode: '',
        //     /**
        //      * SKU 编码 即库存编码
        //      */
        //     skuCode: '',
        //     salePrice: '',
        //     marketPrice: '',
        //     supplyPrice: '',
        //     storeCount: '',
        //     storeCountWarn: ''
        // }]
        storeCountWorn: '',
        detailShowImgList: [],
        detailSaveImgList: []
    },
    skuDataKey: {
        marketPrice: 0,
        shopPrice: 0,
        supplyPrice: 0,
        storeCount: 0,
        showImgPath: "",
        saveImgPath: "",
        disAbled: true,
        skuComputerCode: ''
    }
}
export default config