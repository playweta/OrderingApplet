package com.yu.common.entity.app.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yu.common.entity.app.Goods;
import com.yu.common.entity.app.GoodsProperty;
import lombok.Data;

import java.util.List;

@Data
@TableName("goods")
public class GoodsDTO extends Goods {

    // 商品的属性列表
    @TableField(exist = false)
    private List<GoodsProperty> goodsPropertyList;

}
