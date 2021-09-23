package com.yu.common.common.annotation;

import java.lang.annotation.*;

// 用于标记方法需要登录认证
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NeedLogin {
}
