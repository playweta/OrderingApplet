import Vue from 'vue'
import store from '../store/index'
// 引入并注册vue-router 路由
import VueRouter from "vue-router";

Vue.use(VueRouter);

// 把重复导航的报错吞了 可有可无
// const originalPush = VueRouter.prototype.push
// VueRouter.prototype.push = function push(location) {
//   return originalPush.call(this, location).catch(err => err)
// }

// 常量路由
export var constantRouterMap = [

  {
    path: '/',
    redirect: "/login"
  },
  {
    path: '/login',
    component: (resolve) => require(['../components/LoginIndex.vue'], resolve),
    name: "login"
  },

  {
    path: '/index',
    // 父组件路由必须要有 子组件基于父组件 父组件就好比容器一样
    component: () => import('../components/Layout.vue'),
    redirect: "/system/dashboard",
    name: "systemIndex",
    title: "首页",
    hidden: true,
    children: [
      // dashboard面板 后台首页
      {
        path: '/system/dashboard',
        component: () => import('../components/system/dashboard/index.vue'),
        name: "dashboard",
        title: "首页报表"
      }
    ]
  }
]


// 创建一个路由器实例
const router = new VueRouter({
  // 并且配置路由规则
  // mode: 'hash', // history模式 没有/#/  默认为hash模式
  routes: constantRouterMap
})

// 路由拦截器
router.beforeEach((to, from, next) => {
  if (!store.getters.userInfo && !store.getters.token) {
    if (to.path.startsWith("/login")) // 防止死循环
      next()
    else {
      console.log("还没有登录")
      next('/login')
    }
  }
  next()
})


var getLastUrl = (str, yourStr) => str.slice(str.lastIndexOf(yourStr))//取到浏览器出现网址的最后"/"出现的后边的字符

export default router
