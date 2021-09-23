<template>
	<view class="container">
		<view class="intro">
			<image src="/static/images/customer-level/5.png"></image>
			<view class="tips">遇见~ 美好~</view>
		</view>
		<view class="bottom">
			<a style="text-decoration: none;" href="https://payjs.cn/api/openid?mchid=1613001888&callback_url=https://xxl.today/naicha_ui">
				<button type="primary" size="default" class="login-btn" open-type="getUserInfo" @getuserinfo="weiXinLogin" :disabled="logining">
					<image src="/static/images/mine/wechat.png"></image>
					微信授权登录
				</button>
			</a>
			<button type="primary" size="default" class="login-btn" v-if="appProperties.testUserLoginEnabled || $config.env == 'dev'" @tap="showPop = true" :disabled="logining">测试号登录</button>

			<view style="position: fixed;top:0px;width:100vw;height:100vh;" v-if="showPop" @tap="showPop = false"></view>
			<view v-if="showPop" style="position: fixed;top: 20vh;width: 90vw;height: 35vh; background-color: #cccccc;padding: 30px;">
				<!--弹窗-->
				<view  title="删除图片需要管理员密码" confirm-text="确定" cancel-text="取消" @cancel="cancelPop" @confirm="confirmPop">
					<input v-model="secretPassword" type="text" style="height: 50px;border: 1px solid #878889; margin: 10px;" placeholder="请输入密码" />
					<button @tap="loginForTest">登录</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	data() {
		return {
			logining: false,
			showPop: false, // 显示弹窗
			secretPassword: this.$config.env == 'dev' ? '3191794968' : ''
		};
	},
	computed: {
		...mapState(['appProperties'])
	},
	onLoad() {
		this.handlerLocalToken();
		this.handlerOpenidLogin();
	},
	methods: {
		// 通过本地缓存的token去登录
		handlerLocalToken() {
			let token = uni.getStorageSync('token');
			uni.removeStorageSync('token');
			if (token) {
				let that = this;
				that.$store.commit('setToken', token);
				that.$request('/user/info', 'get').then(result => {
					console.log('[通过token登录]');
					that.$store.commit('login', result.data);
					that.$store.commit("setToken", result.data.token);
					uni.reLaunch({
						url: '/pages/mine/mine'
					});
				});
			}
		},
		// 微信小程序登录 通过code在服务器获取微信openid登录
		weiXinLogin(e) {
			const that = this;
			that.logining = true;
			const { errMsg, userInfo } = e.detail;
			if (errMsg !== 'getUserInfo:ok') {
				uni.showModal({
					title: '提示',
					content: '您取消了授权登录，请重新授权',
					showCancel: false
				});
				return;
			}

			let weixUserInfo = e.detail.userInfo; // {avatarUrl, city, country, gender, nickname...}
			console.log('微信用户：' + JSON.stringify(weixUserInfo));
			uni.login({
				provider: 'weixin',
				success: wxres => {
					that.logining = false;
					// eg: {code: "073BdF1w3us1mV2SRk1w3MDxpH3BdF1z", errMsg: "login:ok"}
					console.log('微信登录 获取到的code=', wxres.code);
					that.$request('/user/login/weixin', 'post', {
						code: wxres.code,
						wxAvatar: weixUserInfo.avatarUrl
					}).then(result => {
						result.data.wxAvatar = weixUserInfo.avatarUrl; // 同步微信头像
						that.$store.commit('login', result.data);
						uni.reLaunch({
							url: '/pages/mine/mine'
						});
					});
				}
			});
		},
		// 通过payJS获取openid后跳转过来直接登录
		handlerOpenidLogin(){
			let that = this;
			let url = window.location.href;
			if (url.indexOf("openid") < 0)
				return;
			let openid = url.substr(url.indexOf("openid=") + 7, 28);
			that.$request('/user/login/openid/' + openid, 'post').then(result => {
				// result.data.wxAvatar = weixUserInfo.avatarUrl; // 同步微信头像
				that.$store.commit('login', result.data);
				uni.reLaunch({
					url: '/pages/mine/mine'
				});
			});
		},
		//弹窗
		confirmPop() {
			//确认
			console.log('点击了确定');
			this.showPop = false;
		},
		cancelPop() {
			//取消
			console.log('点击了取消');
			this.showPop = false;
		},
		// 测试号登陆
		loginForTest() {
			let that = this;
			if(this.secretPassword != '123'){
				this.$msg("密码错误")
				return;
			}
			console.log('测试号登录');
			that.$request('/user/login/dev?secretPassword=' + this.secretPassword, 'post').then(result => {
				that.$store.commit('login', result.data);
				uni.reLaunch({
					url: '/pages/mine/mine'
				});
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.intro {
	width: 100%;
	height: 50vh;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: space-evenly;
	font-size: $font-size-base;
	color: $text-color-assist;

	image {
		width: 200rpx;
		height: 200rpx;
	}

	.tips {
		line-height: 72rpx;
		text-align: center;
	}
}

.bottom {
	height: 20vh;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	padding: 0 40rpx;

	.login-btn {
		width: 100%;
		border-radius: 50rem !important;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 10rpx 0;

		image {
			width: 36rpx;
			height: 30rpx;
			margin-right: 10rpx;
		}
	}

	.row {
		.grid {
			width: 20%;
			image {
				width: 60rpx;
				height: 60rpx;
				margin-bottom: 10rpx;
			}
		}
	}
}
</style>
