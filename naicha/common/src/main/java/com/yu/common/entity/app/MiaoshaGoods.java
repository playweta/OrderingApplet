package com.yu.common.entity.app;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class MiaoshaGoods {
    @TableId
    private Integer id;
    private Integer goodsId;
    private String miaoshaTitle;
    private Integer originPrice; // 原价
    private Integer miaoshaPrice;
    private Integer count; // 剩余数量
    private String image;
    private long startTime;
    private long endTime;
}

