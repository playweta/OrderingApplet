package com.yu.system.modules.auth.controller;

import com.yu.system.common.util.session.SessionUtil;
import com.yu.system.modules.system.entity.dto.SysUserDTO;
import com.yu.system.modules.system.entity.form.SysUserLoginForm;
import com.yu.system.modules.system.entity.form.UpdatePasswordForm;
import com.yu.system.modules.system.service.SysUserService;
import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Api(tags = "系统：后台认证授权")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Resource
    private SysUserService sysUserService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<SysUserDTO> login(@RequestBody SysUserLoginForm sysUserLoginVo, HttpServletRequest request) throws Exception {
        return Result.ok(sysUserService.login(sysUserLoginVo, request));
    }

    @ApiOperation("通过token登录")
    @NeedPermission
    @PostMapping("/loginByToken")
    public Result<SysUserDTO> loginByToken(HttpServletRequest request) {
        SysUserDTO sysUserDTO = SessionUtil.getCurrentSysUserDTO(request);
        SessionUtil.setSysUserSession(sysUserDTO); // 续期token
        return Result.ok(sysUserDTO);
    }

    @ApiOperation("退出登录")
    @DeleteMapping("/logout")
    public Result logout(HttpServletRequest request)  {
        return Result.ok(SessionUtil.logout(request));
    }

    @ApiOperation("获取登录用户信息")
    @NeedPermission
    @GetMapping("/userInfo")
    public Result<SysUserDTO> getLoginUserInfo(HttpServletRequest request) throws ServiceException {
        return Result.ok(SessionUtil.getCurrentSysUserDTO(request));
    }

    @ApiOperation("修改密码")
    @NeedPermission
    @PutMapping("/password")
    public Result<Integer> updatePassword(@RequestBody UpdatePasswordForm params, HttpServletRequest request) throws Exception {
        return Result.ok(sysUserService.updatePassword(SessionUtil.getCurrentSysUserId(request), params));
    }


    @ApiOperation("上传头像") // file的名字要和前端input里的name一致
    @NeedPermission
    @PostMapping("/avatar")
    public Result<Integer> uploadUserAvatar(@RequestParam MultipartFile file, HttpServletRequest request) throws ServiceException, IOException {
        return Result.ok(sysUserService.updateUserAvatar(file, SessionUtil.getCurrentSysUserDTO(request)));
    }

}
