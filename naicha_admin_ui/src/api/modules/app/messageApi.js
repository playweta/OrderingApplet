import request from '@/utils/axiosRequest.js'

// 消息服务
// 最新订单消息
export function resentOrderMessage(){
  return request({
    url: "/message/resentOrderMessage",
    method: "get"
  })
}

// 确认收到消息
export function confirmReceiveOrderMessage(orderNo) {
  return request({
    url: "/message/confirmReceiveOrderMessage/"+ orderNo,
    method: "delete"
  })
}
