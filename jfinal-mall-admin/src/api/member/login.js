/* jshint esversion: 6 */
import service from '@/util/config'

export function login(username, password, safecode_iput) {
  return service({
    url: '/staff/login',
    method: 'post',
    params: {
      username,
      password,
      safecode_iput
    }
  })
}

export function logout() {
  return service({
    url: '/staff/logout',
    method: 'post'
  })
}