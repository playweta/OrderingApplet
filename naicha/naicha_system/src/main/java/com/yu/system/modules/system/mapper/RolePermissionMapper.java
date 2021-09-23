package com.yu.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.common.entity.system.RolePermission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色的权限(可以不止一个)
 */
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    // 获取角色的权限
    @Select("select permission from sys_role_permission where role_id = #{param1} group by permission;")
    List<String> selectPerimssionByRoleId(Integer roleId);

    // 根据用户id获取所有权限
    @Select("select sys_role_permission.`permission`  from sys_role_permission where sys_role_permission.role_id = \n" +
            "\t(select sys_user.`role_id` from sys_user where sys_user.id = #{param1});")
    List<String> selectPermissionByUserId(Integer userId);
}
