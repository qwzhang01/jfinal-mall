/**
 * joker
 * 我要开店
 */
const cart = {
  state: {
    // 我要开店
    step1: {
      user: '',
      mobile: ''
    },
    // 填写店铺
    step2: {
      // 店铺图片
      savePath: '',
      showPath: '',
      // 店铺名称
      storeName: '',
      // 店铺描述
      storeDescribe: ''
    },
    // 提交认证资料
    step3: {
      // 店铺类型
      storeType: '',
      storeTypeName: '',
      // 主营类目
      category: '',
      // 主营类目详情
      categoryDetails: '',
      // 经营地址
      ausinessAddress: '',
      // 地址Id
      districtId: '',
      // 详细地址
      detailedAddress: ''
    },
    // 身份认证
    step4: {
      // 正面
      card_just: {
        fileList: [],
        savePath: '',
        showPath: ''
      },
      // 反面
      card_back: {
        fileList: [],
        savePath: '',
        showPath: ''
      },
      // 手持
      hold_card: {
        fileList: [],
        savePath: '',
        showPath: ''
      }
    },
    // 营业执照
    step5: {
      code: '',
      license: {
        fileList: [],
        savePath: '',
        showPath: ''
      },
      selectCertificates: 1, // 默认为固定期限有效
      years: '',
      time: ''
    },
    // 银行卡
    step6: {
      // 银行卡号
      code: '',
      // 正面
      bank_just: {
        fileList: [],
        savePath: '',
        showPath: ''
      },
      // 反面
      bank_back: {
        fileList: [],
        savePath: '',
        showPath: ''
      }
    }
  },
  mutations: {
    // 步骤一
    STEPFUN (state, newData) {
      state.step1 = Object.assign(state.step1, newData)
    },
    // 添加店铺信息
    ADDSTORE (state, newData) {
      state.step2 = Object.assign(state.step2, newData)
    },
    // 提交认证资料
    ATTESTATIONSTORE (state, newData) {
      state.step3 = Object.assign(state.step3, newData)
    },
    // 身份认证
    STORESTEP4 (state, newData) {
      state.step4 = Object.assign(state.step4, newData)
    },
    // 营业执照
    STORESTEP5 (state, newData) {
      state.step5 = Object.assign(state.step5, newData)
    },
    // 银行卡
    STORESTEP6 (state, newData) {
      state.step6 = Object.assign(state.step6, newData)
    }
  },
  actions: {},
  getters: {}
}

export default cart
