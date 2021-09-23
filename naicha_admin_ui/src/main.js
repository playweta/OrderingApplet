import Vue from 'vue'
import App from './App.vue'
import store from './store/index.js'
import router from './router/index.js'
// 引入element
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
// 配置cookie

Vue.use(ElementUI)
// 设置别名 组件里可以不用导入直接使用如:this.$notify.success("成功")
Vue.prototype.$loading = ElementUI.Loading.service
Vue.prototype.$msgbox = ElementUI.MessageBox
Vue.prototype.$alert = ElementUI.MessageBox.alert // 确认框 只有确认
Vue.prototype.$confirm = ElementUI.MessageBox.confirm // 确认框 点击遮罩可以关闭
Vue.prototype.$notify = ElementUI.Notification
Vue.prototype.$message = ElementUI.Message


// 开启debug模式
Vue.config.debug = process.env.NODE_ENV === 'development';


// 路由器会创建一个 App 实例，并且挂载到选择符 #app 匹配的元素上。
new Vue({
  router: router,  // 路由
  store: store, // 缓存 vuex
  render: h => h(App)
}).$mount('#app')


