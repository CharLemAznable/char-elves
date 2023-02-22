package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvScan;
import com.github.charlemaznable.core.config.EnvScannerRegistrar;
import com.github.charlemaznable.core.config.TestFactoryConfig;
import com.github.charlemaznable.core.spring.ElvesImport;
import com.github.charlemaznable.core.spring.ShortBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ElvesImport
@EnvScan(basePackageClasses = TestEnvSpringConfig.class,
        nameGenerator = ShortBeanNameGenerator.class,
        includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {TestEnvSpringConfig.class})},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {TestFactoryConfig.class})})
public class EnvSpringConfiguration {

    @Bean
    public EnvScannerRegistrar.EnvFactoryBean testFactoryConfig() {
        return EnvScannerRegistrar.buildFactoryBean(TestFactoryConfig.class);
    }
}
