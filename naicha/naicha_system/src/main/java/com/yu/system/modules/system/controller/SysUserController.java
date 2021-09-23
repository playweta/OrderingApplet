package com.yu.system.modules.system.controller;

import com.yu.common.entity.system.SysUser;
import com.yu.system.modules.system.mapper.RolePermissionMapper;
import com.yu.system.modules.system.mapper.SysUserMapper;
import com.yu.system.modules.system.entity.vo.SysUserVO;
import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Api(tags = "系统：系统管理员")
@RestController
@RequestMapping("/sysUser")
public class SysUserController {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @ApiOperation("查询全部")
    @NeedPermission("system:admin:sysUser:list")
    @GetMapping("/list")
    public Result<List<SysUserVO>> getAllSysUsers() throws ServiceException {
        return Result.ok(sysUserMapper.getAllUserVOs());
    }

    @ApiOperation("根据id查询")
    @NeedPermission("system:admin:sysUser:search")
    @GetMapping("/{sysUserId}")
    public Result detail(@PathVariable("sysUserId") Integer sysUserId) {
        return Result.ok(sysUserMapper.selectById(sysUserId));
    }

    @ApiOperation("增")
    @NeedPermission("system:admin:sysUser:add")
    @PostMapping("")
    public Result add(@RequestBody SysUser sysUser) throws ServiceException {
        sysUser.setPassword(GeneratorUtil.md5Base64("123456"));
        return Result.ok(sysUserMapper.insert(sysUser));
    }

    @ApiOperation("改")
    @NeedPermission("system:admin:sysUser:update")
    @PutMapping("")
    public Result update(@RequestBody SysUser sysUser) throws ServiceException {
        sysUser.setPassword(null);
        return Result.ok(sysUserMapper.updateById(sysUser));
    }

    @ApiOperation("批量删")
    @NeedPermission("system:admin:sysUser:delete")
    @DeleteMapping("/{sysUserId}")
    public Result deleteBatch(@RequestBody Integer sysUserId) throws ServiceException {
        if ( rolePermissionMapper.selectPermissionByUserId(sysUserId).contains("*"))
            throw ServiceException.CONST_can_not_change_role_of_super_system_admin_user;
        return Result.ok(sysUserMapper.deleteById(sysUserId));
    }

    @ApiOperation("更新账号激活状态")
    @NeedPermission("system:admin:sysUser:status")
    @PutMapping("/status/{sysUserId}/{status}")
    public Result updateStatus(@ApiParam("sysUserId") @PathVariable Integer sysUserId,
                               @ApiParam("status") @PathVariable Boolean status) throws ServiceException {
        SysUser sysUser = new SysUser();
        sysUser.setId(sysUserId);
        sysUser.setStatus(status);
        return Result.ok(sysUserMapper.updateById(sysUser));
    }

    @ApiOperation("重置密码为123456")
    @NeedPermission("system:admin:sysUser:resetPassword")
    @PutMapping("/resetPassword/{sysUserId}")
    public Result resetPassword(@ApiParam("sysUserId") @PathVariable Integer sysUserId) throws ServiceException {
        SysUser sysUser = new SysUser();
        sysUser.setId(sysUserId);
        sysUser.setPassword(GeneratorUtil.md5Base64("123456"));
        return Result.ok(sysUserMapper.updateById(sysUser));
    }

    @ApiOperation("设置用户的角色")
    @NeedPermission("system:admin:sysUser:update")
    @PutMapping("/role/{userId}/{roleId}")
    public Result setRoleOfSysUser(@PathVariable Integer userId, @PathVariable Integer roleId) throws ServiceException {
        SysUser sysUser = new SysUser();
        sysUser.setId(userId);
        sysUser.setRoleId(roleId);
        return Result.ok(sysUserMapper.updateById(sysUser));
    }
}
