package com.yu.app.moudles.controller;

import com.yu.common.entity.app.vo.GoodsMenuVO;
import com.yu.app.moudles.service.GoodsServiceImpl;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "商品服务")
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Resource
    private GoodsServiceImpl goodsService;

    @ApiOperation("获取要显示的商品菜单列表")
    @GetMapping("/goodsMenu/list")
    public Result<List<GoodsMenuVO>> getGoodsMenuDetailList() {
        return Result.ok(goodsService.getGoodsMenuDetailList());
    }

}

