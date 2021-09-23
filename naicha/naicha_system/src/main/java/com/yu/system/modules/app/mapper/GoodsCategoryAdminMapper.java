package com.yu.system.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.common.entity.app.GoodsCategory;
import org.apache.ibatis.annotations.Update;

/**
 * 商品类别
 */
public interface GoodsCategoryAdminMapper extends BaseMapper<GoodsCategory> {

    @Update("update goods_category set show_status = !show_status where name=#{param1}; ")
    int updateCategoryShowStatus(String name);

    // 修改商品类别信息
    @Update("update goods_category set name = #{param2}, display_order=#{param3} " +
            " where name = #{param1}; ")
    int updateGoodsCategoryAdmin(String oldName, String newName, Integer displayOrder);
}
