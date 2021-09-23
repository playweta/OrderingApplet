package com.yu.common.common.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yu.common.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * jwt工具类
 */
@Slf4j
public class JWTUtil {

    private static final String COSNT_jwt_secret = "yu_qian_yao_~!@#$%^&*()_+";

    // 校验 token是否正确
    public static boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(COSNT_jwt_secret)).build();
            verifier.verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.error("token已经过期 {}", e.getMessage());
            return false;
        } catch (JWTVerificationException e) {
            log.error("token无效 {}", e.getMessage());
            return false;
        }
    }

    // 获取token的过期时间
    public static Date getExpireTime(String token) throws ServiceException {
        try {
            return JWT.decode(token).getExpiresAt();
        } catch (JWTDecodeException e) {
            log.error("error：{}", e.getMessage());
            throw ServiceException.CONST_token_is_not_validate;
        }
    }

    // 生成 token
    public static String generateTokenWithOpenid(String wxOpenid, long expireTime) {
        try {
            return JWT.create()
                    .withClaim("wxOpenid", wxOpenid)
                    .withExpiresAt(new Date(expireTime))
                    .sign(Algorithm.HMAC256(COSNT_jwt_secret)); // 加上服务器秘钥 也可以再加上用户密码
        } catch (Exception e) {
            log.error("error：{}", e.getMessage());
            return null;
        }
    }

    // 从 token中获取用户的wxOpenid
    public static String getWxOpenid(String token) throws ServiceException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("wxOpenid").asString();
        } catch (JWTDecodeException e) {
            log.error("error：{}", e.getMessage());
            throw ServiceException.CONST_token_is_not_validate;
        }
    }

    // ************************* 后台管理员认证 ********************** //
    // 生成 token
    public static String generateTokenWithUserId(Integer userId, long expireTime) {
        try {
            return JWT.create()
                    .withClaim("userId", userId)
                    .withExpiresAt(new Date(expireTime))
                    .sign(Algorithm.HMAC256(COSNT_jwt_secret)); // 加上服务器秘钥 也可以再加上用户密码
        } catch (Exception e) {
            log.error("error：{}", e.getMessage());
            return null;
        }
    }

    // 从 token中获取用户id
    public static Integer getSysUserId(String token) throws ServiceException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asInt();
        } catch (JWTDecodeException e) {
            log.error("error：{}", e.getMessage());
            throw ServiceException.CONST_token_is_not_validate;
        }
    }


}
