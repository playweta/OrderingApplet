package com.yu.common.entity.system;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

@ApiModel("")
@Data
@TableName("sys_role")
public class Role implements Serializable {
	private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;

    private String name;

    private String description;

}
