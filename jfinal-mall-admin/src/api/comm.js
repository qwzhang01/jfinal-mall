/* jshint esversion: 6 */
import service from '../util/config';

/**
 * FASTDFS上传地址
 */
export const UpdateFileFast = process.env.BASE_API + '/web/file/updateFile';

/**
 * Base64上传方法
 */
export function UpdateFileFastFn(data) {
    return service({
        url: '/file/updateFile',
        method: 'post', headers: {
            'Content-Type': 'multipart/form-data',
        },
        data
    })
}