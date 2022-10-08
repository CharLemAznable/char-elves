package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvScan;
import com.github.charlemaznable.core.spring.ElvesImport;
import com.github.charlemaznable.core.spring.ShortBeanNameGenerator;

@ElvesImport
@EnvScan(basePackageClasses = TestEnvSpringConfig.class,
        nameGenerator = ShortBeanNameGenerator.class)
public class EnvSpringConfiguration {
}
