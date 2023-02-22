package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvFactory;
import com.github.charlemaznable.core.spring.SpringContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringJUnitConfig(EnvSpringFilterConfiguration.class)
public class EnvSpringFilterTest {

    @Autowired(required = false)
    private TestEnvSpringConfig testEnvConfig;
    @Autowired
    private TestBaseConfig testBaseConfig;
    @Autowired
    private TestBaseConfig.BaseConfig baseConfig;
    @Autowired
    private TestBaseConfig.ExtendConfig extendConfig;

    @Test
    public void testScannerFilter() {
        assertNull(testEnvConfig);

        assertNotNull(testBaseConfig);
        assertSame(testBaseConfig, SpringContext.getBean(TestBaseConfig.class));
        assertSame(testBaseConfig, EnvFactory.getEnv(TestBaseSubConfig.class));

        assertNotNull(baseConfig);
        assertEquals(testBaseConfig.keyBase(), baseConfig.getValue());
        assertSame(baseConfig, SpringContext.getBean("TestBaseSubConfig.baseConfig"));
        assertNotSame(baseConfig, testBaseConfig.baseConfig());

        assertNotNull(extendConfig);
        assertEquals(testBaseConfig.keyBase(), extendConfig.getValue());
        assertSame(extendConfig, SpringContext.getBean("TestBaseSubConfig.extendConfig"));
        assertNotSame(extendConfig, testBaseConfig.extendConfig(baseConfig));

        assertNull(SpringContext.getBean("TestBaseSubConfig.noBaseConfig"));
    }
}
