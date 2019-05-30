package com.activiti.oaf.adapter.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class OAFSwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()                 
                .apis(RequestHandlerSelectors.basePackage("com.activiti.oaf.adapter.controller"))
//                .paths(regex("/manageOAF"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }
    
    private ApiInfo metaData() {
        ApiInfo apiInfo = new ApiInfo(
                "Manage OAF Adapter Service",
                "This service is responsible to manage OAF json data into database",
                "1.0",
                "Terms of service",
                new Contact("EnProwess Technologies Pvt. Ltd.", "http://www.enprowess.com", "info@enprowess.com"),
               "",
                "");
        return apiInfo;
    }
}
