package com.yu.app.common.interceptor;


import com.yu.common.common.annotation.NeedLogin;
import com.yu.common.common.constant.Const;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.jwt.JWTUtil;
import com.yu.app.common.util.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 小程序后台api拦截器
@Slf4j
@Component
public class AppApiInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ((handler instanceof HandlerMethod)) {
            NeedLogin annotation = ((HandlerMethod) handler).getMethod().getAnnotation(NeedLogin.class);
            if (annotation != null) {
                String token = SessionUtil.getToken(request);
                if (StringUtils.isEmpty(token)) {
                    log.error("未携带token，拦截，请求路径[{}][{}]", request.getMethod(), request.getServletPath());
                    return false;
                }

                // 检测token的有效性
                if (!JWTUtil.verify(token))
                    throw ServiceException.CONST_token_is_not_validate;

                String wxOpenid = JWTUtil.getWxOpenid(token);
                // 检验登录
                if (!SessionUtil.checkUserLogin(wxOpenid)) {
                    log.error("拦截未登录 请求路径[{}][{}]", request.getMethod(), request.getServletPath());
                    renderResponse(request, response);
                    return false;
                }

                // 将wxOpenid放到request的属性里
                request.setAttribute(Const.CONST_wx_openid, wxOpenid);
            }
        }
        return super.preHandle(request, response, handler);
    }

    private void renderResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("content-type", "application/json;charset=UTF-8 ");
        response.getWriter().println("{\"code\":10001,\"message\":\"未登录或登录已过期\",\"data\":null}");
        response.flushBuffer();
    }
}
