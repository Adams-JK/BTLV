package com.zykj.btlv.config;

import com.zykj.btlv.result.ResultEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：lp
 * @version :
 * @date ：2020/5/16 14:37
 * @description: Interface Document Configuration
 * @modified By：
 */
@Configuration
public class SwaggerConfig {

    @Value("${swagger.enable}")
    public Boolean swaggerEnable;

    @Bean
    public Docket desertsApi(){

        List<Response> globalResponses = new ArrayList<>();

        Class<ResultEnum> enumClass = ResultEnum.class;

        ResultEnum[] enumConstants = enumClass.getEnumConstants();

        for(ResultEnum resultEnum:enumConstants){
            globalResponses.add(new ResponseBuilder()
                    .code(String.valueOf(resultEnum.getCode()))
                    .description(resultEnum.getMsg())
                    .build());
        }

        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(swaggerEnable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.zykj.btlv.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false)
                .globalResponses(HttpMethod.GET,globalResponses)
                .globalResponses(HttpMethod.POST,globalResponses)
                .groupName("cgr");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("cgr")
                .description("接口文档")
                .termsOfServiceUrl("http://1.1.1.1:8099/swagger-ui.html")
                .version("3.0.0")
                .build();
    }
}