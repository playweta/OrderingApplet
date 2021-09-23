package com.yu.common.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("用户")
@Data
public class User {
    @TableId(type = IdType.INPUT)
    private String wxOpenid;
    private String name;
    private String address;
    private String wxAvatar;
    private String phone;
    private Byte sex;
    private Boolean status;

    @TableField(exist = false)
    private String token; // 认证令牌，在缓存会话信息的时候生成

}
