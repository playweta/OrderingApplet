import request from '@/utils/axiosRequest.js'

//


// 分页查询
export function getGoodsAdmins(pageNo, pageSize){
  let url = "/goodsAdmin/page?pageNo=";
  url = url + (pageNo ? pageNo : "");
  url = url + "&pageSize=" + (pageSize ? pageSize : "");
  return request({
    url: url,
    method: "get"
  })
}


// 根据商品id查询
export function getGoodsAdminById(goodsId){
  return request({
    url: "/goodsAdmin/" +goodsId,
    method: "get"
  })
}

// 添加
export function addGoodsAdmin(goodsAdmin) {
  return request({
    url: "/goodsAdmin",
    method: "post",
    data: goodsAdmin
  })
}

// 更新
export function updateGoodsAdmin(goodsAdmin) {
  return request({
    url: "/goodsAdmin",
    method: "put",
    data: goodsAdmin
  })
}

// 上架或下架商品
export function updateSellStatus(goodsId) {
  return request({
    url: "/goodsAdmin/updateSellStatus/" + goodsId,
    method: "put"
  })
}

// 批量删除
export function deleteGoodsAdmins(goodsAdminIdList) {
  return request({
    url: "/goodsAdmin/batch",
    method: "delete",
    data: goodsAdminIdList
  })
}


// ************ 商品属性 ************* //
// 设置商品的默认属性
export function setDefaultPropertyOfGoods(goodsPropertyId){
  return request({
    url: "/goodsPropertyAdmin/setDefault/" + goodsPropertyId,
    method: "put"
  })
}

// 添加商品属性
export function addGoodsProperty(goodsProperty) {
  return request({
    url: "/goodsPropertyAdmin",
    method: "post",
    data: goodsProperty
  })
}

// 更新商品属性
export function updateGoodsProperty(goodsProperty) {
  return request({
    url: "/goodsPropertyAdmin",
    method: "put",
    data: goodsProperty
  })
}


// 批量商品属性
export function deleteGoodsProperty(goodsPropertyId) {
  return request({
    url: "/goodsPropertyAdmin/" + goodsPropertyId,
    method: "delete"
  })
}
