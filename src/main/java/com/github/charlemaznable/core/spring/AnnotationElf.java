package com.github.charlemaznable.core.spring;

import lombok.NoArgsConstructor;
import lombok.val;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class AnnotationElf {

    public static Class<? extends Annotation> resolveContainerAnnotationType(Class<? extends Annotation> annotationType) {
        val repeatable = AnnotationUtils.getAnnotation(annotationType, Repeatable.class);
        return nonNull(repeatable) ? repeatable.value() : null;
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clazz, @Nullable Class<A> annotationType) {
        return AnnotationUtils.findAnnotation((AnnotatedElement) clazz, annotationType);
    }

    public static <A extends Annotation> A findAnnotation(Method method, @Nullable Class<A> annotationType) {
        return AnnotationUtils.findAnnotation((AnnotatedElement) method, annotationType);
    }
}
