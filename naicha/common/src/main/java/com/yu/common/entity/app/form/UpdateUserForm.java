package com.yu.common.entity.app.form;

import lombok.Data;

@Data
public class UpdateUserForm {
    private String name;
    private String address;
    private String wxAvatar;
    private String phone;
    private Byte sex;
}
