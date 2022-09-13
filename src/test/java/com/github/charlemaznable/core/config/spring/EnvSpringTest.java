package com.github.charlemaznable.core.config.spring;

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
import static org.junit.jupiter.api.Assertions.assertNull;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EnvSpringConfiguration.class)
public class EnvSpringTest {

    @Autowired
    private TestEnvSpringConfig testEnvConfig;
    @Autowired
    @Qualifier("configKey1")
    private ConfigBean configKey1;
    @Autowired
    @Qualifier("configKey22")
    private ConfigBean configKey2;
    @Autowired
    @Qualifier("configKey33")
    private ConfigBean configKey3;
    @Autowired
    @Qualifier("configKey4")
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
        assertEquals(configKey1, SpringContext.getBean("configKey1"));
        assertEquals(configKey2, SpringContext.getBean("configKey22"));
        assertEquals(configKey3, SpringContext.getBean("configKey3"));
        assertEquals(configKey4, SpringContext.getBean("configKey4"));
        assertEquals("value1", configKey1.getValue());
        assertEquals("value2", configKey2.getValue());
        assertEquals("value3", configKey3.getValue());
        assertEquals("value4", configKey4.getValue());
    }
}
