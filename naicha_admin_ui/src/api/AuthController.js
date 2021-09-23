import request from '../utils/axiosRequest.js'

// 获取验证码
export function getCode() {
  return request({
    url: '/auth/code',
    method: 'get'
  })
}

//登录
export function login(requestUser) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: requestUser
  })
}

// 通过token直接登录
export function loginByToken(token) {
  return request({
    url: '/auth/loginByToken?token=' + token,
    method: 'post'
  })
}


//修改密码
export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/auth/password',
    method: 'put',
    data: {
      oldPassword: oldPassword,
      newPassword: newPassword
    }
  })
}

// 获取当前登录的用户信息
export function getUserInfo() {
  return request({
    url: '/auth/userInfo',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'delete'
  })
}
