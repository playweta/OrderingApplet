import request from '@/utils/axiosRequest.js'

export function getAllSysUsers(){
  return request({
    url: "/sysUser/list",
    method: "get"
  })
}

// 添加管理员
export function addSysUser(user) {
  return request({
    url: "/sysUser/",
    method: "post",
    data: user
  })
}

// 更新管理员
export function updateSysUser(user) {
  return request({
    url: "/sysUser",
    method: "put",
    data: user
  })
}

// 批量删除管理员
export function deleteSysUsers(sysUserIds) {
  return request({
    url: "/sysUser/batch",
    method: "delete",
    data: sysUserIds
  })
}

// 更新账号激活状态
export function updateStatus(sysUserId, status) {
  return request({
    url: "/sysUser/status/" + sysUserId + "/" + status,
    method: "put"
  })
}

// 重置密码
export function resetPassword(sysUserId){
  return request({
    url: "/sysUser/resetPassword/" + sysUserId,
    method: "put"
  })
}


// 设置角色
export function setRole(userId, roleId){
  return request({
    url: "/sysUser/role/" + userId + "/" + roleId,
    method: "put"
  })
}
