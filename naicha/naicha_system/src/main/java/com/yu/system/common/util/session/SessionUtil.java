package com.yu.system.common.util.session;

import com.yu.common.common.constant.Const;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.common.util.jwt.JWTUtil;
import com.yu.common.common.util.spring.SpringContextUtil;
import com.yu.common.service.RedisService;
import com.yu.system.modules.system.entity.dto.SysUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

// 后台session会话
@Slf4j
public class SessionUtil {
    private static final RedisService redisService = SpringContextUtil.getBeanByClass(RedisService.class);

    public static boolean logout(HttpServletRequest request) {
        return redisService.hdel(Const.CONST_sys_user_session_map, request.getAttribute(Const.CONST_sys_user_id));
    }

    // 检查管理员是否登录
    public static boolean checkSysUserLogin(Integer sysUserId) {
        return redisService.hHasKey(Const.CONST_sys_user_session_map, sysUserId.toString());
    }

    /**
     * 添加会话缓存  缓存到redis里，session Map<sysUserId, SysUserDTO>
     */
    public static void setSysUserSession(SysUserDTO sysUserDTO) {
        sysUserDTO.setToken(JWTUtil.generateTokenWithUserId(sysUserDTO.getId(), GeneratorUtil.generateExpireTime(Const.CONST_half_month)));
        redisService.hset(Const.CONST_sys_user_session_map, sysUserDTO.getId().toString(), sysUserDTO);
    }

    // 更新会话缓存
    public static void updateSysUserSession(SysUserDTO sysUserDTO) {
        redisService.hset(Const.CONST_sys_user_session_map, sysUserDTO.getId().toString(), sysUserDTO);
    }

    // 当前用户的id
    public static Integer getCurrentSysUserId(HttpServletRequest request) {
        return Integer.valueOf(request.getAttribute(Const.CONST_sys_user_id).toString());
    }

    // 当前登录的管理员信息
    public static SysUserDTO getCurrentSysUserDTO(Integer sysUserId) {
        Object o = redisService.hget(Const.CONST_sys_user_session_map, sysUserId.toString());
        if (o != null)
            return (SysUserDTO) o;
        return null;
    }

    // 当前登录的管理员信息
    public static SysUserDTO getCurrentSysUserDTO(HttpServletRequest request) {
        Object o = redisService.hget(Const.CONST_sys_user_session_map, getCurrentSysUserId(request).toString());
        if (o != null)
            return (SysUserDTO) o;
        return null;
    }

    // 找请求里携带的token
    public static String getToken(HttpServletRequest request) throws ServiceException {
        String token = request.getParameter(Const.CONST_token); // 从url后面的参数里找
        if (StringUtils.isEmpty(token)) // 从header里面找
            token = request.getHeader(Const.CONST_token);
        if (token == null)
            throw ServiceException.CONST_user_not_login;
        return token;
    }

    /**
     * 是否含有某项权限, 根据前缀去匹配
     *
     * @param needPermission 标匹配权限 eg: 需要权限system:user:update 如果有system:user权限即可
     * @return boolean
     */
    public static boolean hasPermission(String needPermission, Integer sysUserId) {
        Set<String> permissions = getCurrentSysUserDTO(sysUserId).getPermissions(); // 拥有的权限
        if (StringUtils.isEmpty(needPermission))
            return true;

        if (permissions.contains(needPermission))
            return true;
        for (String userPermission : permissions)
            if (needPermission.startsWith(userPermission) || "*".equals(userPermission))
                return true;
        return false;
    }


}
