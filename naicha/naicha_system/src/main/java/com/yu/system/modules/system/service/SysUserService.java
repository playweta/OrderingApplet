package com.yu.system.modules.system.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.common.common.config.property.IOProperty;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.entity.system.SysUser;
import com.yu.common.service.RedisService;
import com.yu.system.common.util.session.SessionUtil;
import com.yu.system.modules.system.entity.dto.SysUserDTO;
import com.yu.system.modules.system.entity.form.SysUserLoginForm;
import com.yu.system.modules.system.entity.form.UpdatePasswordForm;
import com.yu.system.modules.system.mapper.RoleMapper;
import com.yu.system.modules.system.mapper.RolePermissionMapper;
import com.yu.system.modules.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

@Slf4j
@Service
public class SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;
    @Resource
    private IOProperty ioProperty;

    @Transactional
    public SysUserDTO login(SysUserLoginForm loginVo, HttpServletRequest request) throws Exception {
        // 校对验证码
//        String verifyCode = String.valueOf(redisService.get(Const.CONST_verify_code_redis_prefix + loginVo.getUuid()));
//        redisService.del(Const.CONST_verify_code_redis_prefix + loginVo.getUuid());
//        if (StringUtils.isEmpty(verifyCode) || !verifyCode.equals(loginVo.getVerifyCode()))
//            throw ServiceException.CONST_verify_code_error_or_expire;

        SysUser user = sysUserMapper.selectOne(new QueryWrapper<SysUser>()
                .eq("username", loginVo.getUsername())
                .eq("password", GeneratorUtil.md5Base64(loginVo.getPassword())));
        if (user == null) {
            log.error("[管理员登录失败] {}", loginVo);
            throw ServiceException.CONST_login_failed;
        }

        if (!user.getStatus()) {
            log.error("[管理员登录失败 账号冻结] {}", user);
            throw ServiceException.CONST_user_is_forbidden;
        }

        SysUserDTO userDTO = new SysUserDTO();
        BeanUtils.copyProperties(user, userDTO, "password");
        // 获取权限
        userDTO.setPermissions(new HashSet<>(rolePermissionMapper.selectPerimssionByRoleId(user.getRoleId())));
        userDTO.setRoleName(roleMapper.selectById(user.getRoleId()).getName());
        SessionUtil.setSysUserSession(userDTO);

        log.info("[管理员登录] {}", userDTO);
        return userDTO;
    }

    @Transactional
    public int updatePassword(Integer sysUserId, UpdatePasswordForm params) throws ServiceException {
        if (StringUtils.isEmpty(params.getOldPassword()) || StringUtils.isEmpty(params.getNewPassword())
                || params.getNewPassword().length() < 6 || params.getNewPassword().length() > 18)
            throw ServiceException.CONST_password_pattern_error;
        if (params.getOldPassword().equals(params.getNewPassword()))
            throw ServiceException.CONST_new_password_can_not_equals_old_password;

        SysUser user = sysUserMapper.selectById(sysUserId);
        if (!GeneratorUtil.md5Base64(params.getOldPassword()).equals(user.getPassword()))
            throw ServiceException.CONST_old_password_error;

        user.setPassword(GeneratorUtil.md5Base64(params.getNewPassword()));
        return sysUserMapper.updateById(user);
    }


    // 更新头像
    public int updateUserAvatar(MultipartFile file, SysUserDTO currentSysUser) throws ServiceException, IOException {
        File dir = new File(ioProperty.getImageFileRootPath());
        if (!dir.exists()) // 不存在该目录就创建
            dir.mkdir();
        String userAvatar = "userAvatar-" + currentSysUser.getId() + "-" + file.getOriginalFilename();
        file.transferTo(new File(dir, userAvatar));
        if (file.getSize() > 1024 * 1024)
            throw ServiceException.CONST_image_upload_failed; // 文件超过1MB

        currentSysUser.setAvatar(userAvatar);
        SessionUtil.updateSysUserSession(currentSysUser); // 更新缓存里的会话信息
        SysUser user = new SysUser();
        user.setId(currentSysUser.getId());
        user.setAvatar(currentSysUser.getAvatar());
        return sysUserMapper.updateById(user); // 更新数据库
    }
}
