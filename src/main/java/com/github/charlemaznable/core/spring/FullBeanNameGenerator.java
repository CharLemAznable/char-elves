package com.github.charlemaznable.core.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

public final class FullBeanNameGenerator extends AnnotationBeanNameGenerator {

    public static String getBeanClassName(BeanDefinition definition) {
        return checkNotNull(definition.getBeanClassName());
    }

    @Nonnull
    @Override
    protected String buildDefaultBeanName(@Nonnull BeanDefinition definition) {
        return getBeanClassName(definition);
    }
}
