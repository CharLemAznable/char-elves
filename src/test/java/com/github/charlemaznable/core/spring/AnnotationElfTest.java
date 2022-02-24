package com.github.charlemaznable.core.spring;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import static com.github.charlemaznable.core.spring.AnnotationElf.resolveContainerAnnotationType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AnnotationElfTest {

    @Test
    public void testAnnotationElf() {
        assertNull(resolveContainerAnnotationType(Component.class));
        assertEquals(ComponentScans.class, resolveContainerAnnotationType(ComponentScan.class));
    }
}
