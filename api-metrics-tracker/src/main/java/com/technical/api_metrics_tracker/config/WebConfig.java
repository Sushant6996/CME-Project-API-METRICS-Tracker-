package com.technical.api_metrics_tracker.config;

import com.technical.api_metrics_tracker.interceptor.ResponseTimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ResponseTimeInterceptor responseTimeInterceptor;

    @Autowired
    public WebConfig(ResponseTimeInterceptor responseTimeInterceptor) {
        this.responseTimeInterceptor = responseTimeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(responseTimeInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Path pattern to allow CORS for
                .allowedOrigins("http://localhost:3000") // Frontend URL (can be adjusted)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allowed methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow cookies if needed
    }
}
