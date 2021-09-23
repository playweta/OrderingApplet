<template>
	<view class="container">
		<view v-if="!handlingOrders || !handlingOrders.length" class="d-flex w-100 h-100 flex-column just-content-center align-items-center">
			<view class="tips d-flex flex-column align-items-center font-size-base text-color-assist">
				<view>您还没有点单</view>
				<view>快去犒劳一下自己吧~</view>
			</view>
			<button type="primary" class="drink-btn" size="default" @tap="menu">去点餐</button>
			<view class="font-size-sm text-color-primary" @tap="orders">查看历史订单</view>
		</view>

		<template v-else>
			<view v-for="(order, index) in handlingOrders" :key="index">
				<view class="order-box">
					<view class="bg-white">
						<view class="section">
							<list-cell :hover="false" padding="50rpx 30rpx">
								<view class="w-100 d-flex flex-column">
									<!-- steps begin -->
									<view class="d-flex just-content-center">
										<view class="steps d-flex flex-column">
											<view class="steps__img-column">
												<view class="steps__img-column-item">
													<image src="/static/images/order/ordered_selected.png" v-if="order.orderStatus == '未付款'"></image>
													<image src="/static/images/order/ordered.png" v-else></image>
												</view>
												<view class="steps__img-column-item">
													<image src="/static/images/order/production_selected.png" v-if="order.orderStatus == '制作中'"></image>
													<image src="/static/images/order/production.png" v-else></image>
												</view>
												<view class="steps__img-column-item" v-if="order.takeType == '外卖配送'">
													<image
														src="/static/images/order/delivery_selected.png"
														v-if="order.orderStatus == '配送中' || order.orderStatus == '已送达'"
													></image>
													<image src="/static/images/order/delivery.png" v-else></image>
												</view>
												<view class="steps__img-column-item" v-else>
													<image src="/static/images/order/delivered_selected.png" v-if="order.orderStatus == '请取餐'"></image>
													<image src="/static/images/order/delivered.png" v-else></image>
												</view>
											</view>
											<view class="steps__text-column ">
												<view class="steps__text-column-item">
													<view class="steps__column-item-line bg-transparent"></view>
													<view class="steps__text-column-item-text" style="color: lightgrey;">未付款</view>
													<view class="steps__column-item-line"></view>
												</view>
												<view class="steps__text-column-item">
													<view class="steps__column-item-line"></view>
													<view class="steps__text-column-item-text" style="color: lightgrey;">制作中</view>
													<view class="steps__column-item-line"></view>
												</view>
												<view class="steps__img-column-item" v-if="order.takeType == '外卖配送'">
													<view class="steps__column-item-line"></view>
													<view class="steps__text-column-item-text" style="color: lightgrey;">
														{{ order.orderStatus == '已送达' ? '已送达' : '配送中' }}
													</view>
													<view class="steps__column-item-line bg-transparent"></view>
												</view>
												<view class="steps__text-column-item" v-else>
													<view class="steps__column-item-line"></view>
													<view class="steps__text-column-item-text" style="color: lightgrey;">请取餐</view>
													<view class="steps__column-item-line bg-transparent"></view>
												</view>
											</view>
										</view>
									</view>
									<!-- steps end -->

									<!-- 订单商品列表 start -->
									<view class="w-100 d-flex flex-column position-relative mt-30" style="margin-bottom: -40rpx;">
										<view class="w-100 d-flex align-items-center mb-40" v-for="(orderGoods, index) in order.orderGoodsList" :key="index">
											<view class="d-flex flex-column w-60 overflow-hidden">
												<view class="font-size-lg text-color-base mb-10 text-truncate">{{ orderGoods.goodsName }}</view>
												<view class="font-size-sm text-color-assist text-truncate">{{ orderGoods.goodsProperty }}</view>
											</view>
											<view class="d-flex w-40 align-items-center justify-content-between pl-30">
												<view class="font-size-base text-color-base">{{ orderGoods.buyNum }}</view>
												<view class="font-size-base text-color-base font-weight-bold">￥{{ orderGoods.realPrice / 100 }}</view>
											</view>
										</view>
									</view>
									<!-- 订单商品列表 end -->
								</view>
							</list-cell>
						</view>
						<view class="section">
							<!-- payment and amount begin -->
							<list-cell :hover="false" padding="50rpx 30rpx">
								<view class="w-100 d-flex flex-column">
									<view class="pay-cell">
										<view>金额总计</view>
										<view class="font-weight-bold">￥{{ order.totalPrice / 100 }}</view>
									</view>
									<view class="pay-cell">
										<view>实际支付</view>
										<view class="font-weight-bold">￥{{ order.payPrice / 100 }}</view>
									</view>
								</view>
							</list-cell>
							<!-- payment and amount end -->
						</view>

						<view class="section">
							<!-- order info begin -->
							<list-cell :hover="false" padding="50rpx 30rpx">
								<view class="w-100 d-flex flex-column">
									<view class="pay-cell">
										<view>订单编号</view>
										<view class="font-weight-bold">{{ order.orderNo }}</view>
									</view>
									<view class="pay-cell">
										<view>下单时间</view>
										<view class="font-weight-bold">{{ order.createTime }}</view>
									</view>
								</view>
							</list-cell>
							<!-- order info end -->
						</view>
						<!-- 订单的其他信息 begin -->
						<list-cell :hover="false" padding="50rpx 30rpx 20rpx">
							<view class="w-100 d-flex flex-column">
								<view class="pay-cell">
									<view>取单号</view>
									<view class="font-weight-bold">{{ order.verifyNum }}</view>
								</view>
								<view class="pay-cell">
									<view>取餐方式</view>
									<view class="font-weight-bold">{{ order.takeType }}</view>
								</view>
								<view class="pay-cell">
									<view>取餐地点</view>
									<view class="font-weight-bold">{{ order.addressDetail }}</view>
								</view>
								<view class="pay-cell">
									<view>联系方式</view>
									<view class="font-weight-bold">{{ order.userPhone }}</view>
								</view>
								<view class="pay-cell">
									<view>取餐人</view>
									<view class="font-weight-bold">{{ order.receiver }}</view>
								</view>
								<view class="pay-cell">
									<view>订单备注</view>
									<view class="font-weight-bold">{{ order.extraInfo }}</view>
								</view>
							</view>
						</list-cell>
						<!-- 订单的其他信息 end -->

						<!-- 额外操作 start -->
						<list-cell :hover="false">
							<view class="">
								<button type="primary" size="mini" style="margin-right: 5px;" @tap="calcelOrder(order.orderNo)" :disabled="order.orderStatus != '未付款'">
									取消订单
								</button>
								<button type="primary" size="mini" class=" right" :disabled="paying" v-if="order.orderStatus == '未付款'" @tap="payCashier(order.orderNo)">
									去付款
								</button>
								<button
									type="primary"
									size="mini"
									class=" right"
									v-if="order.orderStatus == '请取餐' || order.orderStatus == '已送达'"
									@tap="comfirmRedeive(order.orderNo)"
								>
									确认收货
								</button>
							</view>
						</list-cell>
						<!-- 额外操作 end -->
					</view>
				</view>
			</view>
		</template>
	</view>
</template>

<script>
import listCell from '@/components/list-cell/list-cell';
import { mapState } from 'vuex';

export default {
	components: {
		listCell
	},
	data() {
		return {
			// 正在进行中的订单
			handlingOrders: [],
			paying: false
		};
	},
	onShow() {
		if (this.userInfo) {
			this.getHandlingOrders();
		}
	},
	computed: {
		...mapState(['order', 'userInfo'])
	},
	onPullDownRefresh() {
		setTimeout(() => {
			uni.stopPullDownRefresh();
		}, 300);
		this.getHandlingOrders();
	},
	methods: {
		getHandlingOrders() {
			let that = this;
			that.$request('/order/notCompleted', 'get').then(result => {
				that.handlingOrders = result.data;
				uni.stopPullDownRefresh();
			});
		},
		// 取消订单
		calcelOrder(orderNo) {
			let that = this;
			uni.showModal({
				confirmText: '确定取消',
				content: '是否取消订单',
				success: function(e) {
					if (e.confirm) {
						that.$request('/order/cancel/' + orderNo, 'delete').then(result => {
							that.$msg('取消成功');
							that.getHandlingOrders();
						});
					}
				}
			});
		},
		// 确认收货
		comfirmRedeive(orderNo) {
			let that = this;
			that.$request('/order/confirmReceive/' + orderNo, 'put').then(result => {
				that.getHandlingOrders();
				that.$msg('已确认收货');
			});
		},
		orders() {
			uni.navigateTo({
				url: '/pages/orders/orders'
			});
		},
		menu() {
			uni.switchTab({
				url: '/pages/menu/menu'
			});
		},
		// 支付订单,跳转到payjs小程序的方式支付
		payMP(orderNo) {
			let that = this;
			that.$request('/order/payMP/' + orderNo, 'GET', null, function() {
				uni.reLaunch({
					url: '/pages/take-foods/take-foods'
				});
			}).then(result => {
				uni.navigateToMiniProgram({
					appId: 'wx959c8c1fb2d877b5',
					path: 'pages/pay',
					extraData: result.data,
					success: () => {
						that.$msg('下单成功');
					},
					fail: () => {
						that.$msg('支付失败');
					}
				});
			});
		},
		// h5收银台的支付方式
		payCashier(orderNo) {
			let that = this;
			that.$request('/order/payCashier/' + orderNo, 'GET').then(result => {
				console.log(result.data)
				window.location.href = result.data;
			});
		}
	}
};
</script>

<style lang="scss" scoped>
/* #ifdef H5 */
page {
	min-height: 100%;
	background-color: $bg-color;
}
/* #endif */
.order-box {
	padding: 20rpx;
	/* #ifdef H5 */
	margin-bottom: 100rpx;
	/* #endif */
}

.drinks-img {
	width: 260rpx;
	height: 260rpx;
}

.tips {
	margin: 60rpx 0 80rpx;
	line-height: 48rpx;
}

.drink-btn {
	width: 320rpx;
	border-radius: 50rem !important;
	margin-bottom: 40rpx;
	font-size: $font-size-base;
	line-height: 3;
}

@mixin arch {
	content: '';
	position: absolute;
	background-color: $bg-color;
	width: 30rpx;
	height: 30rpx;
	bottom: -15rpx;
	z-index: 10;
	border-radius: 100%;
}

.section {
	position: relative;

	&::before {
		@include arch;
		left: -15rpx;
	}

	&::after {
		@include arch;
		right: -15rpx;
	}
}

.pay-cell {
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	font-size: $font-size-base;
	color: $text-color-base;
	margin-bottom: 40rpx;

	&:nth-last-child(1) {
		margin-bottom: 0;
	}
}

.sort-num {
	font-size: 64rpx;
	font-weight: bold;
	color: $text-color-base;
	line-height: 2;
}

.steps__img-column {
	display: flex;
	margin: 30rpx 0;

	.steps__img-column-item {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;

		image {
			width: 80rpx;
			height: 80rpx;
		}
	}
}

.steps__text-column {
	display: flex;
	margin-bottom: 40rpx;

	.steps__text-column-item {
		flex: 1;
		display: inline-flex;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: $font-size-base;
		color: $text-color-assist;

		&.active {
			color: $text-color-base;
			font-weight: bold;

			.steps__column-item-line {
				background-color: $text-color-base;
			}
		}

		.steps__column-item-line {
			flex: 1;
			height: 2rpx;
			background-color: #919293;
			transform: scaleY(0.5);
		}

		.steps__text-column-item-text {
			margin: 0 8px;
		}
	}
}
</style>
