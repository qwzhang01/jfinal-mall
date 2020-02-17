'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  // BASE_API: '"http://h5.sdbjgj.com/tomcat/api"',
  BASE_API: '"http://localhost:8899/api"',
  JUPT_URL: '"http://shop.sdbjgj.com"'
})
