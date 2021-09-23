<script>
import { mapMutations } from 'vuex';
export default {
	methods: {
		...mapMutations(['login'])
	},
	onLaunch: function() {},
	onShow: function(options) {
		console.log(window.location.href)
		// 如果支付方式是跳转到PayJS支付小程序进行支付
		// 针对场景值为 1038、来源 appid 为 wx959c8c1fb2d877b5、跳转到的页面 id 为发起支付的页面，则标记为支付成功、记录支付的 payjs_order_id
		if (options.scene === 1038 && options.referrerInfo && options.referrerInfo.appId === 'wx959c8c1fb2d877b5') {
			// 还应判断请求路径
			let extraData = options.referrerInfo.extraData;
			console.log(extraData)
			this.globalData.paySuccess = extraData.success;
			this.globalData.resultCode = extraData.resultCode;
			this.globalData.msg = extraData.msg;
			this.globalData.payjsOrderId = extraData.payjsOrderId;
			let that = this;
			let data = {
				"outTradeNo": extraData.outTradeNo,
				"payjsOrderId": extraData.payjsOrderId,
				"resultCode": extraData.resultCode,
				"success": extraData.success,
			}
			that.$request("/order/finishWeixinPay", "PUT", data).then(res=>{
				console.log("支付成功", res)
			})
		}
	},
	onHide: function() {
		console.log('App Hide');
	}
};
</script>

<style lang="scss">
/*每个页面公共css */
@import '~@/static/style/app.scss';
</style>
