package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvScan;
import com.github.charlemaznable.core.spring.ElvesImport;

@ElvesImport
@EnvScan(basePackageClasses = TestEnvSpringConfig.class)
public class EnvSpringConfiguration {
}
