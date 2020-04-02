/* jshint esversion: 6 */
import service from '../util/config';

/**
 * FASTDFS上传地址
 */
export const UpdateFileFast = process.env.BASE_API + '/web/file/updateFile';

/**
 * 传统图片上传方法
 */
export function UpdateFileFastFn(params) {
    return service({
        url: '/article/detail',
        method: 'post',
        params,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}