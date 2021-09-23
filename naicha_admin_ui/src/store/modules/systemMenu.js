import systemRouters from "../../router/systemRouters.js";
import router from "../../router/index";

function hasPermission(permissions, needPermission) {
    for (let i = 0; i < permissions.length; i++)
        if (permissions[i].startsWith(needPermission))
            return true;
    return false;
}

/**
 * 递归过滤系统路由表，返回符合用户角色权限的路由表
 * @param routes 所有路由表
 * @param perms
 */
function filterRouter(routes, perms) {
    const res = []
    routes.forEach(route => {
        if (route.children) {
            route.children = filterRouter(route.children, perms) // 递归处理孩子节点
            if (route.children && route.children.length > 0)
                res.push(route)
        } else {
            if (route.permission) { // 需要权限
                if (hasPermission(perms, route.permission))
                    res.push(route);
            } else {
                res.push(route)
            }
        }
    })
    return res
}

const systemMenu = {
    state: {
        systemMenus: [], // 后台菜单
    },
    getters: {
        systemMenus: state => state.systemMenus
    },
    mutations: {
        setSystemMenus(state, systemMenu) {
            state.systemMenus = systemMenu;
        }
    },
    actions: {
        generateSystemMenus({commit}, permissions) { // 生成菜单栏(路由表)  eg:permissions=["system:sysUser:list]
            if (permissions.includes('*')) {
                commit("setSystemMenus", systemRouters)
                router.addRoutes(systemRouters)
            } else {
                let routers = filterRouter(systemRouters, permissions);
                commit("setSystemMenus", routers)
                router.addRoutes(routers) // 动态添加可访问路由表
            }
        }
    }
}

export default systemMenu
