package com.github.charlemaznable.core.config.spring;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface TestBaseConfig {

    String keyBase();

    @AllArgsConstructor
    @Getter
    class BaseConfig {

        private String value;
    }

    default BaseConfig baseConfig() {
        return new BaseConfig(this.keyBase());
    }

    @AllArgsConstructor
    @Getter
    class ExtendConfig {

        private String value;
    }

    default ExtendConfig extendConfig(BaseConfig baseConfig) {
        return new ExtendConfig(baseConfig.getValue());
    }
}
