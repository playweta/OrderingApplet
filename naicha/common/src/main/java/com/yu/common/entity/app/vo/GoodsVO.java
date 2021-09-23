package com.yu.common.entity.app.vo;

import com.yu.common.entity.app.Goods;
import lombok.Data;

import java.util.List;

// 商品属性 前端商品列表展示
@Data
public class GoodsVO extends Goods {

    private Integer number; // 购买数量
    private Integer realPrice; // 具体价格
    private String propertyStr; // 具体属性
    private List<SameCategoryPropertyVO> goodsPropertyVos;
}
