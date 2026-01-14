package com.uce.emprendimiento.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirecciones directas (sin lógica Java detrás)

        // Login y Root
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/signup").setViewName("forward:/pages/signup.html");

        // Páginas del Dashboard
        // Esto permite que cuando entres a localhost:8080/products
        // Spring te muestre el archivo /pages/products.html sin cambiar la URL
        registry.addViewController("/home").setViewName("forward:/pages/home.html");
        registry.addViewController("/products").setViewName("forward:/pages/products.html");
        registry.addViewController("/profile").setViewName("forward:/pages/profile.html");
        registry.addViewController("/invoice").setViewName("forward:/pages/invoice.html");
        registry.addViewController("/history").setViewName("forward:/pages/history.html");
        registry.addViewController("/invoiceDetails").setViewName("forward:/pages/invoiceDetails.html");
    }
}