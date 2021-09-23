package com.yu.system.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.common.entity.app.GoodsProperty;
import org.apache.ibatis.annotations.Update;

public interface GoodsPropertyAdminMapper extends BaseMapper<GoodsProperty> {

    // 重置同类商品属性的默认状态
    @Update("update goods_property set is_default = 0 where goods_id=#{param1} and category=#{param2}; ")
    int resetDefaultStatusOfSameGoodsProperty(Integer goodsId, String category);

    @Update("UPDATE goods_property SET is_default = 0 ;")
    void resetIsDefault();
}
