<template>
	<view>
		<view style="width: 100vw;padding: 10px;">
			<view style="padding: 10px 10px 0;"  @tap="callPhone1">
				联系电话1: {{appProperties.lianxidianhua1}}</view>
			<view style="padding: 10px 10px 0;"  @tap="callPhone2">
				联系电话2: {{appProperties.lianxidianhua2 }}</view>
			<view style="padding: 10px 10px 0;"  @tap="setClipboardData">
				联系QQ: {{appProperties.lianxiQQ}}</view>
		</view>
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
		...mapState(['appProperties'])
	},
	onShow() {
	},
	methods: {
		// 拨打电话
		callPhone1() {
			const tel = this.appProperties.lianxidianhua1 + ''
			console.log("打电话1: " + tel)
			uni.makePhoneCall({
			    phoneNumber: '' + tel   // 坑，纯数字就不行
			});
		},
		callPhone2() {
			let tel = this.appProperties.lianxidianhua2 + '';
			console.log("打电话1: " + tel)
			uni.makePhoneCall({
				phoneNumber: '' + tel
			});
		},
		// 负责内容到剪切板
		setClipboardData() {
			let qq = this.appProperties.lianxiQQ + '';
			uni.setClipboardData({
				data: '' + qq,
				success: function() {
					
				}
			});
		}
	}
};
</script>

<style></style>
