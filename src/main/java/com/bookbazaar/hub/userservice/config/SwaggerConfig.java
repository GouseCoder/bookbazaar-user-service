package com.bookbazaar.hub.userservice.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bookbazaar.hub.userservice.utils.SwaggerConstants;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	@Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
        	.apiInfo(getSwaggerInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.bookbazaar.hub.userservice.controller"))
            .paths(PathSelectors.any())
            .build();
    }

	private ApiInfo getSwaggerInfo() {
		
		return new ApiInfo(SwaggerConstants.TITLE, SwaggerConstants.DESCRIPTION, SwaggerConstants.VERSION1, SwaggerConstants.TERMS_OF_SERVICE, 
				new Contact(SwaggerConstants.CONTACT_NAME, SwaggerConstants.CONTACT_EMAIL, SwaggerConstants.CONTACT_URL), 
				SwaggerConstants.LISCENCE_NAME,SwaggerConstants.LISCENCE_URL, Collections.emptyList());
	}

}
