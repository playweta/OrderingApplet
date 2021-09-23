package com.yu.common.entity.app.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("历史订单列表信息")
public class HistoryOrderVO {
    private String orderNo;
    private String orderStatus;
    @ApiModelProperty("商品大致信息，eg: 奶茶*2")
    private String goodsPreview;
    private Integer goodsTotalNum;
    private String createTime;
    private Integer payPrice;
}
