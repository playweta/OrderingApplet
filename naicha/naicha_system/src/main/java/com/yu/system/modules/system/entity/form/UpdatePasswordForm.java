package com.yu.system.modules.system.entity.form;

import lombok.Data;

@Data
public class UpdatePasswordForm {
    private String oldPassword;

    private String newPassword;
}
