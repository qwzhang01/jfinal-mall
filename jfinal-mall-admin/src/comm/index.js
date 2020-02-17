/* jshint esversion: 6 */
const comm = {
  /**
   * 消息提示 确定
   * @param message string 提示信息
   * @param type    string 提示类型 success、warning
   */
  message3: (message = '', title = '', that) => {
    that.$alert(message, title, {
      confirmButtonText: '确定',
      callback: action => {

      }
    });
  },
  /**
   * 消息提示 确定 取消
   * @param message string 提示信息
   * @param type    string 提示类型 success、warning
   */
  message2: (message = '', title = '', type = '', that) => {
    return that.$confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type
    })
  },
  /**
   * 消息提示
   * @param message string 提示信息
   * @param type    string 提示类型 success、warning
   */
  message: (message = '', type = '', that) => {
    that.$message({ message, type });
  },
  // 字符串日期转为date
  stringToDate: function (datetimeStr) {
    var mydateint = Date.parse(datetimeStr);//数值格式的时间
    if (!isNaN(mydateint)) {
      var mydate = new Date(mydateint);
      return mydate;
    }
    var mydate = new Date(datetimeStr);//字符串格式时间
    var monthstr = mydate.getMonth() + 1;
    if (!isNaN(monthstr)) {//转化成功
      return mydate;
    }//字符串格式时间转化失败
    var dateParts = datetimeStr.split(" ");
    var dateToday = new Date();
    var year = dateToday.getFullYear();
    var month = dateToday.getMonth();
    var day = dateToday.getDate();
    if (dateParts.length >= 1) {
      var dataPart = dateParts[0].split("-");//yyyy-mm-dd  格式时间             
      if (dataPart.length == 1) {
        dataPart = dateParts[0].split("/");//yyyy/mm/dd格式时间
      }
      if (dataPart.length == 3) {
        year = Math.floor(dataPart[0]);
        month = Math.floor(dataPart[1]) - 1;
        day = Math.floor(dataPart[2]);
      }
    }
    if (dateParts.length == 2) {//hh:mm:ss格式时间
      var timePart = dateParts[1].split(":");//hh:mm:ss格式时间
      if (timePart.length == 3) {
        var hour = Math.floor(timePart[0]);
        var minute = Math.floor(timePart[1]);
        var second = Math.floor(timePart[2]);
        return new Date(year, month, day, hour, minute, second);
      }
    }
    else {
      return new Date(year, month, day);
    }
  },

  /**
   * 时间大小比较
   * ps:时间戳,精确到秒即可
   */
  timeCompare: function (startTime, endTime, newTime, that) {
    if (endTime < newTime) {
      that.$message({ message: '结束时间 必须 大于 现在时间', type: 'warning' });
      return false;
    }

    if (!startTime >= endTime) {
      that.$message({ message: '起始时间必须小于结束时间', type: 'warning' });
      return false;
    }
    return true;
  }
}
export default comm