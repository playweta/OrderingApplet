package com.yu.common.common.annotation;

import java.lang.annotation.*;

// 标记接口方法, 需要登录且有对应的权限
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedPermission {
    String value() default "";
}
