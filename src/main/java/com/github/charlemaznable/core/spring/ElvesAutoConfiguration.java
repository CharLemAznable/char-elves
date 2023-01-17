package com.github.charlemaznable.core.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SpringContextRegistrar.class})
public class ElvesAutoConfiguration {

    @Bean("com.github.charlemaznable.core.spring.SpringContext")
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Bean("com.github.charlemaznable.core.spring.MutableHttpServletFilter")
    public MutableHttpServletFilter mutableHttpServletFilter() {
        return new MutableHttpServletFilter();
    }
}
