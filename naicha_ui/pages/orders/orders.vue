<template>
	<view class="container">
		<view class="orders-list d-flex flex-column w-100" style="padding: 20rpx; padding-bottom: 0;">
			<view class="order-item" v-for="(historyOrder, index) in historyOrderList" :key="index" style="margin-bottom: 30rpx;">
				<list-cell :hover="false">
					<view class="w-100 d-flex align-items-center">
						<view class="flex-fill d-flex flex-column">
							<view class="font-size-sm text-color-assist">订单编号：{{ historyOrder.orderNo }}</view>
						</view>
						<view class="font-size-lg text-color-primary">{{ historyOrder.orderStatus }}</view>
					</view>
				</list-cell>
				<list-cell :hover="false" last>
					<view class="w-100 d-flex flex-column">
						<view class="w-100 text-truncate font-size-lg text-color-base" style="margin-bottom: 20rpx;">{{ historyOrder.goodsPreview }}</view>
						<view class="d-flex justify-content-between align-items-center" style="margin-bottom: 30rpx;">
							<view class="font-size-sm text-color-assist">{{ historyOrder.createTime }}</view>
							<view class="d-flex font-size-sm text-color-base align-items-center">
								<view style="margin-right: 10rpx;">共{{ historyOrder.goodsTotalNum }}件商品 实付</view>
								<view class="font-size-lg">￥{{ historyOrder.payPrice / 100 }}</view>
							</view>
						</view>
						<view class="d-flex align-items-center justify-content-end">
							<view style="margin-right: 10rpx;"><button type="primary" plain size="mini" @tap="detail(historyOrder.orderNo)">查看详情</button></view>
						</view>
					</view>
				</list-cell>
			</view>
		</view>
		<view style="color: #444444;display: flex; align-items: center;justify-content: space-between;padding:2rpx 20rpx;">
			<view @click="prePage()" style="text-decoration: underline; ">上一页</view>
			<view>{{ pageNo }}/{{ totalPage }}</view>
			<view @click="nextPage()" style="text-decoration: underline; ">下一页</view>
		</view>
	</view>
</template>

<script>
import listCell from '@/components/list-cell/list-cell';
export default {
	components: {
		listCell
	},
	data() {
		return {
			pageNo: 1,
			pageSize: 3,
			historyOrderList: [],
			total: 0
		};
	},
	computed: {
		totalPage() {
			return this.total / this.pageSize > Math.floor(this.total / this.pageSize) ? Math.floor(this.total / this.pageSize) + 1 : Math.floor(this.total / this.pageSize);
		}
	},
	onLoad() {
		if (!this.$store.getters.getUserInfo) {
			uni.navigateTo({ url: '/pages/login/login' });
		}
		this.getHistoryOrders();
	},
	methods: {
		getHistoryOrders() {
			let path = '/order/history/page/' + this.pageNo + '/' + this.pageSize;
			let that = this;
			this.$request(path, 'get').then(result => {
				that.historyOrderList = result.data.records;
				that.total = result.data.total;
			});
		},
		// 查看订单详情
		detail(orderNo) {
			uni.navigateTo({
				url: '/pages/orders/detail?orderNo=' + orderNo
			});
		},
		// 上一页
		prePage() {
			if (this.pageNo <= 1) return;
			this.pageNo--;
			this.getHistoryOrders();
		},
		// 下一页
		nextPage() {
			if (this.pageNo >= this.totalPage) return;
			this.pageNo++;
			this.getHistoryOrders();
		}
	}
};
</script>

<style lang="scss" scoped></style>
