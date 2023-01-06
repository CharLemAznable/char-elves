package com.github.charlemaznable.core.spring;

import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AnnotationElf {

    public static Class<? extends Annotation> resolveContainerAnnotationType(Class<? extends Annotation> annotationType) {
        val repeatable = AnnotationUtils.getAnnotation(annotationType, Repeatable.class);
        return nonNull(repeatable) ? repeatable.value() : null;
    }
}
