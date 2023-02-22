package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvScan;
import com.github.charlemaznable.core.spring.ElvesImport;
import com.github.charlemaznable.core.spring.ShortBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ElvesImport
@EnvScan(nameGenerator = ShortBeanNameGenerator.class,
        includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {TestBaseConfig.class})},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {TestEnvSpringConfig.class})})
public class EnvSpringFilterConfiguration {
}
