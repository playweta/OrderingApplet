/**
 * 路由格式
   path: '/system/setting',
   // component: (resolve) => require(['../components/app/taskLog/TaskLogIndex.vue'], resolve), 组件
   component: () => import('../components/system/SystemIndex.vue'), 组件
   icon: 'el-icon-menu',
   title: '系统设置',
   name: "systemSetting",                     路由的唯一标识
   hidden: true                               不显示到菜单栏里
   permission: "system:app:user"              需要的权限  粗粒度
 **/
const systemRouters = [

  {
    path: '/appAdmin',
    component: () => import('../components/Layout.vue'),
    icon: 'el-icon-s-management',
    title: '应用管理',
    name: "appAdmin",
    children: [
      {
        path: '/app/userAdmin',
        component: () => import('../components/app/user/UserAdminIndex.vue'),
        icon: 'el-icon-user-solid',
        title: '用户管理',
        name: "userAdmin",
        permission: "system:app:user"
      },
      {
        path: '/app/goodsAdmin',
        component: () => import('../components/app/goods/GoodsIndex.vue'),
        icon: 'el-icon-s-operation',
        title: '商品管理',
        name: "goodsAdmin",
        permission: "system:app:goods"
      },
      {
        path: '/app/goodsCategoryAdmin',
        component: () => import('../components/app/goodsCategory/index.vue'),
        icon: 'el-icon-s-operation',
        title: '商品种类',
        name: "goodsCategoryAdmin",
        permission: "system:app:goodsCategory"
      },
      {
        path: '/app/orderAdmin',
        component: () => import('../components/app/order/index.vue'),
        icon: 'el-icon-s-operation',
        title: '订单管理',
        name: "orderAdmin",
        permission: "system:app:orderInfo"
      },
      {
        path: '/app/orderHandler',
        component: () => import('../components/app/orderHandler/index.vue'),
        icon: 'el-icon-s-operation',
        title: '订单处理',
        name: "orderHandler",
        permission: "system:app:orderInfo"
      }
    ]
  },
  {
    path: '/systemManage',
    component: () => import('../components/Layout.vue'),
    icon: 'el-icon-s-grid',
    title: '系统管理',
    name: "systemManage",
    children: [
      {
        path: '/system/monitor/appConfig',
        component: () => import('../components/system/monitor/AppConfigIndex.vue'),
        icon: 'el-icon-setting',
        title: '系统配置',
        name: "appConfigSetting",
        permission: "system:admin:config"
      },
      {
        path: '/system/monitor/timedTask',
        component: () => import('../components/system/monitor/TimingTaskIndex.vue'),
        icon: 'el-icon-video-camera-solid',
        title: '定时任务',
        name: "timingTaskAdmin",
        permission: "system:admin:timingTask"
      },
      {
        path: '/system/manage/user',
        component: () => import('../components/system/manage/UserIndex.vue'),
        icon: 'el-icon-s-custom',
        title: '系统用户',
        name: "sysUser",
        permission: "system:admin:sysUser"
      },
      {
        path: '/system/manage/role',
        component: () => import('../components/system/manage/RoleIndex.vue'),
        icon: 'el-icon-s-check',
        title: '角色管理',
        name: "roleAdmin",
        permission: "system:admin:role"
      }
    ]
  }
]

export default systemRouters
