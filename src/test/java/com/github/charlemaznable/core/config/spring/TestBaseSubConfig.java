package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

@EnvConfig
public interface TestBaseSubConfig extends TestBaseConfig {

    @Bean
    @Override
    default BaseConfig baseConfig() {
        return TestBaseConfig.super.baseConfig();
    }

    @Bean
    @Override
    default ExtendConfig extendConfig(@Qualifier("baseConfig") BaseConfig baseConfig) {
        return TestBaseConfig.super.extendConfig(baseConfig);
    }
}
