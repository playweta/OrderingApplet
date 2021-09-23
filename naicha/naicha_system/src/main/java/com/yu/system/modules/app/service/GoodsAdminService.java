package com.yu.system.modules.app.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yu.common.entity.app.GoodsProperty;
import com.yu.common.entity.app.dto.GoodsDTO;
import com.yu.system.modules.app.mapper.GoodsAdminMapper;
import com.yu.system.modules.app.mapper.GoodsPropertyAdminMapper;
import com.yu.common.common.config.property.IOProperty;
import com.yu.common.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Slf4j
@Service
public class GoodsAdminService {

    @Resource
    private GoodsAdminMapper goodsAdminMapper;
    @Resource
    private IOProperty ioProperty;
    @Resource
    private GoodsPropertyAdminMapper goodsPropertyAdminMapper;

    /**
     * 分页条件查询
     *
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @return Page<GoodsAdmin>
     */
    public Page<GoodsDTO> getGoodsAdminByPage(int pageNo, int pageSize) {
        Page<GoodsDTO> page = new Page<>(pageNo, pageSize);
        goodsAdminMapper.selectPage(page, new QueryWrapper<GoodsDTO>().orderByAsc("goods_category_name"));
        List<GoodsDTO> goodsVOList = page.getRecords();
        for (GoodsDTO goods : goodsVOList) {
            // 设置商品的属性列表
            List<GoodsProperty> goodsPropertyList = goodsPropertyAdminMapper
                    .selectList(new QueryWrapper<GoodsProperty>().eq("goods_id", goods.getId()));
            goods.setGoodsPropertyList(goodsPropertyList);

        }
        return page;
    }

    // 根据id查询商品
    public GoodsDTO getGoodsById(Integer goodsId) {
        GoodsDTO goods = goodsAdminMapper.selectById(goodsId);
        goods.setGoodsPropertyList(
                goodsPropertyAdminMapper
                        .selectList(new QueryWrapper<GoodsProperty>().eq("goods_id", goodsId))
        );
        return goods;
    }

    // 增加
    @Transactional
    public int addGoodsAdmin(GoodsDTO goodsDTO) {
        return goodsAdminMapper.insert(goodsDTO);
    }

    // 批量删除
    @Transactional
    public int deleteGoodsAdminBatchIds(List<Integer> goodsAdminIdList) {
        return goodsAdminMapper.deleteBatchIds(goodsAdminIdList);
    }

    // 修改
    @Transactional
    public int updateGoodsAdmin(GoodsDTO goods) {
        return goodsAdminMapper.updateById(goods);
    }

    // 修改商品的图片
    public int updateGoodsImage(Integer goodsId, MultipartFile file) throws ServiceException {
        try {
            File dir = new File(ioProperty.getImageFileRootPath());
            if (!dir.exists()) // 不存在该目录就创建
                dir.mkdir();
            String goodsImageName = "goodsImage-" + goodsId + ".jpg";
            file.transferTo(new File(dir, goodsImageName));
            if (file.getSize() > 1024 * 1024)
                throw ServiceException.CONST_goods_image_upload_failed; // 文件超过1MB

            return goodsAdminMapper.updateImageByGoodsId(goodsId, goodsImageName);
        } catch (Exception e) {
            throw ServiceException.CONST_goods_image_upload_failed;
        }
    }

}
