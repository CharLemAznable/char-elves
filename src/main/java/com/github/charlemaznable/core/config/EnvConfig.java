package com.github.charlemaznable.core.config;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvConfig {

    /**
     * effective when annotated on ElementType.METHOD
     */
    @AliasFor("value")
    String configKey() default "";

    /**
     * effective when annotated on ElementType.METHOD
     */
    @AliasFor("configKey")
    String value() default "";

    /**
     * effective when annotated on ElementType.METHOD
     */
    String defaultValue() default "";
}
