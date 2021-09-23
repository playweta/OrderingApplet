<template>
	<view class="container">
		<view class="main" v-if="goods">
			<view class="nav">
				<view class="header">
					<view class="left overflow-hidden">
						<view class="d-flex align-items-center overflow-hidden" v-if="takeType == '外卖配送'">
							<image src="/static/images/order/location.png" style="width: 30rpx; height: 30rpx;" class="mr-10"></image>
							<text v-if="!userInfo" @tap="toLogin()">去登录</text>
							<text v-else>{{ userInfo.address && userInfo.address != '' ? userInfo.address : '请完善用户信息' }}</text>
						</view>
					</view>
					<view class="right">
						<view class="dinein" :class="{ active: takeType != '外卖配送' }" @tap="takein"><text>到店自取</text></view>
						<view class="takeout" :class="{ active: takeType == '外卖配送' }" @tap="takeout"><text>外卖配送</text></view>
					</view>
				</view>
				<view class="coupon">
					<text class="title">{{ appProperties.shopNotice }}</text>
				</view>
			</view>
			<view class="content">
				<!-- 左侧菜单栏 -->
				<scroll-view class="menus" :scroll-into-view="menuScrollIntoView" scroll-with-animation scroll-y>
					<view class="wrapper">
						<view
							v-for="(item, index) in goods"
							class="menu"
							:class="{ current: item.goodsCategoryName === currentCategoryName }"
							:id="`menu-${item.goodsCategoryName}`"
							:key="index"
							@tap="handleMenuTap(item.goodsCategoryName)"
						>
							<text>{{ item.goodsCategoryName }}</text>
							<view class="dot" v-show="menuCartNum(item.goodsCategoryName)">{{ menuCartNum(item.goodsCategoryName) }}</view>
						</view>
					</view>
				</scroll-view>

				<!-- 右侧商品列表 begin -->
				<scroll-view class="goods" scroll-with-animation scroll-y :scroll-top="categoryScrollTop" @scroll="handleGoodsScroll">
					<view class="wrapper">
						<view class="list">
							<!-- 商品分类展示 begin -->
							<view id="categoryParent" v-for="(category, index) in goods" :key="index">
								<view class="category" id="categoryMenu">
									<view>
										<text class="title">{{ category.goodsCategoryName }}</text>
									</view>
									<view class="items">
										<!-- 商品列表 begin -->
										<view v-if="category.goodsCategoryShow" class="good" v-for="(goods, key) in category.goodsList" :key="key">
											<image :src="goodsImageBaseUrl + goods.image" class="image" @tap="showGoodDetailModal(goods)"></image>
											<view class="right">
												<text class="name">{{ goods.name }}</text>
												<text class="tips">{{ goods.description && goods.description != '' ? goods.description : goods.name }}</text>
												<view class="price_and_action">
													<text class="price">￥{{ goods.defaultPrice / 100 }}</text>
													<!-- 商品没有下架 -->
													<view v-if="goods.isSell">
														<!-- 要选商品规格详细信息 -->
														<view class="btn-group" v-if="goods.goodsPropertyVos && goods.goodsPropertyVos.length">
															<button type="primary" class="btn property_btn" hover-class="none" size="mini" @tap="showGoodDetailModal(goods)">
																选规格
															</button>
															<view class="dot" v-show="goodsCartNum(goods.id)">{{ goodsCartNum(goods.id) }}</view>
														</view>
														<!-- 可以直接添加，不用选择商品属性 -->
														<view class="btn-group" v-else>
															<button
																type="default"
																v-show="goodsCartNum(goods.id)"
																plain
																class="btn reduce_btn"
																size="mini"
																hover-class="none"
																@tap="handleReduceFromCart(goods)"
															>
																<view class="iconfont iconsami-select"></view>
															</button>
															<view class="number" v-show="goodsCartNum(goods.id)">{{ goodsCartNum(goods.id) }}</view>
															<button type="primary" class="btn add_btn" size="min" hover-class="none" @tap="handleAddToCart(goods)">
																<view class="iconfont iconadd-select"></view>
															</button>
														</view>
													</view>
													<!-- 已经下架了 -->
													<view v-else>
														<view class="btn-group">
															<button type="primary" disabled class="btn property_btn" style="text-decoration: line-through;" size="mini">
																已售空
															</button>
														</view>
													</view>
												</view>
											</view>
										</view>
										<!-- 商品 end -->
									</view>
								</view>
							</view>
							<!-- 商品分类展示 end -->
						</view>
					</view>
				</scroll-view>
				<!-- 右侧商品列表 end -->
			</view>
			<!-- content end -->
			<!-- 购物车栏 begin -->
			<view class="cart-box" v-if="cart && cart.length">
				<view class="mark" @tap="cartPopupVisible = !cartPopupVisible">
					<image src="/static/images/menu/cart.png" class="cart-img"></image>
					<view class="tag">{{ getCartGoodsNumber }}</view>
				</view>
				<view class="price" @tap="cartPopupVisible = !cartPopupVisible">￥{{ getCartGoodsPrice / 100 }}</view>
				<view v-if="!appProperties.shopStatus"><button type="primary" class="pay-btn" disabled>商家休息中</button></view>
				<view v-else-if="new Date().getHours() >= appProperties.businessStartTime && new Date().getHours() < appProperties.businessEndTime">
					<button type="primary" class="pay-btn" @tap="toPay()" :disabled="disabledPay">{{ disabledPay ? `差${spread / 100}元起送` : '去结算' }}</button>
				</view>
				<view v-else><button type="primary" class="pay-btn" disabled>未在营业时段</button></view>
			</view>
			<!-- 购物车栏 end -->
		</view>

		<!-- 商品详情模态框 begin -->
		<modal :show="goodDetailModalVisible" class="good-detail-modal" color="#5A5B5C" width="90%" custom padding="0rpx" radius="12rpx">
			<view class="cover">
				<image v-if="currentGoods.image" :src="goodsImageBaseUrl + currentGoods.image" class="image"></image>
				<view class="btn-group">
					<image src="/static/images/menu/close.png" @tap="closeGoodDetailModal"></image>
				</view>
			</view>
			<scroll-view class="detail" scroll-y style="padding-bottom: 100px;">
				<view class="wrapper">
					<view class="basic">
						<view class="name">{{ currentGoods.name }}</view>
						<view class="tips">{{ currentGoods.description }}</view>
					</view>
					<!-- 商品的额外属性 -->
					<view class="properties" v-if="currentGoods.goodsPropertyVos && currentGoods.goodsPropertyVos.length">
						<view class="property" v-for="(goodsPropertyVo, index) in currentGoods.goodsPropertyVos" :key="index">
							<view class="title">
								<text class="name">
									<text v-if="goodsPropertyVo.required" style="color: red">*</text>
									{{ goodsPropertyVo.category }}
								</text>
							</view>
							<view class="values">
								<view
									class="value"
									v-for="(property, index2) in goodsPropertyVo.propertyList"
									:key="index2"
									:class="{ default: property.isDefault }"
									@tap="changePropertyDefault(index, index2)"
								>
									{{ property.propertyOption }}
									{{ property.rebasePrice ? '￥' + property.rebasePrice / 100 : property.extraPrice / 100 ? '￥' + property.extraPrice / 100 : '' }}
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
			<view class="action">
				<!-- 价格和属性 -->
				<view class="left">
					<view class="price">￥{{ currentGoods.realPrice / 100 }}</view>
					<view class="props" v-if="getGoodSelectedProps(currentGoods)">{{ getGoodSelectedProps(currentGoods) }}</view>
				</view>
				<!-- 模态窗里面增减商品 -->
				<view class="btn-group">
					<button type="default" plain class="btn" size="mini" hover-class="none" @tap="changeCurrentGoodsNumber(-1)">
						<view class="iconfont iconsami-select"></view>
					</button>
					<view class="number">{{ currentGoods.number }}</view>
					<button type="primary" class="btn" size="min" hover-class="none" @tap="changeCurrentGoodsNumber(1)"><view class="iconfont iconadd-select"></view></button>
				</view>
			</view>
			<view class="add-to-cart-btn" @tap="addCurrentGoodsToCart()"><view>确认</view></view>
		</modal>
		<!-- 商品详情模态框 end -->

		<!-- 购物车详情popup -->
		<popup-layer direction="top" :show-pop="cartPopupVisible" class="cart-popup">
			<template slot="content">
				<view class="top"><text @tap="handleCartClear">清空</text></view>
				<scroll-view class="cart-list" scroll-y>
					<view class="wrapper">
						<view class="item" v-for="(goods, index) in cart" :key="index">
							<view class="left">
								<view class="name">{{ goods.name }}</view>
								<view class="props">{{ getGoodSelectedProps(goods) }}</view>
							</view>
							<view class="center">
								<text>￥{{ goods.realPrice / 100 }}</text>
							</view>
							<view class="right">
								<!-- 购物车里添加数量 -->
								<button type="default" plain size="mini" class="btn" hover-class="none" @tap="handleCartItemReduce(index)">
									<view class="iconfont iconsami-select"></view>
								</button>
								<view class="number">{{ goods.number }}</view>
								<!-- 购物车里减少数量 -->
								<button type="primary" class="btn" size="min" hover-class="none" @tap="handleCartItemAdd(index)">
									<view class="iconfont iconadd-select"></view>
								</button>
							</view>
						</view>
						<view class="item" v-if="takeType == '外卖配送'">
							<view class="left"><view class="name">包装费</view></view>
							<view class="center">
								<text>￥{{ appProperties.packingPrice / 100 }}</text>
							</view>
							<view class="left"><view class="name">配送费</view></view>
							<view class="center">
								<text>￥{{ appProperties.sendingPrice / 100 }}</text>
							</view>
						</view>
					</view>
				</scroll-view>
			</template>
		</popup-layer>
		<!-- 购物车详情popup -->
	</view>
</template>

<script>
import modal from '@/components/modal/modal';
import popupLayer from '@/components/popup-layer/popup-layer';
import { mapState, mapMutations, mapActions, mapGetters } from 'vuex';
import staticGoods from '../../api/goods.js'
export default {
	components: {
		modal,
		popupLayer
	},
	data() {
		return {
			goods: staticGoods, // 引入静态数据
			currentCategoryName: '', // 默认分类
			categoryScrollTop: 0,
			menuScrollIntoView: '',
			goodDetailModalVisible: false, //是否饮品详情模态框
			currentGoods: {}, // 当前饮品
			selectedPropertyList: [], // 当前商品的属性
			cartPopupVisible: false,
			sizeCalcState: false,
			// 商品图片的基础路径
			goodsImageBaseUrl: this.$config.baseUrl + '/static/image/'
		};
	},
	onLoad() {
		if (!this.userInfo) {
			// 不登录 通过本地缓存直接进入
			let userInfo = uni.getStorageSync('userInfo');
			if (userInfo) {
				console.log('不登录，走本地缓存');
				this.$store.commit('login', userInfo);
			}
		}
		this.init();
	},
	computed: {
		...mapState(['userInfo', 'address', 'appProperties', 'takeType', 'cart']),
		// 计算单个饮品添加到购物车的数量
		goodsCartNum() {
			return id =>
				this.cart.reduce((acc, cur) => {
					if (cur.id === id) {
						return (acc += cur.number);
					}
					return acc;
				}, 0);
		},
		// 计算该类商品的购买总数
		menuCartNum() {
			return goodsCategoryName =>
				this.cart.reduce((acc, cur) => {
					if (cur.goodsCategoryName === goodsCategoryName) {
						return (acc += cur.number);
					}
					return acc;
				}, 0);
		},
		// 计算购物车总数
		getCartGoodsNumber() {
			return this.cart.reduce((acc, cur) => acc + cur.number, 0);
		},
		// 计算购物车总价,
		getCartGoodsPrice() {
			let totalPrice = 0;
			this.cart.forEach(goods => {
				totalPrice += goods.realPrice * goods.number;
			});
			if (this.$store.getters.getTakeType == '外卖配送') {
				totalPrice += this.appProperties.packingPrice + this.appProperties.sendingPrice;
			}
			return totalPrice;
		}, // 是否达到起送价
		disabledPay() {
			return this.$store.getters.getTakeType == '外卖配送' && this.getCartGoodsPrice < this.appProperties.sendingNeedLeastPrice ? true : false;
		}, // 差多少元起送
		spread() {
			if (this.$store.getters.getTakeType != '外卖配送') return;
			return this.appProperties.sendingNeedLeastPrice - this.getCartGoodsPrice;
		}
	},
	onPullDownRefresh() {
		setTimeout(() => {
			uni.stopPullDownRefresh();
		}, 300);
		this.init();
	},
	methods: {
		//页面初始化
		async init() {
			let that = this;
			that.$request('/config', 'get').then(result => {
				console.log('刷新配置信息:' + JSON.stringify(result.data));
				that.$store.commit('set_app_properties', result.data);
			});

			that.$request('/goods/goodsMenu/list', 'get').then(result => {
				that.goods = result.data;
				that.sizeCalcState = false;
				uni.stopPullDownRefresh();
			});
		},
		toLogin() {
			uni.navigateTo({ url: '/pages/login/login' });
		},
		// 选择到店自取的方式下单
		takein() {
			this.$store.commit('set_take_type', '到店自取');
		},
		// 选择外卖配送的方式下单
		takeout() {
			this.$store.commit('set_take_type', '外卖配送');
			if (!this.userInfo) {
				uni.navigateTo({ url: '/pages/login/login' });
				return;
			}
		},
		handleMenuTap(goodsCategoryName) {
			if (!this.sizeCalcState) {
				this.calcSize();
			}

			this.currentCategoryName = goodsCategoryName;
			this.$nextTick(() => (this.categoryScrollTop = this.goods.find(item => item.goodsCategoryName == goodsCategoryName).top));
		},
		handleGoodsScroll({ detail }) {
			// if (!this.sizeCalcState) {
			// 	this.calcSize();
			// }
			// const { scrollTop } = detail;
			// let tabs = this.goods.filter(item => item.top < scrollTop).reverse();
			// console.log(tabs)
			// if (tabs.length > 0) {
			// 	this.currentCategoryName = tabs[0].goodsCategoryName;
			// }
		},
		
		// 辅助函数，滚动时判断事件用
		calcSize() {
			let that = this;
			// 这里是真的坑!!! 微信里没法绑定获取，暂时只能这样了
			let view = uni.createSelectorQuery().selectAll('#categoryMenu');
			view.fields(
				{
					size: true // 返回高和宽
				},
				data => {
					// eg: {width: 219, height: 760}
				}
			).exec(res => {
				// 回调信息
				let h = 10;
				let whArr = res[0];
				console.log('右边菜单商品的高宽为: ', JSON.stringify(whArr));
				let goodsMenus = that.goods;
				for (let i = 0; i < that.goods.length; i++) {
					goodsMenus[i].top = h;
					h += whArr[i].height;
					goodsMenus[i].bottom = h;
				}
				that.sizeCalcState = true;
			});

		},
		// 获取商品具体价格，如大杯商品，小杯商品价格不同
		getGoodsRealPrice(goods) {
			console.log(goods);
			let realPrice = goods.realPrice;
			if (goods.goodsPropertyVos && goods.goodsPropertyVos.length) {
				let vos = goods.goodsPropertyVos;
				for (let i = 0; i < vos.length; i++) {
					let propertyList = vos[i].propertyList;
					for (let j = 0; j < propertyList.length; j++) {
						if (propertyList[j].isDefault && propertyList[j].realPrice) {
							realPrice = propertyList[j].realPrice;
							break;
						}
					}
				}
			}
			return realPrice;
		},
		// 显示当前商品的详情信息
		showGoodDetailModal(currentGoods) {
			currentGoods.number = 1;
			currentGoods.realPrice = this.getGoodsRealPrice(currentGoods);
			currentGoods.propertyStr = this.getGoodSelectedProps(currentGoods);
			this.currentGoods = JSON.parse(JSON.stringify(currentGoods)); // 深拷贝

			this.goodDetailModalVisible = true;
			this.selectedPropertyList = [];
			console.log('显示当前商品的详情信息', this.currentGoods);
		},
		closeGoodDetailModal() {
			// 关闭饮品详情模态框
			this.goodDetailModalVisible = false;
		},
		// 改变商品的某个默认属性值 要区分必选和多选的属性
		changePropertyDefault(categoryIndex, propertyIndex) {
			let propertyCategory = this.currentGoods.goodsPropertyVos[categoryIndex].category;
			let propertyList = this.currentGoods.goodsPropertyVos[categoryIndex].propertyList;
			if (propertyCategory != '加料') {
				// 必须选一个
				propertyList.forEach(property => (property.isDefault = false)); // 重置所有默认选择状态重新选择
				propertyList[propertyIndex].isDefault = true;
				if (propertyCategory == '大小') {
					this.currentGoods.realPrice = this.currentGoods.realPrice - this.currentGoods.defaultPrice + propertyList[propertyIndex].rebasePrice;
					this.currentGoods.defaultPrice = propertyList[propertyIndex].rebasePrice;
					this.currentGoods.number = 1;
					this.currentGoods.propertyStr = this.getGoodSelectedProps(this.currentGoods);
				}
			} else {
				// 可选项  加料或减料
				if (propertyList[propertyIndex].isDefault) {
					this.currentGoods.realPrice -= propertyList[propertyIndex].extraPrice;
					propertyList[propertyIndex].isDefault = false;
				} else {
					this.currentGoods.realPrice += propertyList[propertyIndex].extraPrice;
					propertyList[propertyIndex].isDefault = true;
				}
			}
		},
		// 计算当前饮品所选属性，空格隔开
		getGoodSelectedProps(goods) {
			if (goods.goodsPropertyVos) {
				let propertyStr = '';
				goods.goodsPropertyVos.forEach(goodsPropertyVo => {
					goodsPropertyVo.propertyList.forEach(property => {
						if (property.isDefault) {
							propertyStr += property.propertyOption + ' ';
						}
					});
				});
				return propertyStr;
			}
			return '';
		},
		// 改变当前商品数量
		changeCurrentGoodsNumber(number) {
			this.currentGoods.number += number;
			if (this.currentGoods.number < 0) {
				this.currentGoods.number = 0;
			} else if (this.currentGoods.number >= 100) {
				this.currentGoods.number = 100;
			}
		},
		// 将当前商品加入到购物车
		addCurrentGoodsToCart() {
			if (this.currentGoods && this.currentGoods.number) {
				this.handleAddToCart(this.currentGoods);
			}
			this.closeGoodDetailModal();
		},
		// 减少购物车里的商品数量, goodsId和propertyStr去比较
		handleReduceFromCart(goods) {
			let propertyStr = this.getGoodSelectedProps(goods);
			let cart = this.cart;
			for (let i = cart.length - 1; i >= 0; i--) {
				if (cart[i].id == goods.id && cart[i].propertyStr == propertyStr) {
					if (cart[i].number <= 1) cart.splice(i, 1);
					else cart[i].number -= 1;
					return;
				}
			}
		},
		// 添加到购物车
		handleAddToCart(goods) {
			let propertyStr = this.getGoodSelectedProps(goods);
			goods.propertyStr = propertyStr;
			let cart = this.cart;
			for (let i = cart.length - 1; i >= 0; i--) {
				if (cart[i].id == goods.id && cart[i].propertyStr == propertyStr) {
					cart[i].number++;
					return;
				}
			}

			// 购物车里不存在
			if (!goods.number) goods.number = 1;
			this.cart.push(JSON.parse(JSON.stringify(goods)));
		},
		// 清空购物车
		handleCartClear() {
			let that = this;
			uni.showModal({
				title: '提示',
				content: '确定清空购物车么',
				success: ({ confirm }) => {
					if (confirm) {
						that.cartPopupVisible = false;
						that.$store.commit('set_cart', []);
					}
				}
			});
		},
		// 在购物车里操作商品数量
		handleCartItemAdd(index) {
			this.cart[index].number += 1;
		},
		handleCartItemReduce(index) {
			if (this.cart[index].number === 1) {
				this.cart.splice(index, 1);
			} else {
				this.cart[index].number -= 1;
			}
			if (!this.cart.length) {
				this.cartPopupVisible = false;
			}
		},
		toPay() {
			this.cartPopupVisible = false;
			if (!this.userInfo) {
				uni.navigateTo({ url: '/pages/login/login' });
			}
			if (!this.userInfo.address || this.userInfo.address == '' || !this.userInfo.phone || this.userInfo.phone == '') {
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

			console.log('进入支付页面');
			if (!this.userInfo) {
				uni.navigateTo({ url: '/pages/login/login' });
				return;
			}

			uni.navigateTo({
				url: '/pages/pay/pay'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
@import '~@/pages/menu/menu.scss';
</style>
