package com.github.charlemaznable.core.spring;

import lombok.val;
import org.apache.commons.logging.Log;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListenerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.github.charlemaznable.core.lang.Condition.notNullThen;
import static com.github.charlemaznable.core.lang.Condition.notNullThenRun;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;
import static org.joor.Reflect.onClass;
import static org.springframework.beans.factory.annotation.RequiredAnnotationBeanPostProcessor.SKIP_REQUIRED_CHECK_ATTRIBUTE;

final class SpringClassPathMethodBeanLoader {

    private static final String CONDITION_CONTEXT_IMPL =
            "org.springframework.context.annotation.ConditionEvaluator$ConditionContextImpl";
    private static final String VALUE = "value";

    private final BeanDefinitionRegistry registry;
    private final ConditionContext conditionContext;
    private final Log logger;

    SpringClassPathMethodBeanLoader(BeanDefinitionRegistry registry,
                                    Environment environment,
                                    ResourceLoader resourceLoader, Log logger) {
        this.registry = registry;
        this.conditionContext = onClass(CONDITION_CONTEXT_IMPL)
                .create(registry, environment, resourceLoader).get();
        this.logger = logger;
    }

    Set<MethodMetadata> loadBeanMethodMetadataSet(AbstractBeanDefinition beanDefinition,
                                                  String beanClassName, Class<?> beanClass) {
        AnnotationMetadata metadata;
        if (beanDefinition instanceof AnnotatedBeanDefinition &&
                beanClassName.equals(((AnnotatedBeanDefinition) beanDefinition).getMetadata().getClassName())) {
            metadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
        } else {
            if (BeanFactoryPostProcessor.class.isAssignableFrom(beanClass) ||
                    BeanPostProcessor.class.isAssignableFrom(beanClass) ||
                    AopInfrastructureBean.class.isAssignableFrom(beanClass) ||
                    EventListenerFactory.class.isAssignableFrom(beanClass)) {
                return Collections.emptySet();
            }
            metadata = AnnotationMetadata.introspect(beanClass);
        }
        return metadata.getAnnotatedMethods(Bean.class.getName());
    }

    void loadBeanDefinitionForBeanMethod(MethodMetadata methodMetadata, String beanName) {
        if (conditionShouldSkip(methodMetadata)) return;
        val beanAnnoAttrs = attributesFor(methodMetadata, Bean.class);
        if (isNull(beanAnnoAttrs)) return;

        val beanMethodName = methodMetadata.getMethodName();
        val defaultName = beanName + "." + beanMethodName;
        val names = new ArrayList<>(Arrays.asList(beanAnnoAttrs.getStringArray("name")));
        val beanMethodBeanName = (!names.isEmpty() ? names.remove(0) : defaultName);
        for (String alias : names) {
            registry.registerAlias(beanMethodBeanName, alias);
        }
        if (isOverriddenByExistingDefinition(methodMetadata, beanMethodBeanName)) return;

        val beanMethodDefinition = new RootBeanDefinition();
        beanMethodDefinition.setFactoryBeanName(beanName);
        beanMethodDefinition.setUniqueFactoryMethodName(beanMethodName);
        if (methodMetadata instanceof StandardMethodMetadata) {
            beanMethodDefinition.setResolvedFactoryMethod(
                    ((StandardMethodMetadata) methodMetadata).getIntrospectedMethod());
        }
        beanMethodDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        beanMethodDefinition.setAttribute(SKIP_REQUIRED_CHECK_ATTRIBUTE, Boolean.TRUE);
        processCommonDefinitionAnnotations(beanMethodDefinition, methodMetadata);
        processCommonAnnotationAttributes(beanMethodDefinition, beanAnnoAttrs);
        val beanDefinitionToRegister = processScopeProxy(
                beanMethodDefinition, methodMetadata, beanMethodBeanName);

        registry.registerBeanDefinition(beanMethodBeanName, beanDefinitionToRegister);
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("Registering bean definition for @Bean %s, method %s.%s()",
                    beanMethodBeanName, beanName, beanMethodName));
        }
    }

    private boolean conditionShouldSkip(MethodMetadata methodMetadata) {
        if (!methodMetadata.isAnnotated(Conditional.class.getName())) return false;

        val conditions = new ArrayList<Condition>();
        getConditionClasses(methodMetadata).forEach(conditionClasses ->
                Arrays.stream(conditionClasses).forEach(conditionClass ->
                        conditions.add(getCondition(conditionClass, conditionContext.getClassLoader()))));
        AnnotationAwareOrderComparator.sort(conditions);

        for (Condition condition : conditions) {
            ConfigurationPhase requiredPhase = null;
            if (condition instanceof ConfigurationCondition) {
                requiredPhase = ((ConfigurationCondition) condition).getConfigurationPhase();
            }
            if ((requiredPhase == null || requiredPhase == ConfigurationPhase.REGISTER_BEAN)
                    && !condition.matches(conditionContext, methodMetadata)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static List<String[]> getConditionClasses(MethodMetadata methodMetadata) {
        val attributes = methodMetadata.getAllAnnotationAttributes(Conditional.class.getName(), true);
        return nullThen((List<String[]>) notNullThen(attributes,
                m -> (Object) m.get(VALUE)), Collections::emptyList);
    }

    private static Condition getCondition(String conditionClassName, ClassLoader classloader) {
        val conditionClass = ClassUtils.resolveClassName(conditionClassName, classloader);
        return (Condition) BeanUtils.instantiateClass(conditionClass);
    }

    @Nullable
    private static AnnotationAttributes attributesFor(AnnotatedTypeMetadata metadata, Class<?> annotationClass) {
        return AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(annotationClass.getName()));
    }

    private boolean isOverriddenByExistingDefinition(MethodMetadata methodMetadata, String beanName) {
        if (!registry.containsBeanDefinition(beanName)) return false;
        val existingBeanDef = registry.getBeanDefinition(beanName);
        // A bean definition resulting from a component scan can be silently overridden
        // by an @Bean method, as of 4.2...
        if (existingBeanDef instanceof ScannedGenericBeanDefinition) return false;
        // Has the existing bean definition bean marked as a framework-generated bean?
        // -> allow the current bean method to override it, since it is application-level
        if (existingBeanDef.getRole() > BeanDefinition.ROLE_APPLICATION) return false;
        // At this point, it's a top-level override (probably XML), just having been parsed
        // before configuration class processing kicks in...
        if (registry instanceof DefaultListableBeanFactory &&
                !((DefaultListableBeanFactory) registry).isAllowBeanDefinitionOverriding()) {
            throw new BeanDefinitionStoreException(methodMetadata.getDeclaringClassName(),
                    beanName, "@Bean definition illegally overridden by existing bean definition: " + existingBeanDef);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Skipping bean definition for BeanMethod %s: a definition for bean '%s' " +
                            "already exists. This top-level bean definition is considered as an override.",
                    methodMetadata, beanName));
        }
        return true;
    }

    private static void processCommonDefinitionAnnotations(AbstractBeanDefinition beanDefinition,
                                                           MethodMetadata methodMetadata) {
        notNullThenRun(attributesFor(methodMetadata, Lazy.class), lazy ->
                beanDefinition.setLazyInit(lazy.getBoolean(VALUE)));
        if (methodMetadata.isAnnotated(Primary.class.getName())) beanDefinition.setPrimary(true);
        notNullThenRun(attributesFor(methodMetadata, DependsOn.class), dependsOn ->
                beanDefinition.setDependsOn(dependsOn.getStringArray(VALUE)));
        notNullThenRun(attributesFor(methodMetadata, Role.class), role ->
                beanDefinition.setRole(role.getNumber(VALUE).intValue()));
        notNullThenRun(attributesFor(methodMetadata, Description.class), description ->
                beanDefinition.setDescription(description.getString(VALUE)));
    }

    private static void processCommonAnnotationAttributes(AbstractBeanDefinition beanDefinition,
                                                          AnnotationAttributes beanAnnoAttrs) {
        Autowire autowire = beanAnnoAttrs.getEnum("autowire");
        if (autowire.isAutowire()) beanDefinition.setAutowireMode(autowire.value());
        boolean autowireCandidate = beanAnnoAttrs.getBoolean("autowireCandidate");
        if (!autowireCandidate) beanDefinition.setAutowireCandidate(false);
        String initMethodName = beanAnnoAttrs.getString("initMethod");
        if (StringUtils.hasText(initMethodName)) beanDefinition.setInitMethodName(initMethodName);
        String destroyMethodName = beanAnnoAttrs.getString("destroyMethod");
        beanDefinition.setDestroyMethodName(destroyMethodName);
    }

    private BeanDefinition processScopeProxy(AbstractBeanDefinition beanDefinition,
                                             MethodMetadata methodMetadata,
                                             String beanMethodBeanName) {
        ScopedProxyMode proxyMode = ScopedProxyMode.NO;
        val attributes = attributesFor(methodMetadata, Scope.class);
        if (attributes != null) {
            beanDefinition.setScope(attributes.getString(VALUE));
            proxyMode = attributes.getEnum("proxyMode");
            if (proxyMode == ScopedProxyMode.DEFAULT) {
                proxyMode = ScopedProxyMode.NO;
            }
        }
        BeanDefinition beanDefToRegister = beanDefinition;
        if (proxyMode != ScopedProxyMode.NO) {
            val proxyDef = ScopedProxyUtils.createScopedProxy(
                    new BeanDefinitionHolder(beanDefinition, beanMethodBeanName),
                    registry, proxyMode == ScopedProxyMode.TARGET_CLASS);
            beanDefToRegister = new RootBeanDefinition(
                    (RootBeanDefinition) proxyDef.getBeanDefinition());
        }
        return beanDefToRegister;
    }
}
