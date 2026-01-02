package com.uce.emprendimiento.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/signup").setViewName("forward:/pages/signup.html");
        registry.addViewController("/products").setViewName("forward:/pages/products.html");
        registry.addViewController("/profile").setViewName("forward:/pages/profile.html");
        registry.addViewController("/invoice").setViewName("forward:/pages/invoice.html");
        registry.addViewController("/history").setViewName("forward:/pages/history.html");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*") // In production, restrict this to specific domains
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
