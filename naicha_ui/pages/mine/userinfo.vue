<template>
	<view class="container d-flex flex-column">
		<view class="flex-fill form">
			<list-cell :hover="false">
				<view class="form-input w-100 d-flex align-items-center">
					<view class="label"><text style="color: red;">*</text>姓名</view>
					<view class="input flex-fill">
						<input type="text" placeholder="请填写姓名" maxlength="4" placeholder-class="text-color-assist font-size-base" 
						v-model="userInfoForm.name">
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false">
				<view class="form-input w-100 d-flex align-items-center">
					<view class="label"><text style="color: red;">*</text>手机号码</view>
					<view class="input flex-fill" >
						<input type="text" v-model="userInfoForm.phone" placeholder="请输入11位手机号码" maxlength="11" placeholder-class="text-color-assist font-size-base" >
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false">
				<view class="form-input w-100 d-flex align-items-center">
					<view class="label"><text style="color: red;">*</text>收货地址</view>
					<view class="input flex-fill" >
						<picker @change="bindPickerChange" :range="dorms">
							<text>🔍快捷填入</text>
						</picker>
						<input type="text" v-model="userInfoForm.address" placeholder="比如: 2区15舍309"  maxlength="20" placeholder-class="text-color-assist font-size-base" >
					</view>
				</view>
			</list-cell>
			<list-cell :hover="false">
				<view class="form-input w-100 d-flex align-items-center">
					<view class="label">性别</view>
					<view class="input flex-fill">
						<view class="radio-group">
							<view class="radio" :class="{'checked': userInfoForm.sex == '1'}" style="margin-right: 10rpx;" @tap="userInfoForm.sex=1">男</view>
							<view class="radio" :class="{'checked': userInfoForm.sex == '2'}"  @tap="userInfoForm.sex=2">女</view>
						</view>
					</view>
				</view>
			</list-cell>
			<view class="btn-box d-flex align-items-center just-content-center" style="margin-top: 30px;">
				<button type="primary" class="save-btn" @tap="save">保存</button>
			</view>
		</view>
	</view>
</template>

<script>
	import listCell from '@/components/list-cell/list-cell'
	
	export default {
		components: {
			listCell
		},
		data() {
			return {
				userInfo: this.$store.state.userInfo,
				userInfoForm: {
					name: '',
					phone: '',
					address: '',
					sex: 1,
				},
				dorms: [
					'2区10舍', '2区11舍', '2区12舍', '2区13舍', '2区14舍',
					'2区15舍', '2区16舍', '2区17舍', 
					'3区18舍','3区19舍','3区20舍','3区21舍','3区22舍','3区23舍'
				]
			}
		},
		onShow() {
			console.log(this.userInfo)
			this.userInfoForm = {
				name: this.userInfo.name,
				phone: this.userInfo.phone,
				address: this.userInfo.address ,
				sex: this.userInfo.sex 
			}
		},
		methods: {
			// 选择楼舍
			bindPickerChange(e){
				this.userInfoForm.address = this.dorms[e.target.value] + this.userInfoForm.address;
			},
			save() {
				let that = this;
				if(!that.userInfoForm.name || that.userInfoForm.name.length > 4){
					that.$msg("姓名格式不对")
					return false;
				}
				if(!/^1\d{10}$/.test(that.userInfoForm.phone)){
					that.$msg("请输入正确的手机号")
					return false;
				}
				if(!that.userInfoForm.address || that.userInfoForm.address == ''){
					that.$msg("请输入收货地址")
					return false;
				}
				
				that.$request("/user", "put", that.userInfoForm).then(() => {
					that.userInfo.name = that.userInfoForm.name
					that.userInfo.phone = that.userInfoForm.phone
					that.userInfo.address = that.userInfoForm.address					
					that.userInfo.sex = that.userInfoForm.sex
					that.$store.commit("setUserInfo", that.userInfo)
				})
				uni.navigateBack()
			}
		}
	}
</script>

<style lang="scss" scoped>
page {
	height: 100%;
}

.container {
	padding: 20rpx 30rpx;
}

.form {
	border-radius: 8rpx;
}

.form-input {
	.label {
		width: 160rpx;
		font-size: $font-size-base;
		color: $text-color-base;
	}
	
	.input {
	}
	
	.radio-group {
		display: flex;
		justify-content: flex-start;
		
		.radio {
			padding: 10rpx 30rpx;
			border-radius: 6rpx;
			border: 2rpx solid $text-color-assist;
			color: $text-color-assist;
			font-size: $font-size-base;
			
			&.checked {
				background-color: $color-primary;
				color: $text-color-white;
				border: 2rpx solid $color-primary;
			}
		}
	}
}
.save-btn {
	width: 90%;
	border-radius: 50rem !important;
	font-size: $font-size-lg;
}
</style>
