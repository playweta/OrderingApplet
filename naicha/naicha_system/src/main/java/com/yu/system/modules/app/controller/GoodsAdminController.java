package com.yu.system.modules.app.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.entity.app.dto.GoodsDTO;
import com.yu.system.modules.app.mapper.GoodsAdminMapper;
import com.yu.system.modules.app.service.GoodsAdminService;
import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "系统：商铺管理")
@RestController
@RequestMapping("/goodsAdmin")
public class GoodsAdminController {

    @Resource
    private GoodsAdminService goodsAdminService;
    @Resource
    private GoodsAdminMapper goodsAdminMapper;


    @ApiOperation("分页查询")
    @NeedPermission("system:app:goods:list")
    @GetMapping("/page")
    public Result<Page<GoodsDTO>> getGoodsAdminByPage(@RequestParam(defaultValue = "1") int pageNo,
                                                      @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(goodsAdminService.getGoodsAdminByPage(pageNo, pageSize));
    }

    @ApiOperation("查询一个商品")
    @NeedPermission("system:app:goods:list")
    @GetMapping("/{goodsId}")
    public Result<GoodsDTO> getGoodsById(@PathVariable Integer goodsId) {
        return Result.ok(goodsAdminService.getGoodsById(goodsId));
    }

    @ApiOperation("增加")
    @NeedPermission("system:app:goods:add")
    @PostMapping("")
    public Result<Integer> add(@RequestBody GoodsDTO goodsAdmin) {
        return Result.ok(goodsAdminService.addGoodsAdmin(goodsAdmin));
    }

    @ApiOperation("批量删除")
    @NeedPermission("system:app:goods:delete")
    @DeleteMapping("/batch")
    public Result<Integer> delete(@RequestBody List<Integer> idList) {
        return Result.ok(goodsAdminService.deleteGoodsAdminBatchIds(idList));
    }

    @ApiOperation("修改")
    @NeedPermission("system:app:goods:update")
    @PutMapping("")
    public Result<Integer> update(@RequestBody GoodsDTO goodsAdmin) {
        return Result.ok(goodsAdminService.updateGoodsAdmin(goodsAdmin));
    }

    @ApiOperation("下架或上架商品")
    @NeedPermission("system:app:goods:update")
    @PutMapping("/updateSellStatus/{goodsId}")
    public Result<Integer> updateSellStatus(@PathVariable Integer goodsId) {
        return Result.ok(goodsAdminMapper.updateSellStatus(goodsId));
    }

    @ApiOperation("更新商品的图片")
    @NeedPermission("system:app:goods:update")
    @RequestMapping("/image")
    public Result<Integer> updateGoodsImage(@RequestParam Integer goodsId, @RequestParam MultipartFile file) throws ServiceException { // file的名字要和前端input里的name一致
        return Result.fail("改功能已关闭");
        // return Result.ok(goodsAdminService.updateGoodsImage(goodsId, file));
    }
}
