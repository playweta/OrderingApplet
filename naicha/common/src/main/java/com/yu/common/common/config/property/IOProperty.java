package com.yu.common.common.config.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class  IOProperty {
    // 图片上传到服务器的的本地路径
    @Value("${my.properties.filePath.imageFileRootPath}")
    private String imageFileRootPath;

}
