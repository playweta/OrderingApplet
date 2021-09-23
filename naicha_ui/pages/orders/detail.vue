<template>
	<view class="container" style="padding:20rpx;">
		<view style="padding-bottom: 100rpx;">
			<view class="bg-white">
				<view class="section">
					<view class="section">
						<list-cell :hover="false" padding="30rpx 30rpx">
							<view class="w-100 d-flex flex-column">
								<view style="margin-bottom: 10rpx;">商品信息</view>
								<view class="pay-cell">
									<view class="font-weight-bold">{{ orderInfo.goodsPreview }}</view>
								</view>
								<view class="pay-cell">
									<view>商品总数</view>
									<view class="font-weight-bold">{{ orderInfo.goodsTotalNum }}</view>
								</view>
							</view>
						</list-cell>
					</view>
				</view>

				<view class="section">
					<!-- payment and amount begin -->
					<list-cell :hover="false" padding="50rpx 30rpx">
						<view class="w-100 d-flex flex-column">
							<view class="pay-cell">
								<view>金额总计</view>
								<view class="font-weight-bold">￥{{ orderInfo.totalPrice / 100 }}</view>
							</view>
							<view class="pay-cell">
								<view>实际支付</view>
								<view class="font-weight-bold">￥{{ orderInfo.payPrice / 100 }}</view>
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
								<view class="font-weight-bold">{{ orderInfo.orderNo }}</view>
							</view>
							<view class="pay-cell">
								<view>订单状态</view>
								<view class="font-weight-bold">{{ orderInfo.orderStatus }}</view>
							</view>
							<view class="pay-cell">
								<view>下单时间</view>
								<view class="font-weight-bold">{{ orderInfo.createTime }}</view>
							</view>
							<view class="pay-cell">
								<view>支付时间</view>
								<view class="font-weight-bold">{{ orderInfo.payTime ? orderInfo.payTime : '未支付'  }}</view>
							</view>
							<view class="pay-cell">
								<view>微信交易号</view>
								<view class="font-weight-bold">{{ orderInfo.wxPayTransactionId ? orderInfo.wxPayTransactionId : '未支付' }}</view>
							</view>
						</view>
					</list-cell>
					<!-- order info end -->
				</view>
				<!-- order other info begin -->
				<list-cell :hover="false" padding="50rpx 30rpx 20rpx" last>
					<view class="w-100 d-flex flex-column">
						<view class="pay-cell">
							<view>取单号</view>
							<view class="font-weight-bold">{{ orderInfo.verifyNum }}</view>
						</view>
						<view class="pay-cell">
							<view>订餐方式</view>
							<view class="font-weight-bold">{{ orderInfo.takeType }}</view>
						</view>
						<view class="pay-cell">
							<view>完成时间</view>
							<view class="font-weight-bold">{{ orderInfo.finishTime ? orderInfo.finishTime : '未完成'}}</view>
						</view>
						<view class="pay-cell">
							<view>联系电话</view>
							<view class="font-weight-bold">{{ orderInfo.userPhone }}</view>
						</view>
						<view class="pay-cell">
							<view>取餐人</view>
							<view class="font-weight-bold">{{ orderInfo.receiver }}</view>
						</view>
						<view style="margin-bottom: 10rpx;">订单备注</view>
						<view class="pay-cell">
							<view class="font-weight-bold">{{ orderInfo.extraInfo }}</view>
						</view>
					</view>
				</list-cell>
				<!-- order other info end -->
			</view>
			<view class="position-relative w-100"><image src="/static/images/order/bottom.png" mode="widthFix" class="w-100"></image></view>
		</view>
	</view>
</template>

<script>
import Orders from '@/api/orders';
import listCell from '@/components/list-cell/list-cell';

export default {
	components: {
		listCell
	},

	data() {
		return {
			orderInfo: {}
		};
	},
	onLoad({ orderNo }) {
		if (orderNo) this.getOrderInfo(orderNo);
	},
	methods: {
		getOrderInfo(orderNo) {
			this.$request('/order/detail/' + orderNo, 'get').then(result => {
				this.orderInfo = result.data;
				console.log(JSON.stringify(result.data));
			});
		},
		review() {
			uni.navigateTo({
				url: '/pages/review/review'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
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

.invote-box {
	position: absolute;
	width: 100%;
	left: 0;
	top: 0;
	display: flex;
	justify-content: center;
	align-items: center;

	image {
		width: 30rpx;
		height: 30rpx;
	}
}
</style>
