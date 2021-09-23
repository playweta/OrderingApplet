package com.yu.common.service;

import com.yu.common.common.constant.Const;
import com.yu.common.entity.common.AppConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AppConfigService {

    @Resource
    private RedisService redisService;

    // 获取小程序配置
    public AppConfig getAppConfig()  {
        Object o = redisService.get(Const.CONST_app_config);
        if (o != null)
            return (AppConfig) o;

        AppConfig appConfig = new AppConfig();
        redisService.set(Const.CONST_app_config, appConfig);
        return appConfig;
    }
}
