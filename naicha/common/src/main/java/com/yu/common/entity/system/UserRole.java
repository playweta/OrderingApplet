package com.yu.common.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel("")
@Data
@TableName("sys_user_role")
public class UserRole implements Serializable {
	private static final long serialVersionUID = 1L;

    private Integer userId;

    private Integer roleId;


}
