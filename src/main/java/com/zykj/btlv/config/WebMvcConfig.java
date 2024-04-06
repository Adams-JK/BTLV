package com.zykj.btlv.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static resources cannot be accessed
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        // Resolve swagger not being accessible
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        // Swagger js file not accessible
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Set the route to be allowed across domains
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                // Whether to allow certificates（cookies）
                .allowCredentials(true)
                // Set the allowed methods
                .allowedMethods("*")
                // Time allowed across domains
                .maxAge(3600);
    }
}
