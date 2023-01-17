package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.config.EnvConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.charlemaznable.core.spring.AnnotationElf.resolveContainerAnnotationType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

public class AnnotationElfTest {

    @SneakyThrows
    @Test
    public void testAnnotationElf() {
        assertNull(resolveContainerAnnotationType(Component.class));
        assertEquals(ComponentScans.class, resolveContainerAnnotationType(ComponentScan.class));

        assertTrue(isAnnotated(SuperInterface.class, EnvConfig.class));
        assertTrue(isAnnotated(SuperInterface.class.getMethod("config"), EnvConfig.class));

        assertFalse(isAnnotated(SubInterface.class, EnvConfig.class));
        assertFalse(isAnnotated(SubInterface.class.getMethod("config"), EnvConfig.class));

        assertTrue(isAnnotated(SuperInterface2.class, EnvConfig.class));
        assertTrue(isAnnotated(SuperInterface2.class.getMethod("config"), EnvConfig.class));

        assertFalse(isAnnotated(SubInterface2.class, EnvConfig.class));
        assertFalse(isAnnotated(SubInterface2.class.getMethod("config"), EnvConfig.class));
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

    @Documented
    @Inherited
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @EnvConfig
    public @interface MyEnvConfig {
    }

    @MyEnvConfig
    public interface SuperInterface2 {

        @MyEnvConfig
        String config();
    }

    public interface SubInterface2 extends SuperInterface2 {

        @Override
        String config();
    }
}
