import request from '@/utils/axiosRequest.js'


// 分页查询
export function getOrderInfoAdmins(pageNo, pageSize, orderStatus = null){
  let url = "/orderInfoAdmin/page?pageNo=";
  url = url + (pageNo ? pageNo : "");
  url = url + "&pageSize=" + (pageSize ? pageSize : "");
  if(orderStatus)
    url += "&orderStatus=" + orderStatus
  return request({
    url: url,
    method: "get"
  })
}

// 进入订单的下一步
export function toNextOrderStatus(orderNo, currentStatus) {
  return request({
    url: "/orderInfoAdmin/nextStatus/" + orderNo + "/" + currentStatus,
    method: "post"
  })
}

// 查询订单状态
export function queryWeixinOrder(orderNo){
  return request({
    url: "/orderInfoAdmin/wxPayStatus/" + orderNo
  })
}

// 取消没有付款的订单
export function cancelOrderNotPay(orderNo){
  return request({
    url: "/orderInfoAdmin/cancelOrderNotPay/" + orderNo,
    method: "put"
  })
}

// 取消订单并退款
export function cancelAndRefund(orderNo, reason){
  return request({
    url: "/orderInfoAdmin/cancelAndRefund/" + orderNo + "?reason=" + reason,
    method: 'put'
  })
}
