package com.yu.common.common.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class Swagger2Config {
    @Value("${my.properties.swaggerEnable}")
    private boolean swaggerEnable;

    @Bean
    public Docket createRestApi() {  // docket摘要
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("API")
                        .description("")
                        .version("")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.yu"))
                .paths(PathSelectors.any())
                .build().enable(swaggerEnable); // 开发环境就打开swagger
    }
}
