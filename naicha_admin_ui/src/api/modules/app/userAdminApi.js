import request from '@/utils/axiosRequest.js'

//


// 分页查询
export function getUserAdmins(pageNo, pageSize){
  let url = "/userAdmin/page?pageNo=";
  url = url + (pageNo ? pageNo : "");
  url = url + "&pageSize=" + (pageSize ? pageSize : "");
  return request({
    url: url,
    method: "get"
  })
}

// 添加
export function addUserAdmin(userAdmin) {
  return request({
    url: "/userAdmin",
    method: "post",
    data: userAdmin
  })
}

// 更新
export function updateUserAdmin(userAdmin) {
  return request({
    url: "/userAdmin",
    method: "put",
    data: userAdmin
  })
}

// 批量删除
export function deleteUserAdmins(userAdminId) {
  return request({
    url: "/userAdmin/" + userAdminId,
    method: "delete",
    data: userAdminIdList
  })
}
