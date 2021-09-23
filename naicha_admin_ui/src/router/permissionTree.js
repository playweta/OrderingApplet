/**
 * 权限树 授权的时候使用 细粒度
 id: 'title',
 label: 'title',
 children: 'children'

 permission作为key
 */
const permissionTree = [
    {
        title: '应用管理',
        permission: "system:app", // 仅仅是作为key控制下视图
        children: [
            {
                title: '用户管理',
                permission: 'system:app:user' // 仅仅是作为key控制下视图
            },
            {
                title: '商品管理',
                permission: 'system:app:goods',
                children: [
                    {
                        title: '添加商品',
                        permission: "system:app:goods:add" // TODO 真正可以添加到数据库的权限值, 对应到具体的后台接口权限
                    },
                    {
                        title: '修改商品',
                        permission: "system:app:goods:update"
                    }
                ]
            },
            {
                title: '商品种类管理',
                permission: 'system:app:goodsCategory'
            },
            {
                title: '订单管理',
                permission: 'system:app:orderInfo'
            }
        ]
    },
    {
        title: '系统管理',
        permission: "system:admin",
        children: [
            {
                title: '系统配置',
                permission: 'system:admin:config'
            },
            {
                title: '定时任务',
                permission: "system:admin:timingTask"

            },
            {
                title: '管理员管理',
                permission: "system:admin:sysUser"
            },
            {
                title: '角色管理',
                permission: "system:admin:role"
            }
        ]
    }

]

export default permissionTree
