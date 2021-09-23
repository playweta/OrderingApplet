package com.yu.common.entity.app;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel("商品")
@Data
public class Goods {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String goodsCategoryName;
    private String name;
    private Integer displayOrder; // 显示顺序
    private Integer defaultPrice;
    private Boolean isSell; // 是否在卖
    private String image;
    private String description;
}
