package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nonnull;

@EnvConfig
public interface TestBaseSubConfig extends TestBaseConfig {

    @Bean
    @Override
    default BaseConfig baseConfig() {
        return TestBaseConfig.super.baseConfig();
    }

    @Bean
    @Override
    default ExtendConfig extendConfig(BaseConfig baseConfig) {
        return TestBaseConfig.super.extendConfig(baseConfig);
    }

    class MyCondition implements Condition {

        @Override
        public boolean matches(@Nonnull ConditionContext context,
                               @Nonnull AnnotatedTypeMetadata metadata) {
            return false;
        }
    }

    @Conditional(MyCondition.class)
    @Bean
    default BaseConfig noBaseConfig() {
        return TestBaseConfig.super.baseConfig();
    }
}
