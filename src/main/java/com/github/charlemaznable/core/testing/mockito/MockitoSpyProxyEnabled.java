package com.github.charlemaznable.core.testing.mockito;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(MockitoSpyProxyBeanPostProcessor.class)
public @interface MockitoSpyProxyEnabled {
}
