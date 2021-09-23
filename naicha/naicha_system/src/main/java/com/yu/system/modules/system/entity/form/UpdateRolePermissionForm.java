package com.yu.system.modules.system.entity.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@ApiModel("角色权限表单")
@Data
public class UpdateRolePermissionForm {
    private Integer roleId;
    private List<String> permissions;
}
