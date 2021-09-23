<template>
	<view class="container position-relative">
		<view style="margin-bottom: 130rpx;">
			<view class="section-1">
				<template>
					<list-cell>
						<view class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center overflow-hidden" style="font-size: 35rpx;margin-bottom:5rpx;">
								<image src="/static/images/order/location.png" style="width: 30rpx; height: 30rpx;" class="mr-10"></image>
								<text v-if="takeType == '外卖配送'">{{ userInfo.address }}</text>
								<text v-else>{{ appProperties.shopName }}</text>
							</view>
							<view class="d-flex text-color-assist font-size-sm align-items-center">
								<view class="mr-10">{{ userInfo.name }}</view>
								<view class="mr-10">{{ userInfo.sex == 1 ? '男' : '女' }}</view>
								<view class="mr-10">手机 {{ userInfo.phone }}</view>
							</view>
						</view>
					</list-cell>
				</template>
				<template v-if="takeType == '外卖配送'">
					<list-cell>
						<view class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center font-size-base text-color-base">
								<view class="flex-fill">预计送达时间</view>
								<view class="mr-10">15分钟后</view>
							</view>
						</view>
					</list-cell>
				</template>
			</view>

			<!-- 购物车列表 begin -->
			<view class="section-2">
				<view class="cart d-flex flex-column">
					<list-cell last v-for="(goods, index) in cart" :key="index">
						<view class="w-100 d-flex flex-column">
							<view class="d-flex align-items-center mb-10">
								<view class="name-and-props overflow-hidden">
									<view class="text-color-base font-size-lg">{{ goods.name }}</view>
								</view>
								<view class="d-flex flex-fill justify-content-between align-items-center text-color-base font-size-lg">
									<view>x{{ goods.number }}</view>
									<view>￥{{ goods.realPrice / 100 }}</view>
								</view>
							</view>
							<view class="text-truncate font-size-base text-color-assist">{{ goods.propertyStr }}</view>
						</view>
					</list-cell>
					<template v-if="takeType == '外卖配送'">
						<list-cell>
							<view class="w-100 d-flex font-size-base align-items-center justify-content-between">
								<view>包装费</view>
								<view>￥{{ appProperties.packingPrice / 100 }}</view>
							</view>
						</list-cell>
						<list-cell>
							<view class="w-100 d-flex font-size-base align-items-center justify-content-between">
								<view>配送费</view>
								<view>￥{{ appProperties.sendingPrice / 100 }}</view>
							</view>
						</list-cell>
					</template>
				</view>
				<list-cell>
					<view class="flex-fill d-flex justify-content-end align-items-center">
						<view>总计￥{{ totalPrice / 100 }}</view>
					</view>
				</list-cell>
			</view>
			<!-- 购物车列表 end -->

			<!-- 备注 begin -->
			<list-cell arrow last @click="goToRemark" style="margin-top: 25rpx;">
				<view class="d-flex flex-fill align-items-center justify-content-between overflow-hidden">
					<view class="flex-shrink-0 mr-20">订单备注</view>
					<view class="text-color-primary flex-fill text-truncate text-right">{{ form.remark || '点击填写备注' }}</view>
				</view>
			</list-cell>
			<!-- 备注 end -->
		</view>

		<!-- 付款栏 begin -->
		<view class="w-100 pay-box position-fixed fixed-bottom d-flex align-items-center justify-content-between bg-white">
			<view class="font-size-sm " style="margin-left: 20rpx;">支付:</view>
			<view class="flex-fill font-size-extra-lg font-weight-bold" style="color: red">￥{{ totalPrice / 100 }}</view>
			<view class="bg-primary h-100 d-flex align-items-center just-content-center text-color-white font-size-base" style="padding: 0 60rpx;" @tap="submit">下单</view>
		</view>
		<!-- 付款栏 end -->

		<modal :show="ensureAddressModalVisible" custom :mask-closable="false" width="90%">
			<view class="modal-content">
				<view class="d-flex justify-content-end">
					<image src="/static/images/pay/close.png" style="width: 40rpx; height: 40rpx;" @tap="ensureAddressModalVisible = false"></image>
				</view>
				<view class="d-flex just-content-center align-items-center" style="margin-bottom: 40px;">
					<view class="font-size-extra-lg text-color-base">请再次确认下单信息</view>
				</view>
				<view class="d-flex font-size-base text-color-base font-weight-bold align-items-center justify-content-between mb-20">
					<view>{{ userInfo.name }} {{ userInfo.sex == 1 ? '男' : '女' }}</view>
					<view>{{ userInfo.phone }}</view>
				</view>
				<view class="d-flex font-size-sm text-color-assist align-items-center justify-content-between mb-40">
					<view>{{ userInfo.address }}</view>
				</view>
				<button type="primary" class="pay_btn" @tap="createOrder">确认下单</button>
			</view>
		</modal>
	</view>
</template>

<script>
import { mapState } from 'vuex';
import listCell from '@/components/list-cell/list-cell';
import modal from '@/components/modal/modal';

export default {
	components: {
		listCell,
		modal
	},
	data() {
		return {
			form: {
				remark: ''
			},
			orderId: null,
			ensureAddressModalVisible: false,
			paying: false
		};
	},
	computed: {
		...mapState(['appProperties', 'userInfo', 'takeType', 'cart']),
		// 总价格
		totalPrice() {
			let totalPrice = 0;
			this.cart.forEach(goods => {
				totalPrice += goods.realPrice * goods.number;
			});
			if (this.takeType == '外卖配送') {
				totalPrice += this.appProperties.packingPrice + this.appProperties.sendingPrice;
			}
			return totalPrice;
		}
	},
	onLoad(option) {
		// 从填写备注页面跳转过来
		if (option.remark) {
			this.form.remark = option.remark;
		}

		this.cart = uni.getStorageSync('cart');
	},
	methods: {
		// 去给订单写备注
		goToRemark() {
			uni.navigateTo({
				url: '/pages/remark/remark?remark=' + this.form.remark
			});
		},
		submit() {
			if (this.takeType == '外卖配送') {
				this.ensureAddressModalVisible = true;
			} else {
				this.createOrder();
			}
		},
		createOrder() {
			this.ensureAddressModalVisible = false;
			let that = this;
			let phone = this.userInfo.phone;
			if (!this.userInfo.address || this.userInfo.address == '' || !phone || phone == '') {
				uni.showModal({
					content: '请先去设置收货信息',
					confirmText: '去设置',
					success: e => {
						if (e.confirm) {
							uni.navigateTo({ url: '/pages/mine/userinfo' });
						}
					}
				});
				return;
			}

			if (this.cart.length <= 0) {
				that.$msg('请选择商品');
				return;
			}

			// 拼接订单的商品信息
			let goodsPreview = '';
			let goodsTotalNum = 0;
			this.cart.forEach(goodsVO => {
				if (goodsVO.propertyStr == null || goodsVO.propertyStr == '') {
					goodsPreview += goodsVO.name + '*' + goodsVO.number + ',';
				} else {
					goodsPreview += goodsVO.name + '[' + goodsVO.propertyStr + ']' + '*' + goodsVO.number + ',';
				}
				goodsTotalNum += goodsVO.number;
			});
			// eg: 奶茶1[加冰 中杯 少量糖 ]*1,
			goodsPreview = goodsPreview.substr(0, goodsPreview.length - 1);

			let params = {
				wxOpenid: this.userInfo.wxOpenid,
				addressDetail: this.takeType == '外卖配送' ? this.userInfo.address : '到店自取',
				totalPrice: this.totalPrice,
				verifyNum: phone.substring(phone.length - 4, phone.length),
				takeType: this.takeType,
				extraInfo: this.form.remark,
				userPhone: this.userInfo.phone,
				receiver: this.userInfo.name,
				goodsPreview: goodsPreview,
				goodsTotalNum: goodsTotalNum
			};
			console.log('创建订单参数,并跳转到支付小程序去支付', params);
			that.$request('/order', 'post', params).then(data => {
				that.$store.commit('set_cart', []);
				uni.reLaunch({
					url: '/pages/take-foods/take-foods'
				});
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.container {
	padding: 30rpx;
}

.arrow {
	width: 50rpx;
	height: 50rpx;
	position: relative;
	margin-right: -10rpx;
}

.location {
	.store-name {
		font-size: $font-size-lg;
	}

	.iconfont {
		font-size: 50rpx;
		line-height: 100%;
		color: $color-primary;
	}
}

.section-1 {
	margin-bottom: 30rpx;
	.contact {
		.contact-tip {
			margin-left: 10rpx;
			border: 2rpx solid $color-primary;
			padding: 6rpx 10rpx;
			color: $color-primary;
		}
	}
}

.section-2 {
	.name-and-props {
		width: 65%;
	}
}

.payment {
	margin-bottom: 30rpx;

	.disabled {
		color: $text-color-grey;
	}

	.payment-icon {
		font-size: 44rpx;
		margin-right: 10rpx;
	}

	.checkbox {
		font-size: 36rpx;
		margin-left: 10rpx;
	}

	.checked {
		color: $color-primary;
	}
}

.pay-box {
	box-shadow: 0 0 20rpx rgba(0, 0, 0, 0.1);
	height: 100rpx;
}

.modal-content {
	.change-address-btn {
		line-height: 2;
		padding: 0 1em;
	}
	.pay_btn {
		width: 100%;
		border-radius: 50rem !important;
		line-height: 3;
	}
}
</style>
