package com.github.charlemaznable.core.config.spring;

import com.github.charlemaznable.core.config.EnvFactory;
import com.github.charlemaznable.core.config.spring.TestBaseConfig.BaseConfig;
import com.github.charlemaznable.core.config.spring.TestBaseConfig.ExtendConfig;
import com.github.charlemaznable.core.config.spring.TestEnvSpringConfig.ConfigBean;
import com.github.charlemaznable.core.spring.SpringContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EnvSpringConfiguration.class)
public class EnvSpringTest {

    @Autowired
    private TestEnvSpringConfig testEnvConfig;
    @Autowired
    @Qualifier("TestEnvSpringConfig.configKey1")
    private ConfigBean configKey1;
    @Autowired
    @Qualifier("configKey22")
    private ConfigBean configKey2;
    @Autowired
    @Qualifier("configKey33")
    private ConfigBean configKey3;
    @Autowired
    @Qualifier("TestEnvSpringConfig.configKey4")
    private ConfigBean configKey4;

    @Test
    public void testEnvSpring() {
        assertEquals("value1", testEnvConfig.key1());
        assertEquals("value2", testEnvConfig.key2());
        assertEquals("value3", testEnvConfig.key3());
        assertEquals("value4", testEnvConfig.key4());
        assertNull(testEnvConfig.key5());
        assertEquals("value5", testEnvConfig.key5Def());
        assertEquals("value5", testEnvConfig.key5("value5"));

        assertNotNull(configKey1);
        assertNotNull(configKey2);
        assertNotNull(configKey3);
        assertNotNull(configKey4);
        assertSame(configKey1, SpringContext.getBean("TestEnvSpringConfig.configKey1"));
        assertSame(configKey2, SpringContext.getBean("configKey22"));
        assertSame(configKey3, SpringContext.getBean("configKey3"));
        assertSame(configKey4, SpringContext.getBean("TestEnvSpringConfig.configKey4"));
        assertEquals("value1", configKey1.getValue());
        assertEquals("value2", configKey2.getValue());
        assertEquals("value3", configKey3.getValue());
        assertEquals("value4", configKey4.getValue());
    }

    @Autowired
    private TestBaseConfig testBaseConfig;
    @Autowired
    private BaseConfig baseConfig;
    @Autowired
    private ExtendConfig extendConfig;

    @Test
    public void testBaseSubEnv() {
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
    }
}
