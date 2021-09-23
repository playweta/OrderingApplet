package com.yu.system.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.system.modules.system.mapper.RoleMapper;
import com.yu.system.modules.system.mapper.RolePermissionMapper;
import com.yu.system.modules.system.entity.form.UpdateRolePermissionForm;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.entity.system.Role;
import com.yu.common.entity.system.RolePermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class RoleService {

    @Resource
    private RoleMapper sysRoleMapper;

    @Resource
    private RolePermissionMapper sysRolePermissionMapper;

    // 分页，条件查询角色
    public List<Role> listByPageAndCondition(Integer pageNo, Integer pageSize, Map<String, Object> params) throws ServiceException {
        QueryWrapper<Role> wrapper = new QueryWrapper<Role>();
        if (params == null)
            return sysRoleMapper.selectPage(new Page<>(pageNo, pageSize), wrapper).getRecords();

        params.forEach((key, value) -> {
            wrapper.eq(key, value);
        });
        return sysRoleMapper.selectPage(new Page<>(pageNo, pageSize), wrapper).getRecords();
    }

    @Transactional
    public void updateRolePermission(UpdateRolePermissionForm vo) throws ServiceException {
        sysRolePermissionMapper.delete(new QueryWrapper<RolePermission>().eq("role_id", vo.getRoleId()));
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(vo.getRoleId());
        for (String permission : vo.getPermissions()) {
            rolePermission.setPermission(permission);
            sysRolePermissionMapper.insert(rolePermission);
        }
        log.info("[更新角色权限] {}", vo);
    }

}
