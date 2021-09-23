package com.yu.system.modules.system.entity.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("管理员登录表单")
@Data
public class SysUserLoginForm {
    private String username;
    private String password;
    private String uuid; // 用于去redis里面找验证码
    private String verifyCode; // 验证码
    private String ip;
}
