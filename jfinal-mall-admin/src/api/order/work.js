import service from '@/util/config'

export function all(){
    return service({
        url: '/service/worker/all',
        method: 'post'
    })
}