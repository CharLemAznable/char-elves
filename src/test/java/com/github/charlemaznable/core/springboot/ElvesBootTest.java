package com.github.charlemaznable.core.springboot;

import com.github.charlemaznable.core.spring.MutableHttpServletFilter;
import com.github.charlemaznable.core.spring.SpringContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.joor.Reflect.onClass;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = NONE)
public class ElvesBootTest {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MutableHttpServletFilter mutableHttpServletFilter;

    @Test
    public void testElvesBoot() {
        assertSame(applicationContext,
                onClass(SpringContext.class).field("applicationContext").get());
        assertNotNull(mutableHttpServletFilter);
    }
}
