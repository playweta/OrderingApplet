package com.yu.system.modules.timer;

import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

// 执行定时任务
@Api(tags = "系统：定时任务")
@RestController
@RequestMapping("/timingTask")
public class TimingTaskController {
    @Resource
    private TimingTaskRunner timingTaskRunner;

    @ApiOperation("执行一次定时任务")
    @NeedPermission("system:admin:timingTask:execute")
    @GetMapping("/execute/{taskMethodName}")
    public Result<String> checkGoodsProperty(@PathVariable String taskMethodName) {
        if ("resetGoodsDefaultProperty".equalsIgnoreCase(taskMethodName)) {
            timingTaskRunner.resetGoodsDefaultProperty();
        } else if ("updateGoodsMenuListRedisCache".equalsIgnoreCase(taskMethodName)) {
            timingTaskRunner.updateGoodsMenuListRedisCache();
        } else return Result.fail("没有该任务: " + taskMethodName);

        return Result.ok("执行成功");
    }


}
