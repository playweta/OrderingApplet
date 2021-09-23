package com.yu.common.entity.app;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("商品的属性，温度甜度等")
@Data
public class GoodsProperty {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer goodsId;
    private String category; // eg: 大小、温度
    private String propertyOption; // eg: 常温
    private Boolean isDefault; // 是否作为默认选项
    private Integer rebasePrice; // 重置默认价格，只有大小类别的属性需要重置
    private Integer extraPrice; // 加料需要的额外价格
}
