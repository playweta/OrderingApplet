package com.yu.common.entity.system;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("后台管理员")
@Data
@TableName("sys_user")
public class SysUser implements Serializable {
	private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;
    private String username;
    private String avatar;
    private Integer roleId;
    private String password;
    @ApiModelProperty("账号是否激活")
    private Boolean status;

}
