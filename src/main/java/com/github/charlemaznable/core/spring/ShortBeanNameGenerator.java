package com.github.charlemaznable.core.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;

public class ShortBeanNameGenerator extends AnnotationBeanNameGenerator {

    public static String getBeanClassName(BeanDefinition definition) {
        return ClassUtils.getShortName(checkNotNull(definition.getBeanClassName()));
    }

    @Nonnull
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return getBeanClassName(definition);
    }
}
