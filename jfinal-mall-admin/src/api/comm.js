/* jshint esversion: 6 */
// 公共接口
import axios from '../util/config';

/**
 * FASTDFS上传地址
 */
export const UpdateFileFast = process.env.BASE_API + '/api/file/updateFile';

/**
 * 传统图片上传方法
 */
export const UpdateFileFastFn = params => {
    return axios.post(`/file/updateFile`, params, {
        baseURL: process.env.BASE_API + '/api',
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }).then(res => res)
};