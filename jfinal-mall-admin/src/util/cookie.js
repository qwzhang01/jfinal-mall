/* jshint esversion: 6 */
import Cookies from 'js-cookie'
const TokenKey = 'token'
const UserIdKey = 'userId'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getUserId() {
  return Cookies.get(UserIdKey)
}

export function setUserId(userId) {
  return Cookies.set(UserIdKey, userId)
}

export function removeUserId() {
  return Cookies.remove(UserIdKey)
}

export function logOut() {
  Cookies.remove(TokenKey)
  Cookies.remove(UserIdKey)
  return
}