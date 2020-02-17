/* jshint esversion: 6 */

const host = require('../../config/domainConfig')
const { Dialog, ImagePreview } = require('vant')
var MobileDetect = require('mobile-detect')
var api = require('../../api/')
var Clipboard = require('clipboard')
// 压缩图片
var Lrz = require('lrz')
// 图片规定大小 5M
const imgSize = 1024 * 1024 * 5
// 微信公众号appid 白金管家
const Appid = 'wx29981dbed8394094'
/**
 * 验证是否登录
 * that：vue指针
 * Url：地址返回路径
 * jump: 取消后跳转的地址
 */
const testoginFun = (that, Url = '/', jump = '/') => {
  if (sessionStorage.getItem('userId') && sessionStorage.getItem('token')) {
    return true
  }
  Dialog.confirm({
    title: '温馨提示',
    message: '您还未登录，是否前往登录？'
  }).then(res => {
    if (res === 'confirm') {
      sessionStorage.setItem('recordUrl', Url)
      that.$router.push({
        path: '/login',
        query: {}
      })
    }
  }).catch(() => {
    if (jump) {
      that.$router.push({
        path: jump,
        query: {}
      })
    }
  })
  return false
}
// 图片预览
const ImagePreviewFun = (arrImg, key) => {
  ImagePreview({
    images: arrImg,
    startPosition: parseInt(key)
  })
}

// 公共的方法
const commJs = {
  host: host,
  /**
   * 跳转(老版本)
   * data={
   *  @param item 点击图片的参数
   *  @param key  点击图片的参数的索引
   *  @param arr  全部图片
   *  @param type images:图片，info：资讯
   * }
   * @param that  vue对象
   * @param suuper 父级路由
   * 跳转的参数说明：0：预览 1.咨询 2活动  3: 商品 4：商品分类 5：web链接  6：模块分类 7:搜索 8:视频
   */
  jump: function (data, that, suuper = '/') {
    // 是否存在商品ID
    if (data.item.hasOwnProperty('goods_id') || data.item.hasOwnProperty('goodId')) {
      that.$router.push({
        path: `/goodsinfo?goodId=${data.item.goods_id ? data.item.goods_id : data.item.goodId}&u=${suuper}`
      })
      return
    }
    let typeJump = parseInt(data.item.jump_type)
    // 预览
    if (typeJump === 0) {
      // 获取图片
      var arr = []
      data.arr.forEach((value, index, array) => {
        arr.push(value.image)
      })
      ImagePreview({
        images: arr,
        startPosition: parseInt(data.key)
      })
    } else if (typeJump === 1) {
      that.$router.push({
        path: '/info',
        query: {
          id: data.item.article_id
        }
      })

      // 资讯
      console.log('资讯')
    } else if (typeJump === 2) {
      // 活动
      console.log('活动')
    } else if (typeJump === 3) {
      console.log('商品')
    } else if (typeJump === 4) {
      // 商品分类
      that.$router.push({
        path: '/categoryList?id=' + data.item.relate_id
      })
      console.log('商品分类')
    } else if (typeJump === 5) {
      // 是否是邀请有礼
      if (data.item.relate_id === 'invite_qr_code') {
        if (sessionStorage.getItem('userId')) {
          window.location.href = `${host.jump_domain}/vue/#/lock?user_id=${sessionStorage.getItem('userId')}`
        } else {
          that.$router.push({
            path: '/lgion'
          })
        }
        return false
      }
    } else if (typeJump === 6) {
      // 模块分类
      console.log('模块分类')
    } else if (typeJump === 7) {
      // 搜索跳转
      that.$router.push({
        path: '/search?u=/'
      })
      console.log('搜索跳转')
    } else if (typeJump === 8) {
      // 视频
    }
  },
  /**
   * 跳转
   * data={
   *  @param item 点击图片的参数
   *  @param key  点击图片的参数的索引
   *  @param arr  全部图片
   *  @param type images:图片，info：资讯
   * }
   * @param that  vue对象
   * @param suuper 父级路由
   * 跳转的参数说明：0：预览 1: 资讯 2：商品详情 3：商品分类 4：WEB地址 5：店铺首页 6：专题页 7：推荐店铺 8：邀请有礼 9：拼团抽奖 10：秒杀 11:积分中心 12:积分购买商品 13:商品奖励积分
   */
  jumpNew: function (data, that, suuper = '/') {
    // 没有登录，转登录页
    if (this.testLogin(that, suuper, false) === false) {
      return false
    }
    let type = data.item.gotoType
    // 图片预览
    if (type === 0) {
      let arrImg = []
      if (data.arr.length > 0) {
        arrImg.push(data.arr.map(item => {
          return item.img
        }))
        ImagePreviewFun(arrImg[0], data.key)
      }
    } else if (type === 1) {
      console.log('资讯')
      that.$router.push({
        path: '/info?id=' + data.item.gotoId
      })
    } else if (type === 2) {
      console.log('商品详情')
      that.$router.push({
        path: `/goodsinfo?goodId=${data.item.gotoId}&u=/`
      })
    } else if (type === 3) {
      console.log('商品分类')
      console.log(data.item.title)
      // `/result?text=${item.name}&id=${item.id}&u=/`
      that.$router.push({
        path: '/result?text=' + data.item.title + '&id=' + data.item.gotoId + '&u=/'
      })
    } else if (type === 4) {
      console.log('WEB地址')
      window.location.href = data.item.webUrl
    } else if (type === 5) {
      console.log('店铺首页')
      that.$router.push({
        path: '/storepage?store_id=' + data.item.gotoId + '&u=/'
      })
    } else if (type === 6) {
      console.log('专题页')
    } else if (type === 7) {
      console.log('推荐店铺')
      that.$router.push({
        path: '/recommendStore'
      })
    } else if (type === 8) {
      console.log('邀请有礼')
      that.$router.push({
        path: '/retail/invite'
      })
    } else if (type === 9) {
      console.log('拼团抽奖')
      that.$router.push({
        path: '/robShoppingList'
      })
    } else if (type === 10) {
      console.log('秒杀')
      that.$router.push({
        path: '/flash_sale_list'
      })
    } else if (type === 11) {
      console.log('积分中心')
      that.$router.push({
        path: '/retail',
        query: {
          u: '/'
        }
      })
    } else if (type === 12) {
      console.log('积分购买商品')
      that.$router.push({
        path: '/retail/share?text=积分换购&from=index&pointAsMoney=true'
      })
    } else if (type === 13) {
      console.log('商品奖励积分')
      that.$router.push({
        path: '/retail/share?text=购买赚积分&from=index&isEarnPoint=1'
      })
    }
    return true
  },
  /**
   * 获取设备信息
   */
  equipment: () => {
    // 获取userAgent信息
    var deviceType = navigator.userAgent
    // 实例化mobile-detect
    var md = new MobileDetect(deviceType)
    // 获取系统
    var os = md.os()
    var model = ''
    // ios系统的处理
    if (os === 'iOS') {
      os = md.os() + md.version('iPhone')
      model = md.mobile()
    } else if (os === 'AndroidOS') {
      // Android系统的处理
      os = md.os() + md.version('Android')
    }
    // 打印系统版本和手机型号
    console.log(os + '---' + model)
    md.width_rem = (document.body.clientWidth / 16)
    md.height_rem = (document.body.clientHeight / 16)
    md.width = document.body.clientWidth
    md.height = document.body.clientHeight
    // console.log(md)
    return md
  },
  /**
   * 提示 1 (title = '', message = '')
   */
  Dialog: function (title = '', message = '') {
    return Dialog.confirm({
      title: title,
      message: message
    })
  },

  /**
   * 提示 2
   * @param message   string 提示语
   * @param icon      string 标签 success、error
   * @param time      array  时间
   */
  Dialog2: (message = '', icon = '', time = 1500, that) => {
    that.$dialog.toast({
      mes: message,
      timeout: time,
      icon: icon
    })
  },
  /**
   * 金额转换以及格式化
   * @description 格式化金额
   * @param number：要格式化的数字
   * @param decimals：保留几位小数 默认0位
   * @param decPoint：小数点符号 默认.
   * @param thousandsSep：千分位符号 默认为,
   */
  abs: (number, decimals = 0, decPoint = '.', thousandsSep = ',') => {
    number = (number + '').replace(/[^0-9+-Ee.]/g, '')
    let n = !isFinite(+number) ? 0 : +number
    let prec = !isFinite(+decimals) ? 0 : Math.abs(decimals)
    let sep = (typeof thousandsSep === 'undefined') ? ',' : thousandsSep
    let dec = (typeof decPoint === 'undefined') ? '.' : decPoint
    let s = ''
    let toFixedFix = function (n, prec) {
      let k = Math.pow(10, prec)
      return '' + Math.ceil(n * k) / k
    }
    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.')
    let re = /(-?\d+)(\d{3})/
    while (re.test(s[0])) {
      s[0] = s[0].replace(re, '$1' + sep + '$2')
    }
    if ((s[1] || '').length < prec) {
      s[1] = s[1] || ''
      s[1] += new Array(prec - s[1].length + 1).join('0')
    }
    return s.join(dec)
  },
  /**
   * 验证
   * @param val         string 被验证的字符串
   * @param valName     string 被验证的字符串的名字
   * @param regularType array 正则类型
   */
  test: (val, valName = '', regularType = [], that) => {
    // 字符串开头不能为空  类型为 1
    let Reg1 = /^[^ /\n]+$/g
    // 数字               类型为 2
    let Reg2 = /^[1-9]*[1-9][0-9]*$/
    // 电话号码
    let Reg3 = /^[1]{1}[0-9]{10}/
    // 只能是数字和英文
    let Reg4 = /^[a-zA-Z0-9]{6,20}/
    // 判断是否为空
    if (!val) {
      console.log(valName + '不能为空')
      that.$dialog.toast({
        mes: valName + '不能为空',
        timeout: 1500,
        icon: 'error'
      })
      return false
    }
    // 判断字符串开头是否为空
    if (!val.match(Reg1) && regularType.includes(1)) {
      that.$dialog.toast({
        mes: valName + '开头不能为空',
        timeout: 1500,
        icon: 'error'
      })
      console.log(valName + '开头不能为空')
      return false
    }

    // 判断只能是数字
    if (!val.match(Reg2) && regularType.includes(2)) {
      that.$dialog.toast({
        mes: valName + '只能是数字',
        timeout: 1500,
        icon: 'error'
      })
      console.log(valName + '只能是数字')
      return false
    }

    // 判断电话号码
    if (!val.match(Reg3) && regularType.includes(3)) {
      that.$dialog.toast({
        mes: valName + '电话格式错误',
        timeout: 1500,
        icon: 'error'
      })
      console.log(valName + '电话格式错误')
      return false
    }
    // 只能是数字和英文
    if (!val.match(Reg4) && regularType.includes(4)) {
      that.$dialog.toast({
        mes: valName + '只能是6-20位的数字和英文',
        timeout: 1500,
        icon: 'error'
      })
      console.log(valName + '只能是6-20位的数字和英文')
      return false
    }
  },
  /**
   * 银行卡号格式化
   */
  manglingFormatCardNumber: cardNumber => {
    if (cardNumber && cardNumber.length > 8) {
      return `${cardNumber.substring(0, 4)} ${'*'.repeat(cardNumber.length - 8).replace(/(.{4})/g, `
      $1 `)}${cardNumber.length % 4 ? ' ' : ''}${cardNumber.slice(-4)}`
    }
    return cardNumber
  },
  /**
   * 检查是否是微信浏览器
   */
  isWeiXin: () => {
    return navigator.userAgent.toLowerCase().indexOf('micromessenger') !== -1
  },
  /**
   * 获取微信code
   */
  wxcode: () => {
    // 跳转地址
    let urljump = window.location.href
    urljump = urljump.replace(/\/$/gi, '')
    let jump = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=' + Appid + '&redirect_uri=' + encodeURIComponent(urljump) + '&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect'
    window.location.href = jump
  },
  // 获取参数
  getQueryString: (name) => {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i')
    var r = window.location.search.substr(1).match(reg)
    if (r != null) return unescape(r[2])
    return null
  },

  /**
   * 公众号微信支付
   * @param userId    string   用户ID
   * @param amount    number   充值金额
   * @param orderSn   string   订单编号
   * @param jumpUrl   string   支付完成后跳转到指定页面
   */
  WxGzLanuch: (userId, amount = '', orderSn = '', jumpUrl = '', that, Url, jump = '/') => {
    that.$dialog.loading.open('支付中...')
    // 判断是否存在userid
    if (!userId) {
      that.$dialog.loading.close()
      return testoginFun(that, Url, jump = false)
    }
    // 先发起支付
    let params = {
      user_id: userId,
      amount,
      order_sn: orderSn
    }
    api.WxAppLanuch(params).then(res => {
      if (res.status === 0) {
        that.$dialog.loading.close()
        // 支付环境监测
        if (typeof WeixinJSBridge === 'undefined') {
          if (document.addEventListener) {
            document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false)
          } else if (document.attachEvent) {
            document.attachEvent('WeixinJSBridgeReady', onBridgeReady)
            document.attachEvent('onWeixinJSBridgeReady', onBridgeReady)
          }
        } else {
          // 支付
          onBridgeReady(res.data)
        }
      } else {
        that.$dialog.toast({
          mes: res.msg,
          timeout: 1500,
          icon: 'error'
        })
        return false
      }
    })

    let thats = that
    // 支付成功判断
    function onBridgeReady(data) {
      WeixinJSBridge.invoke(
        'getBrandWCPayRequest', data,
        function (res) {
          if (res.err_msg === 'get_brand_wcpay_request:ok') {
            // 使用以上方式判断前端返回,微信团队郑重提示：
            // res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
            // 支付成功跳转页面
            if (jumpUrl) {
              that.$dialog.toast({
                mes: '支付成功！',
                timeout: 1500,
                icon: 'success'
              })

              setTimeout(() => {
                thats.$router.push(jumpUrl)
              }, 1500)
            }
          }
        })
    }
  },
  /**
   * h5微信支付
   * @param userId    string   用户ID
   * @param amount    number   充值金额
   * @param orderSn   string   订单编号
   * @param jump_url  string   支付完成后跳转到指定页面
   */
  WxLanuch: (userId, amount = '', orderSn = '', jump_url = '', that) => {
    // 判断是否存在userid
    if (!userId) {
      return testoginFun(that, Url, jump = false)
    }
    // 先发起支付
    let params = {
      user_id: userId,
      amount,
      order_sn: orderSn,
      jump_url
    }
    api.WxAppLanuch(params).then(res => {
      if (res.status === 0) {
        console.log(res)
        window.location.href = res.data
      } else {
        that.$dialog.toast({
          mes: res.msg,
          timeout: 1500,
          icon: 'error'
        })
      }
    })
  },
  /**
   * 支付宝支付
   * @param userId    string   用户ID
   * @param amount    number   充值金额
   * @param orderSn   string   订单编号
   * @param jump_url  string   支付完成后跳转到指定页面
   */
  AliAppLanuch: (userId, amount = '', orderSn = '', jump_url = '', that) => {
    // 判断是否存在userid
    if (!userId) {
      return testoginFun(that, Url, jump = false)
    }
    // 先发起支付
    let params = {
      user_id: userId,
      amount,
      order_sn: orderSn,
      jump_url
    }
    api.AliAppLanuch(params).then(res => {
      if (res.status === 0) {
        // console.log(res)
        window.location.href = res.data
      } else {
        that.$dialog.toast({
          mes: res.msg,
          timeout: 1500,
          icon: 'error'
        })
      }
    })
  },
  /**
   * 微信支付回调
   * @param userId   string  用户ID
   * @param amount   number  充值金额
   * @param orderSn  string  订单编号
   */
  WxNotifyFun: () => {
    api.WxAppLanuch().then(res => {
      if (res.status === 0) {
        console.log(res)
        // window.location.href = res.data
      }
    })
  },
  /**
   * 手机复制
   * @param tag   string  类或者id名
   * @param data  string  复制数据
   */
  copey: (tag, data, that) => {
    let clipboard = new Clipboard(`${tag}`, {
      text: function () {
        return data
      }
    })
    clipboard.on('success', e => {
      that.$dialog.toast({
        mes: '复制成功！',
        timeout: 1500,
        icon: 'success'
      })
      // 释放内存
      clipboard.destroy()
    })
    clipboard.on('error', e => {
      that.$dialog.toast({
        mes: '复制失败！',
        timeout: 1500,
        icon: 'error'
      })
      clipboard.destroy()
    })
  },
  /**
   * 压缩图片
   * @param img     array | object  图片
   * @param num     number          上传多少张图片
   * @param width   number          图片宽度
   * @param height  number          图片高度
   * @param quality number          压缩图片程度（0-1 默认为0.7）
   * @param arr     array           图片数组
   * @param that    object          this指向
   */
  compress: function (img, num = 1, width = 0, height = 0, quality = 0.7, arr = [], that) {
    // console.log(img)
    return new Promise(function (resolve, reject) {
      // 存放处理后的图片
      var imgData = []
      // 数组
      if (img instanceof Array) {
        // 长传图片个数
        if ((img.length + arr.length) > num) {
          that.$dialog.toast({
            mes: '长传图片个数最多' + num,
            timeout: 1000,
            icon: 'error'
          })
          that.$dialog.loading.close()
          return false
        }
        // 多个图片
        img.forEach((item, key, img) => {
          // 是否大于规定的图片大小
          if (imgSizeFun(item) === false) {
            return false
          }

          lrzFun(item.content).then(res => {
            imgData.push(res.base64)
          })
        })
        setTimeout(() => {
          resolve(imgData)
        }, 500)
      } else {
        // 单个图片
        if (imgSizeFun(img) === false) {
          return false
        }

        lrzFun(img.content).then(res => {
          imgData.push(res.base64)
          setTimeout(() => {
            resolve(imgData)
          }, 500)
        })
      }
    })
    // 压缩图片
    function lrzFun(img) {
      var Img = new Image()
      Img.src = img
      var w, h
      if (width) {
        w = width
        h = height
      } else {
        w = Img.width
        h = Img.height
      }

      // 压缩属性
      let options = {
        width: w,
        height: h,
        // 图片压缩质量，取值0-1，默认为0.7
        quality
      }
      // 压缩图片
      return Lrz(img, options)
    }

    // 是否大于规定的图片大小
    function imgSizeFun(item) {
      if (item.file.size > imgSize) {
        that.$dialog.toast({
          mes: item.file.name + ' 图片大于' + (imgSize / 1000) + 'M，上传失败！',
          timeout: 1500,
          icon: 'error'
        })
        that.$dialog.loading.close()
        return false
      }
    }
  },
  // 预览图片
  viewImg: (arrImg, key) => {
    ImagePreviewFun(arrImg, key)
  },
  /**
   * idName   节点id
   * len       距离
   */
  slide: (idName, len = 0) => {
    let total = document.getElementById(idName).offsetTop - len
    let distance = document.documentElement.scrollTop || document.body.scrollTop
    // 平滑滚动，时长500ms，每10ms一跳，共50跳
    let step = total / 50
    if (total > distance) {
      smoothDown()
    } else {
      let newTotal = distance - total
      step = newTotal / 50
      smoothUp()
    }

    function smoothDown() {
      if (distance < total) {
        distance += step
        document.body.scrollTop = distance
        document.documentElement.scrollTop = distance
        setTimeout(smoothDown, 10)
      } else {
        document.body.scrollTop = total
        document.documentElement.scrollTop = total
      }
    }

    function smoothUp() {
      if (distance > total) {
        distance -= step
        document.body.scrollTop = distance
        document.documentElement.scrollTop = distance
        setTimeout(smoothUp, 10)
      } else {
        document.body.scrollTop = total
        document.documentElement.scrollTop = total
      }
    }
  },
  /**
   * 路由跳转配置
   *
   * that : vue指针
   * defaultUrl:默认路由，默认为首页
   * delet : 过滤的属性
   */
  jumpUrl: (that, defaultUrl = '', delet = []) => {
    let newUrl = ''
    let objUrl = that.$route.query
    let len = Object.keys(objUrl).length
    if (len > 0) {
      for (var i = 0; i < len; i++) {
        // 过滤不必要的属性
        if (!delet.includes(Object.keys(objUrl)[i])) {
          // 判断属性是否存在
          if (objUrl.hasOwnProperty(Object.keys(objUrl)[i])) {
            // 获取u属性，u表示返回的页面名字
            if (Object.keys(objUrl)[i] === 'u') {
              var strFirst = objUrl[Object.keys(objUrl)[i]]
            } else {
              let str = newUrl + len > 1 ? '&' : '?' + Object.keys(objUrl)[i] + '=' + objUrl[Object.keys(objUrl)[i]]
              if (str.charAt(str.length - 1) === '&') {
                str = str.slice(0, str.length - 1)
              }
              var strLast = str
            }
          }
        }
      }
      // 渐进增强
      if (!strFirst == '') {
        newUrl = newUrl + strFirst
      }
      if (!strLast == '') {
        newUrl = newUrl + strLast
      }
    }
    // console.log(newUrl)
    if (newUrl) {
      return newUrl
    } else {
      return defaultUrl
    }
  },
  /**
   * 时间戳转自定义格式
   *
   * dataTime 时间戳
   * sign     转义符号
   */
  dateHandle: (dataTime, sign = '-') => {
    function meaning(m) {
      return m < 10 ? '0' + m : m
    }
    // 时间戳是整数，否则要parseInt转换
    var time = new Date(dataTime * 1000)
    var y = time.getFullYear()
    var m = time.getMonth() + 1
    var d = time.getDate()
    var h = time.getHours()
    var mm = time.getMinutes()
    var s = time.getSeconds()
    return y + sign + meaning(m) + sign + meaning(d) + ' ' + meaning(h) + ':' + meaning(mm) + ':' + meaning(s)
  },
  /**
   * 记录搜索
   * data:搜索的值
   */
  history: data => {
    // 存储
    if (!sessionStorage.getItem('history')) {
      // 设置
      let arr = []
      arr.push(data)
      sessionStorage.setItem('history', arr.join(','))
    } else {
      let history = sessionStorage.getItem('history').split(',')
      history.push(data)
      sessionStorage.setItem('history', Array.from(new Set(history)))
    }
    this.history = sessionStorage.getItem('history').split(',')
  },

  /**
   * 验证是否登录
   * that：vue指针
   * Dialog
   * Url：地址返回路径
   * jump: 取消后跳转的地址
   */
  testLogin: (that, Url = '/', jump = '/') => {
    return testoginFun(that, Url, jump)
  },
  /**
   * 清除 不必要的数据
   */
  deletData: (dataArr = []) => {
    if (dataArr.length > 0) {
      dataArr.forEach((item) => {
        sessionStorage.removeItem(item)
      })
    }
  }
}

export default commJs
