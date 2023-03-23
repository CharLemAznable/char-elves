package com.github.charlemaznable.core.vertx.spring;

import lombok.val;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.github.charlemaznable.core.lang.Condition.notNullThen;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional({ConditionalOnRx.OnRxCondition.class})
@interface ConditionalOnRx {

    Class<?> value();

    class OnRxCondition implements Condition {

        @Override
        public boolean matches(@NonNull ConditionContext context,
                               @NonNull AnnotatedTypeMetadata metadata) {
            val classLoader = getClassLoader(context);
            val rxClassName = getRxClassName(metadata);
            if (isNull(rxClassName)) return false;
            try {
                if (isNull(classLoader)) {
                    Class.forName(rxClassName);
                } else {
                    Class.forName(rxClassName, false, classLoader);
                }
                return true;
            } catch (Throwable e) {
                return false;
            }
        }

        private ClassLoader getClassLoader(ConditionContext context) {
            return nullThen(context.getClassLoader(), ClassUtils::getDefaultClassLoader);
        }

        private String getRxClassName(AnnotatedTypeMetadata metadata) {
            return (String) notNullThen(metadata.getAllAnnotationAttributes(
                            ConditionalOnRx.class.getName(), true),
                    attributes -> attributes.getFirst("value"));
        }
    }
}
