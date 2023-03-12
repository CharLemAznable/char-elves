package com.github.charlemaznable.core.config;

@EnvConfig
public interface TestFactoryConfig {

    String key1();

    String key2();

    String key3();

    @EnvConfig("testIni.key4")
    String key4();
}
