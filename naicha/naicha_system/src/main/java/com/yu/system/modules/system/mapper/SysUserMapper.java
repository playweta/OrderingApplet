package com.yu.system.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.system.modules.system.entity.vo.SysUserVO;
import com.yu.common.entity.system.SysUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface SysUserMapper extends BaseMapper<SysUser> {

    // 获取全部管理员(视图对象)
    @Select("select sys_user.id, sys_user.`username`, sys_user.`status`, sys_user.`status` ,\n" +
            " sys_role.`id` as roleId, sys_role.`name` as roleName from sys_user  \n" +
            " left join sys_role  on  sys_user.`role_id` = sys_role.`id`; ")
    List<SysUserVO> getAllUserVOs();

}
