package com.yu.app;

import com.yu.app.moudles.mapper.GoodsMapper;
import com.yu.app.moudles.mapper.GoodsPropertyMapper;
import com.yu.common.common.enums.GoodsPropertyCategory;
import com.yu.common.entity.app.GoodsProperty;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@SpringBootApplication(scanBasePackages = {"com.yu.app", "com.yu.common" })
@MapperScan("com.yu.**.mapper")
@RestController
public class NaichaAppRun {
    public static void main(String[] args) {
        SpringApplication.run(NaichaAppRun.class, args);
    }

    @Resource
    private GoodsPropertyMapper goodsPropertyMapper;

    @Resource
    private GoodsMapper goodsMapper;


    @GetMapping({"/ping", "/"})
    public String run(HttpServletRequest request) {
        return "服务器正常运行 [app]";
    }


    @GetMapping({"/404"})
    public String _404() {
        return "不存在对应资源 [naicha app]";
    }

//    @GetMapping({"/generate"})
//    public String generate(HttpServletRequest request) {
//        List<GoodsProperty> properties = generatePropertis();
//        goodsPropertyMapper.delete(null);
//        List<Goods> goods = goodsMapper.selectList(null);
//        System.out.println(goods);
//        for (Goods good : goods) {
//            for (GoodsProperty property : properties) {
//                property.setGoodsId(good.getId());
//                goodsPropertyMapper.insert(property);
//                System.out.println("属性添加成功...");
//            }
//        }
//        return "生成商品属性成功";
//    }



    private List<GoodsProperty> generatePropertis() {
        List<GoodsProperty> properties = new ArrayList<>();
        // 大杯
        GoodsProperty property = new GoodsProperty();
        property.setCategory(GoodsPropertyCategory.ENUM_size.value); // 大小
        property.setPropertyOption("大");
        property.setRebasePrice(10000);
        property.setIsDefault(false);
        properties.add(property);
        // 中杯
        GoodsProperty property1 = new GoodsProperty();
        property1.setCategory(GoodsPropertyCategory.ENUM_size.value); // 大小
        property1.setPropertyOption("中");
        property1.setRebasePrice(10000);
        property1.setIsDefault(true);
        properties.add(property1);
        /// 甜度
        GoodsProperty p2 = new GoodsProperty();
        p2.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p2.setPropertyOption("无糖");
        p2.setIsDefault(false);
        properties.add(p2);

        GoodsProperty p3 = new GoodsProperty();
        p3.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p3.setPropertyOption("微糖");
        p3.setIsDefault(false);
        properties.add(p3);

        GoodsProperty p4 = new GoodsProperty();
        p4.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p4.setPropertyOption("半糖");
        p4.setIsDefault(false);
        properties.add(p4);

        GoodsProperty p5 = new GoodsProperty();
        p5.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p5.setPropertyOption("少糖");
        p5.setIsDefault(false);
        properties.add(p5);

        GoodsProperty p6 = new GoodsProperty();
        p6.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p6.setPropertyOption("正常甜");
        p6.setIsDefault(true);
        properties.add(p6);

        GoodsProperty p7 = new GoodsProperty();
        p7.setCategory(GoodsPropertyCategory.ENUM_tian_du.value);
        p7.setPropertyOption("全糖");
        p7.setIsDefault(false);
        properties.add(p7);

        // 温度
        GoodsProperty wenduProperty1 = new GoodsProperty();
        wenduProperty1.setCategory(GoodsPropertyCategory.ENUM_temperature.value);
        wenduProperty1.setPropertyOption("常温");
        wenduProperty1.setIsDefault(true);
        properties.add(wenduProperty1);

        GoodsProperty wenduProperty2 = new GoodsProperty();
        wenduProperty2.setCategory(GoodsPropertyCategory.ENUM_temperature.value);
        wenduProperty2.setPropertyOption("热");
        wenduProperty2.setIsDefault(false);
        properties.add(wenduProperty2);

        GoodsProperty wenduProperty3 = new GoodsProperty();
        wenduProperty3.setCategory(GoodsPropertyCategory.ENUM_temperature.value);
        wenduProperty3.setPropertyOption("冰");
        wenduProperty3.setIsDefault(false);
        properties.add(wenduProperty3);

        GoodsProperty wenduProperty4 = new GoodsProperty();
        wenduProperty4.setCategory(GoodsPropertyCategory.ENUM_temperature.value);
        wenduProperty4.setPropertyOption("少冰");
        wenduProperty4.setIsDefault(false);
        properties.add(wenduProperty4);

        GoodsProperty wenduProperty5 = new GoodsProperty();
        wenduProperty5.setCategory(GoodsPropertyCategory.ENUM_temperature.value);
        wenduProperty5.setPropertyOption("去冰");
        wenduProperty5.setIsDefault(false);
        properties.add(wenduProperty5);

        // 加料
        GoodsProperty jialiaoProperty1 = new GoodsProperty();
        jialiaoProperty1.setCategory(GoodsPropertyCategory.ENUM_jia_liao.value);
        jialiaoProperty1.setPropertyOption("布丁");
        jialiaoProperty1.setIsDefault(false);
        jialiaoProperty1.setExtraPrice(200);
        properties.add(jialiaoProperty1);

        GoodsProperty jialiaoProperty2 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, jialiaoProperty2);
        jialiaoProperty2.setPropertyOption("红豆");
        properties.add(jialiaoProperty2);

        GoodsProperty 珍珠 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 珍珠);
        珍珠.setPropertyOption("珍珠");
        properties.add(珍珠);

        GoodsProperty 西米 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 西米);
        西米.setPropertyOption("西米");
        properties.add(西米);

        GoodsProperty 椰果 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 椰果);
        椰果.setPropertyOption("椰果");
        properties.add(椰果);

        GoodsProperty 青稞 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 青稞);
        青稞.setPropertyOption("青稞");
        properties.add(青稞);

        GoodsProperty 芋圆 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 芋圆);
        芋圆.setPropertyOption("芋圆");
        properties.add(芋圆);

        GoodsProperty 血糯米 = new GoodsProperty();
        BeanUtils.copyProperties(jialiaoProperty1, 血糯米);
        血糯米.setPropertyOption("血糯米");
        properties.add(血糯米);

        return properties;

    }


}
