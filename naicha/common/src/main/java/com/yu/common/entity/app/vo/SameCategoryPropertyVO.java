package com.yu.common.entity.app.vo;

import com.yu.common.entity.app.GoodsProperty;
import lombok.Data;

import java.util.List;

// 同一类别的属性
@Data
public class SameCategoryPropertyVO {
    private String category;
    private Boolean required;
    private List<GoodsProperty> propertyList;
}
