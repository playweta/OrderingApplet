package com.yu.app.common.util.session;


import com.yu.common.common.constant.Const;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.GeneratorUtil;
import com.yu.common.common.util.jwt.JWTUtil;
import com.yu.common.entity.app.User;
import com.yu.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.yu.common.common.util.spring.SpringContextUtil.getBeanByClass;

/**
 * 用户session服务
 */
@Slf4j
public class SessionUtil {
    private static final RedisService redisService = getBeanByClass(RedisService.class);

    // 退出登录
    public static void logout(HttpServletRequest request) {
        redisService.hdel(Const.CONST_user_session_map, request.getAttribute(Const.CONST_wx_openid));
    }

    // 用户是否登录
    public static boolean checkUserLogin(String wxOpenid) {
        return redisService.hHasKey(Const.CONST_user_session_map, wxOpenid);
    }

    // 添加会话缓存  缓存到redis里，CONST_user_session_prefix + userId作为key
    public static void setUserSession(User user) {
        long expireTime = GeneratorUtil.generateExpireTime(Const.CONST_half_month);
        user.setToken(JWTUtil.generateTokenWithOpenid(user.getWxOpenid(), expireTime));
        redisService.hset(Const.CONST_user_session_map, user.getWxOpenid(), "" + expireTime);
        log.info("[添加会话缓存] {}", user);
    }

    // 当前用户的微信openid 前提是已经登录了
    public static String getCurrentWxOpenidFromRequest(HttpServletRequest request) throws ServiceException {
        Object wxOpenid = request.getAttribute(Const.CONST_wx_openid);
        if (wxOpenid == null)
            throw ServiceException.CONST_user_not_login;
        return wxOpenid.toString();
    }

    // 找请求里携带的token
    public static String getToken(HttpServletRequest request) throws ServiceException {
        String token = request.getHeader(Const.CONST_token); // 从header里面找
        if (StringUtils.isEmpty(token)) // 从url后面的参数里找
            token = request.getParameter(Const.CONST_token);
        if (token == null)
            throw ServiceException.CONST_user_not_login;
        return token;
    }

}
