package com.yu.system.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.common.entity.app.GoodsProperty;
import com.yu.system.modules.app.mapper.GoodsAdminMapper;
import com.yu.system.modules.app.mapper.GoodsPropertyAdminMapper;
import com.yu.system.modules.app.service.GoodsAdminService;
import com.yu.common.common.annotation.NeedPermission;
import com.yu.common.common.enums.GoodsPropertyCategory;
import com.yu.common.common.exception.ServiceException;
import com.yu.common.common.util.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "系统：商品属性管理")
@RestController
@RequestMapping("/goodsPropertyAdmin")
public class GoodsPropertyAdminController {

    @Resource
    private GoodsAdminService goodsAdminService;
    @Resource
    private GoodsAdminMapper goodsAdminMapper;
    @Resource
    private GoodsPropertyAdminMapper goodsPropertyAdminMapper;

    @ApiOperation("设置商品的默认属性")
    @NeedPermission("system:app:goods:update")
    @PutMapping("/setDefault/{goodsPropertyId}")
    public Result<Integer> setDefaultGoodsProperty(@PathVariable String goodsPropertyId) throws ServiceException {
        GoodsProperty goodsProperty = goodsPropertyAdminMapper.selectById(goodsPropertyId);
        if (GoodsPropertyCategory.ENUM_jia_liao.value.equals(goodsProperty.getCategory()))  // 加料
            throw ServiceException.CONST_jia_liao_can_not_set_default_property;

        // 先重置默认的状态
        goodsPropertyAdminMapper.resetDefaultStatusOfSameGoodsProperty(goodsProperty.getGoodsId(), goodsProperty.getCategory());
        goodsProperty.setIsDefault(true);
        // 更新商品的默认价格
        if (GoodsPropertyCategory.ENUM_size.value.equals(goodsProperty.getCategory())) { // 是大小属性时更新默认价格
            goodsAdminMapper.updateGoodsDefaultPrice(goodsProperty.getGoodsId(), goodsProperty.getRebasePrice());
        }

        return Result.ok(goodsPropertyAdminMapper.updateById(goodsProperty));
    }

    @ApiOperation("添加商品属性")
    @NeedPermission("system:app:goods:update")
    @PostMapping
    public Result<Integer> addGoodsProperty(@RequestBody GoodsProperty goodsProperty) {
        if (GoodsPropertyCategory.ENUM_jia_liao.value.equals(goodsProperty.getCategory())) { // 属性种类为: 加料
            goodsProperty.setIsDefault(false);
        } else {
            // 同类属性的数量
            Integer sameCategoryPropertyCount = goodsPropertyAdminMapper.selectCount(
                    new QueryWrapper<GoodsProperty>().eq("goods_id", goodsProperty.getGoodsId())
                            .eq("category", goodsProperty.getCategory())
            );
            if (sameCategoryPropertyCount <= 0) { // 第一个加入的属性就设置为默认属性
                goodsProperty.setIsDefault(true);
                if (GoodsPropertyCategory.ENUM_size.value.equals(goodsProperty.getCategory())) // 属性为大小时更换默认价格
                    goodsAdminMapper.updateGoodsDefaultPrice(goodsProperty.getGoodsId(), goodsProperty.getRebasePrice());
            } else
                goodsProperty.setIsDefault(false);
        }
        return Result.ok(goodsPropertyAdminMapper.insert(goodsProperty));
    }

    @ApiOperation("更新商品属性")
    @NeedPermission("system:app:goods:update")
    @PutMapping
    public Result<Integer> updateGoodsCategory(@RequestBody GoodsProperty goodsProperty) {
        if (goodsProperty.getIsDefault() && GoodsPropertyCategory.ENUM_size.value.equals(goodsProperty.getCategory())) {
            // 同时更新默认价格
            goodsAdminMapper.updateGoodsDefaultPrice(goodsProperty.getGoodsId(), goodsProperty.getRebasePrice());
        } else {
            goodsProperty.setIsDefault(null); // 忽略is_default字段
        }
        return Result.ok(goodsPropertyAdminMapper.updateById(goodsProperty));

    }

    @ApiOperation("删除商品属性")
    @NeedPermission("system:app:goods:update")
    @DeleteMapping("/{goodsPropertyId}")
    public Result<Integer> deleteGoodsCategory(@PathVariable String goodsPropertyId) {
        GoodsProperty goodsProperty = goodsPropertyAdminMapper.selectById(goodsPropertyId);
        // 如果不是默认属性或是加料类型就直接删除
        if (goodsProperty.getIsDefault() || GoodsPropertyCategory.ENUM_jia_liao.value.equals(goodsProperty.getCategory()))
            return Result.ok(goodsPropertyAdminMapper.deleteById(goodsPropertyId));

        List<GoodsProperty> goodsPropertyList = goodsPropertyAdminMapper.selectList(
                new QueryWrapper<GoodsProperty>().eq("goods_id", goodsProperty.getGoodsId())
                        .eq("category", goodsProperty.getCategory())
        );
        if (CollectionUtils.isEmpty(goodsPropertyList)) // 该类属性只有它一个也直接删除
            return Result.ok(goodsPropertyAdminMapper.deleteById(goodsPropertyId));

        // 重新设置一个默认属性
        GoodsProperty randomGoodsProperty = goodsPropertyList.get(0);
        randomGoodsProperty.setIsDefault(true);
        goodsPropertyAdminMapper.updateById(randomGoodsProperty);
        // 更新商品的默认价格
        if (GoodsPropertyCategory.ENUM_size.value.equals(goodsProperty.getCategory()))
            goodsAdminMapper.updateGoodsDefaultPrice(goodsProperty.getGoodsId(), goodsProperty.getRebasePrice());

        return Result.ok(goodsPropertyAdminMapper.deleteById(goodsPropertyId));
    }

}
