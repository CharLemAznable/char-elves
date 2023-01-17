package com.github.charlemaznable.core.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class MutableHttpServletFilterConfiguration implements WebMvcConfigurer {

    @Autowired
    private MutableHttpServletFilterInterceptor mutableHttpServletFilterInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(mutableHttpServletFilterInterceptor).addPathPatterns("/**");
    }
}
