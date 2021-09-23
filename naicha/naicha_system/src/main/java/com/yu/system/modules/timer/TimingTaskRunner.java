package com.yu.system.modules.timer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yu.common.common.constant.Const;
import com.yu.common.common.enums.GoodsPropertyCategory;
import com.yu.common.common.enums.OrderStatus;
import com.yu.common.entity.app.GoodsProperty;
import com.yu.common.entity.app.OrderInfo;
import com.yu.common.entity.app.dto.GoodsDTO;
import com.yu.common.service.RedisService;
import com.yu.system.modules.app.mapper.GoodsAdminMapper;
import com.yu.system.modules.app.mapper.GoodsPropertyAdminMapper;
import com.yu.system.modules.app.mapper.OrderInfoAdminMapper;
import com.yu.system.modules.app.service.OrderInfoAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 定时任务
 */
@EnableScheduling
@Slf4j
@Component
public class TimingTaskRunner {
    @Resource
    private OrderInfoAdminMapper orderInfoAdminMapper;
    @Resource
    private OrderInfoAdminService orderInfoAdminService;
    @Resource
    private GoodsAdminMapper goodsAdminMapper;
    @Resource
    private GoodsPropertyAdminMapper goodsPropertyAdminMapper;
    @Resource
    private RedisService redisService;

    // 刷新商品菜单列表缓存
    public void updateGoodsMenuListRedisCache() {
        log.info("刷新商品菜单列表缓存");
        redisService.del(Const.CONST_goods_menu_vo_cache);
    }

    // TODO 重置所有商品的默认属性和默认价格
    @Transactional(rollbackFor = Exception.class)
    public void resetGoodsDefaultProperty() {
        log.info("重置所有商品的默认属性和默认价格");
        goodsPropertyAdminMapper.resetIsDefault(); // 全部重置
        for (GoodsDTO goods : goodsAdminMapper.selectList(null)) {
            // 重置默认价格和默认选择的大小
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_size.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_temperature.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_tian_du.value, goods);
            resetGoodsDefaultPropertyHelper(GoodsPropertyCategory.ENUM_kou_wei.value, goods);
        }
        updateGoodsMenuListRedisCache();
    }

    // 重置默认的必选属性
    private void resetGoodsDefaultPropertyHelper(String propertyCategory, GoodsDTO goods) {
        List<GoodsProperty> properties = goodsPropertyAdminMapper.selectList(
                new QueryWrapper<GoodsProperty>().eq("goods_id", goods.getId())
                        .eq("category", propertyCategory)
        );
        if (properties != null && properties.size() >= 1) {
            // 随便设置一个默认的属性
            GoodsProperty goodsProperty = properties.get(0);
            goodsProperty.setIsDefault(true);
            goodsPropertyAdminMapper.updateById(goodsProperty);

            if (GoodsPropertyCategory.ENUM_size.value.equalsIgnoreCase(propertyCategory)) {  // 大小属性就要重置商品的默认价格
                goods.setDefaultPrice(goodsProperty.getRebasePrice());
                goodsAdminMapper.updateById(goods);
            }
        }
    }

    // 每天确认已经完成的订单
    @Scheduled(cron = "0 0 3 * * ? ")
    public void confirmOrder() {
        log.info("[timing task] 每天确认已经完成的订单");
        OrderInfo order = new OrderInfo();
        order.setOrderStatus(OrderStatus.ENUM_has_completed.value);
        orderInfoAdminMapper.update(order,
                new QueryWrapper<OrderInfo>().eq("order_status", OrderStatus.ENUM_has_received.value));
    }
}
