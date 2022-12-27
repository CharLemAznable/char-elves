package com.github.charlemaznable.core.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@NeoComponentScan
@Import({SpringContextRegistrar.class})
public class ElvesAutoConfiguration {
}
