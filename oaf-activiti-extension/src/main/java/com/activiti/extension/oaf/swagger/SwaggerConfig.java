package com.activiti.extension.oaf.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()                 
//                .apis(RequestHandlerSelectors.basePackage("com.activiti.extension.oaf.api"))
                .apis(RequestHandlerSelectors.basePackage("com.activiti.extension.api"))
                .paths(regex("/enterprise/*"))
                .build()
                .apiInfo(metaData());
    }
    
    private ApiInfo metaData() {
        ApiInfo apiInfo = new ApiInfo(
                "OAF Service APIs",
                "This service is responsible to manage OAF json data at APS",
                "1.0",
                "Terms of service",
                new Contact("EnProwess Technologies Pvt. Ltd.", "http://www.enprowess.com", "info@enprowess.com"),
               "",
                "");
        return apiInfo;
    }
}
