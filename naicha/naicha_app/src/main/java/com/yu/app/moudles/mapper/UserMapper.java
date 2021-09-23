package com.yu.app.moudles.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.common.entity.app.User;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {
    @Update("update `user` set wx_avatar=#{param1} where wx_openid = #{param2} ")
    int updateWxAvatar(String wxAvatar, String wxOpenid);
}
