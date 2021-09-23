package com.yu.system.modules.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yu.common.entity.app.dto.GoodsDTO;
import org.apache.ibatis.annotations.Update;

public interface GoodsAdminMapper extends BaseMapper<GoodsDTO> {

    // 下架或上架商品
    @Update("update goods set is_sell = !is_sell where id=#{param1};")
    int updateSellStatus(Integer goodsId);

    // 更新商品的默认价格
    @Update("update goods set default_price = #{param2} where id=#{param1};")
    int updateGoodsDefaultPrice(Integer goodsId, Integer realPrice);

    @Update("update goods set image = #{param2} where id=#{param1};")
    int updateImageByGoodsId(Integer goodsId, String goodsImageName);

    // 修改商品的类别
    @Update("update goods set goods_category_name = #{param2} where goods_category_name = #{param1}; ")
    int updateGoodsCategory(String oldName, String name);
}
