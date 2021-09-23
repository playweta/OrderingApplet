import request from '@/utils/axiosRequest.js'

export function doTimingTask(taskMethodName){
  return request({
    url: "/timingTask/execute/" + taskMethodName,
    method: "get"
  })
}

