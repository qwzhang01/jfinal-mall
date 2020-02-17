/**
 * joker
 * 订单配置
 */

export default {
  // 普通订单
  1: {
    from: [
      {
        lable: '配送方式',
        type: 'delivery',
        vModel: '',
        value: '',
        icon: 'arrow',
        selectedData: []
      },
      {
        lable: '买家留言',
        type: 'input',
        value: '',
        icon: ''
      }
    ]
  },
  // 秒杀
  2: {
    from: [
      {
        lable: '配送方式',
        type: 'delivery',
        vModel: '',
        value: '',
        icon: 'arrow',
        selectedData: []
      },
      {
        lable: '买家留言',
        type: 'input',
        value: '',
        icon: ''
      }
    ]
  },
  // 抢购
  3: {
    from: [
      // {
      //   lable: '数量',
      //   type: 'spinner',
      //   value: '1',
      //   icon: ''
      // },
      // {
      //   lable: '店铺优惠',
      //   type: 'coupon',
      //   value: '暂无',
      //   icon: 'arrow'
      // },
      {
        lable: '配送方式',
        type: '',
        value: '',
        key: 'distribution',
        icon: 'arrow'
      },
      {
        lable: '店铺优惠',
        type: '',
        value: '活动抽奖',
        icon: 'arrow'
      },
      {
        lable: '店铺地址',
        type: '',
        value: '',
        key: 'storeAddress',
        icon: 'arrow'
      },
      {
        lable: '联系电话',
        type: '',
        value: '',
        key: 'servicePhone',
        icon: 'arrow'
      },
      // {
      //   lable: '开具发票',
      //   type: '',
      //   value: '',
      //   icon: 'arrow'
      // },
      {
        lable: '买家留言',
        type: 'input',
        value: '',
        key: '',
        icon: ''
      }
    ]
  },
  // 送锁
  4: {
    from: [
      {
        lable: '售后服务',
        type: 'delivery',
        value: '',
        key: 'installation',
        icon: 'arrow'
      },
      {
        lable: '安装信息',
        type: 'installationInfo',
        key: 'lockAddress',
        doorAddressId: '',
        value: '请选择安装地址',
        icon: 'arrow'
      },
      {
        lable: '安装时间',
        type: 'picker',
        value: '',
        key: 'installationTime',
        icon: 'arrow'
      }
    ]
  }
}
