import axios from 'axios'
import store from '../store/index'
import {MessageBox, Notification, Loading} from 'element-ui'

function loading() {
    let loading = Loading.service({  // 加载遮罩
        lock: true,
        text: '加载中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.1)'
    })
    setTimeout(() => {
        loading.close()
    }, 200);
}

// 创建axios实例
const axiosInstance = axios.create({
    baseURL: process.env.BASE_URL,
    // timeout: 1200000 // 请求超时时间，毫秒（默认2分钟
})

// request拦截器
axiosInstance.interceptors.request.use(requestInfo => {
        loading();
        requestInfo.headers['token'] = store.getters.token;
        requestInfo.headers['Content-Type'] = 'application/json;charset=UTF-8';
        return requestInfo
    },
    error => {
        return Promise.reject(error)
    }
)

// response 拦截器
axiosInstance.interceptors.response.use(response => {
    console.log("[请求api] [" + response.config.method + "] ", response.config.url, "[接口返回数据]", response.data)
    if (response.status < 200 || response.status >= 400) {
        Notification.error("未知异常");
        return Promise.reject();
    }

    const res = response.data
    if (res.code === 200)
        return res;
    else if (res.code === 10001) {
        MessageBox.alert('未登录或登录已经过期，请重新登录', {
            confirmButtonText: '去登录',
            type: 'error'
        }).then(() => {
            store.dispatch('logout').then(() => {
                location.reload()
            })
        });
    } else if (res.code === 10002) {
        Notification.error('权限不足 错误码[' + res.code + "]");
    } else
        Notification.error(res.message + ' [错误码' + res.code + ']');
    return Promise.reject();
}, () => {
    Notification.error("服务器网络异常")
    return Promise.reject();
})

export default axiosInstance
