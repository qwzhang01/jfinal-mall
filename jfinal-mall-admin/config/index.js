'use strict'

const path = require('path')

module.exports = {
  dev: {
    env: require('./dev.env'),
    port: 8085,
    autoOpenBrowser: true,
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      "/tomcat": {
        changeOrigin: true,
        pathRewrite: {
          '^/tomcat': ''
        },
        target: 'http://127.0.0.1:7777'
      }
    },
    cssSourceMap: false
  },
  build: {
    env: require('./prod.env'),
    index: path.resolve(__dirname, '../dist/index.html'),
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    productionSourceMap: true,
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],
    bundleAnalyzerReport: process.env.npm_config_report
  }
}