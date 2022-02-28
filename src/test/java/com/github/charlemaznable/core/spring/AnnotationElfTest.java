package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.config.EnvConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.core.spring.AnnotationElf.findAnnotation;
import static com.github.charlemaznable.core.spring.AnnotationElf.resolveContainerAnnotationType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AnnotationElfTest {

    @SneakyThrows
    @Test
    public void testAnnotationElf() {
        assertNull(resolveContainerAnnotationType(Component.class));
        assertEquals(ComponentScans.class, resolveContainerAnnotationType(ComponentScan.class));

        assertNotNull(findAnnotation(SuperInterface.class, EnvConfig.class));
        assertNotNull(findAnnotation(SuperInterface.class.getMethod("config"), EnvConfig.class));

        assertNull(findAnnotation(SubInterface.class, EnvConfig.class));
        assertNull(findAnnotation(SubInterface.class.getMethod("config"), EnvConfig.class));
    }

    @EnvConfig
    public interface SuperInterface {

        @EnvConfig
        String config();
    }

    public interface SubInterface extends SuperInterface {

        @Override
        String config();
    }
}
