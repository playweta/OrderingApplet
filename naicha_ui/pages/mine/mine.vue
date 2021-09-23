<template>
	<view class="container" style="padding-top: 5vh;">
		<!-- service box begin -->
		<view class="service-box" >
			<view style="padding: 0 30rpx;" @tap="userinfo" >
				<!-- 用户头像和用户昵称 -->
				<view class="d-flex flex-column bg-white user-box">
					<view class="d-flex align-items-center">
						<view class="avatar" >
							<image v-if="userInfo" :src="userInfo.wxAvatar"></image>
							<image v-else src="/static/images/mine/default.png"></image>
						</view>
						<view class="d-flex flex-column flex-fill overflow-hidden">
							<view v-if="userInfo" class="font-size-lg font-weight-bold d-flex justify-content-start align-items-center">
								<view class="text-truncate">{{ userInfo.name ? userInfo.name : '新用户' }}</view>
								<view class="iconfont line-height-100"></view>
							</view>
							<view v-else class="font-size-lg font-weight-bold" >请点击授权登录</view>
						</view>
					</view>
				</view>
				<!-- user box end -->
			</view>
			
			<view class="font-size-lg text-color-base font-weight-bold" style="margin-bottom: 20rpx;">我的服务</view>
			<view class="row">
				<view class="grid" @tap="userinfo">
					<view class="image"><image src="/static/images/services/ystk.png"></image></view>
					<view>我的信息</view>
				</view>
				<view class="grid" @tap="orders">
					<view class="image"><image src="/static/images/services/wdzl.png"></image></view>
					<view>我的订单</view>
				</view>
				<view class="grid" @tap="toZaixiankefu()">
					<view class="image"><image src="/static/images/mine/lxkf.png"></image></view>
					<view>联系我们</view>
				</view>
				<view class="grid" @tap="logout">
					<view class="image"><image src="/static/images/services/djsm.png"></image></view>
					<view>退出登录</view>
				</view>
				<view class="grid" @tap="services">
					<view class="image"><image src="/static/images/mine/gdfw.png"></image></view>
					<view>更多服务</view>
				</view>
			</view>
		</view>
		<!-- service box end -->
	</view>
</template>

<script>
import { mapState } from 'vuex';
export default {
	data() {
		return {
			
		};
	},
	computed: {
		...mapState(['userInfo', "appProperties"])
	},
	onLoad() {
		let that = this;
		that.$request('/config', 'get').then(result => {
			console.log('刷新配置信息:' + JSON.stringify(result.data));
			that.$store.commit('set_app_properties', result.data);
		})
		
		if(!this.userInfo){
			// 不登录 通过本地缓存直接进入
			let userInfo = uni.getStorageSync("userInfo");
			if(userInfo){
				console.log("不登录，走本地缓存")
				this.$store.commit("login", userInfo);
			}
		}
	},
	methods: {
		login() {
			uni.navigateTo({
				url: '/pages/login/login'
			});
		},
		// 退出登录
		logout(){
			let that = this;
			if(!this.userInfo){
				this.$msg("请先登录")
				return;
			}
			uni.showModal({
				content: "是否退出登录?",
				cancelText: "取消",
				confirmText: "退出登录",
				success: function(e) {
					if(e.confirm){
						that.$request("/user/logout", "delete").then(result => {
							that.$store.commit("logout")
							uni.reLaunch({
								url:'/pages/login/login'
							})
						})
					}
				}
			})
		},
		toZaixiankefu() {
			uni.navigateTo({
				url: '/pages/services/zai_xian_ke_fu/zai_xian_ke_fu'
			});
		},
		addresses() {
			if (!this.userInfo) {
				this.login();
				return;
			}
			uni.navigateTo({
				url: '/pages/address/address'
			});
		},
		orders() {
			if (!this.userInfo) {
				this.login();
				return;
			}
			uni.navigateTo({
				url: '/pages/orders/orders'
			});
		},
		userinfo() {
			if (!this.userInfo) {
				this.login();
				return;
			}
			uni.navigateTo({
				url: '/pages/mine/userinfo'
			});
		},
		services() {
			uni.navigateTo({
				url: '/pages/services/services'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
page {
	height: auto;
	min-height: 100%;
}

.bg {
	width: 100%;
	height: calc(410 / 594 * 750rpx);
}

.hym-btn {
	position: absolute;
	top: 40rpx;
	right: 40rpx;
	color: $color-primary;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 50rem;
	font-size: $font-size-sm;
	box-shadow: 0 0 20rpx rgba(66, 66, 66, 0.1);
	&::after {
		border: 0;
	}

	image {
		width: 30rpx;
		height: 30rpx;
		margin-right: 10rpx;
	}
}

.user-box {
	position: relative;
	border-radius: 8rpx;
	margin-bottom: 30rpx;
	box-shadow: $box-shadow;
}

.avatar {
	position: relative;
	margin-top: -35rpx;
	margin-left: 35rpx;
	margin-right: 35rpx;
	width: 160rpx;
	height: 160rpx;
	border-radius: 100%;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #ffffff;
	box-shadow: 0 0 20rpx rgba($color: #000000, $alpha: 0.2);

	image {
		width: 140rpx;
		height: 140rpx;
		border-radius: 100%;
	}

	.badge {
		position: absolute;
		right: -10rpx;
		bottom: -10rpx;
		background-color: #ffffff;
		border-radius: 50rem;
		display: flex;
		align-items: center;
		justify-content: center;
		color: $color-warning;
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		box-shadow: 0 0 20rpx rgba($color: #000000, $alpha: 0.2);

		image {
			width: 30rpx;
			height: 30rpx;
		}
	}
}

.level-benefit {
	margin-left: 35rpx;
	padding: 10rpx 0 10rpx 30rpx;
	border-radius: 50rem 0 0 50rem;
}

.user-grid {
	width: 25%;
	padding: 30rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	.value {
		margin-bottom: 20rpx;
	}
}

.level-benefit-box {
	border-radius: 8rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 10rpx 8rpx rgba($color: #878889, $alpha: 0.1);
	width: 100%;
	display: flex;
	padding: 30rpx;
	flex-direction: column;
	background-color: #ffffff;

	.row {
		display: flex;
		padding: 30rpx 0 20rpx;
		justify-content: space-around;
		align-items: center;

		.grid {
			width: 20%;
			display: flex;
			flex-direction: column;
			font-size: $font-size-sm;
			color: $text-color-assist;
			align-items: center;

			image {
				width: 80rpx;
				height: 80rpx;
				margin-bottom: 10rpx;
			}
		}
	}
}

.banner {
	width: 100%;
	border-radius: 8rpx;
	margin-bottom: 30rpx;
}

.service-box {
	width: 100%;
	background-color: #ffffff;
	padding: 32rpx 30rpx 10rpx;
	box-shadow: $box-shadow;

	.row {
		display: flex;
		flex-wrap: wrap;
		color: $text-color-assist;
		font-size: $font-size-sm;
		padding-bottom: -40rpx;

		.grid {
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;
			margin-bottom: 40rpx;
			width: 25%;
			position: relative;

			.image {
				image {
					width: 80rpx;
					height: 80rpx;
					margin-bottom: 20rpx;
				}
			}

			.new-badage {
				width: 40rpx;
				height: 40rpx;
				position: absolute;
				top: 0;
				right: 30rpx;
			}
		}
	}
}
</style>
