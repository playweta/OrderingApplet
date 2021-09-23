import Vue from 'vue'
import App from './App'
import util from './common/util'
import store from './store'
import config from './config.js'

// 统一提示
function msg(title = "", duration = 1000, icon = 'none', mask = false) {
	uni.showToast({
		title: title,
		duration: duration,
		icon: icon ,// none success loading
		mask:mask
	});
}


// 异步请求数据
const request = function(path, method, data, failCallback, completeCallback){
	uni.showLoading({
		mask: true
	})
	return new Promise(resolve => {
		uni.request({
			url: config.baseUrl + path,
			data: data,
			method: method,
			timeout: 2000,
			header: {
				'Content-Type': 'application/json; charset=UTF-8',
				'token': store.getters.getToken
			},
			success: res => {
				uni.hideLoading();
				console.log("[请求][" + method + "][" + path + "] [返回结果]", res.data);
				if (res.statusCode >= 200 && res.statusCode <= 300 ) {
					if (res.data.code === 200) {
						resolve(res.data);
					} else if (res.data.code === 10001) { 
						uni.showModal({
							title: '请先登录',
							content: '尚未登录或登录已过期',
							showCancel: false,
							confirmText: '去登录',
							success: function(e){
								if (e.confirm) {
									store.commit("logout");
									uni.reLaunch({
										url: "/pages/login/login"
									})
								}
							}
						})

					} else {
						if (failCallback) {
							failCallback(res.data.message)
						} else {
							msg(res.data.message)
						}
					}
				}else { // http状态码 != 200
					msg("请求异常")
				}
			},
			fail: () => {
				uni.hideLoading();
				msg("服务器网络错误")
			}, 
			complete() {
				if(completeCallback)
					completeCallback()
			}
		})
	})
}

const prePage = function(){
	let pages = getCurrentPages();
	let prePage = pages[pages.length - 2];
}


// 挂载全局方法
Vue.config.productionTip = false
Vue.prototype.$request = request;
Vue.prototype.$msg = msg;
Vue.prototype.$prePage = prePage
Vue.prototype.$store = store
Vue.prototype.$util = util
Vue.prototype.$config = config

App.mpType = 'app'
const app = new Vue({
    ...App
})
app.$mount()
