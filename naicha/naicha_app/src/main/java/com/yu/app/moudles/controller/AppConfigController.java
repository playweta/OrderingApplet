package com.yu.app.moudles.controller;

import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import com.yu.common.entity.common.AppConfig;
import com.yu.common.service.AppConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "小程序配置")
@RestController
public class AppConfigController {

    @Resource
    private AppConfigService appConfigService;

    @ApiOperation("获取小程序的所有配置")
    @GetMapping("/config")
    public Result<AppConfig> getAppConfig() throws ServiceException {
        return Result.ok(appConfigService.getAppConfig());
    }
}
