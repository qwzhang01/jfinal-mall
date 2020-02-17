/* jshint esversion: 6 */
import { getToken, getUserId } from './cookie'

var SIGN_REGEXP = /([yMdhsm])(\1*)/g;
var DEFAULT_PATTERN = 'yyyy-MM-dd';
// 压缩图片
var Lrz = require('lrz')

function padding(s, len) {
    var len = len - (s + '').length;
    for (var i = 0; i < len; i++) { s = '0' + s; }
    return s;
};

export default {
    intToStr: function (obj) {
        Object.keys(obj).forEach(function (key) {
            let v = obj[key]
            if ('number' == typeof v) {
                obj[key] = v.toString()
            }
        });
        return obj
    },
    getQueryStringByName: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var context = "";
        if (r != null)
            context = r[2];
        reg = null;
        r = null;
        return context == null || context == "" || context == "undefined" ? "" : context;
    },
    /**
     * 日期工具
     */
    formatDate: {
        /**
         * 日期格式化工具
         * @param {*} date 
         * @param {*} pattern 
         */
        format: function (date, pattern) {
            pattern = pattern || DEFAULT_PATTERN;
            return pattern.replace(SIGN_REGEXP, function ($0) {
                switch ($0.charAt(0)) {
                    case 'y': return padding(date.getFullYear(), $0.length);
                    case 'M': return padding(date.getMonth() + 1, $0.length);
                    case 'd': return padding(date.getDate(), $0.length);
                    case 'w': return date.getDay() + 1;
                    case 'h': return padding(date.getHours(), $0.length);
                    case 'm': return padding(date.getMinutes(), $0.length);
                    case 's': return padding(date.getSeconds(), $0.length);
                }
            });
        },
        /**
         * 字符串日期转换位日期
         * @param {*} dateString 
         * @param {*} pattern 
         */
        parse: function (dateString, pattern) {
            var matchs1 = pattern.match(SIGN_REGEXP);
            var matchs2 = dateString.match(/(\d)+/g);
            if (matchs1.length == matchs2.length) {
                var _date = new Date(1970, 0, 1);
                for (var i = 0; i < matchs1.length; i++) {
                    var _int = parseInt(matchs2[i]);
                    var sign = matchs1[i];
                    switch (sign.charAt(0)) {
                        case 'y': _date.setFullYear(_int); break;
                        case 'M': _date.setMonth(_int - 1); break;
                        case 'd': _date.setDate(_int); break;
                        case 'h': _date.setHours(_int); break;
                        case 'm': _date.setMinutes(_int); break;
                        case 's': _date.setSeconds(_int); break;
                    }
                }
                return _date;
            }
            return null;
        },
        //#endregion
    },
    /**
     * 二维数组的笛卡尔积算法
     */
    cartesian: function (arr) {
        if (arr.length < 2) {
            return arr[0] || [];
        }
        return [].reduce.call(arr, (col, set) => {
            let res = [];
            col.forEach(c => {
                set.forEach(s => {
                    let t = [].concat(Array.isArray(c) ? c : [c]);
                    t.push(s);
                    res.push(t);
                });
            });
            return res;
        });
    },
    // 列表页，导出列表为Excel公共方法
    download: function (apiUrl, fileName, params) {

        let url = process.env.BASE_API + "/web" + apiUrl + '?platinum_butler=67B14728AD9902AECBA32E22FA4F6BD&unique_id=67B14728AD9902AECBA32E22FA4F6BD&userId=' + getUserId() + '&token=' + getToken()
        for (var key in params) {
            if (params[key]) {
                url += '&' + key + '=' + params[key]
            }
        }
        let link = document.createElement('a')
        link.style.display = 'none'
        link.href = url
        link.setAttribute('target', '_blank')
        link.setAttribute('download', fileName + '.xlsx')

        document.body.appendChild(link)
        link.click()
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
                    message('长传图片个数最多' + num, 'warning', that);
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
                message(item.file.name + ' 图片大于' + (imgSize / 1000) + 'M，上传失败！', 'warning', that);
                return false
            }
        }
    },
}