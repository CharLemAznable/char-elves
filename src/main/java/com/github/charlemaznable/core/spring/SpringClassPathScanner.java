package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.lang.ClzPath;
import com.google.common.collect.Sets;
import lombok.val;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Objects.isNull;
import static org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotation;

public final class SpringClassPathScanner extends ClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;
    private final Class factoryBeanClass;
    private final Predicate<ClassMetadata> isCandidateClass;
    private final Class<? extends Annotation>[] annotationClasses;

    @SafeVarargs
    public SpringClassPathScanner(BeanDefinitionRegistry registry,
                                  Class factoryBeanClass,
                                  Predicate<ClassMetadata> isCandidateClass,
                                  Class<? extends Annotation>... annotationClasses) {
        super(registry, false);
        this.registry = registry;
        this.factoryBeanClass = factoryBeanClass;
        this.isCandidateClass = isCandidateClass;
        this.annotationClasses = annotationClasses;
    }

    public void registerFilters() {
        for (val annotationClass : annotationClasses) {
            addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        }
    }

    @Nonnull
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        val beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn("No " + factoryBeanClass.getSimpleName() + " was found in '"
                    + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            val beanMethodDefinitions = Sets.<BeanDefinitionHolder>newHashSet();
            for (val holder : beanDefinitions) {
                val beanName = holder.getBeanName();
                val beanDefinition = (GenericBeanDefinition) holder.getBeanDefinition();
                val beanClassName = beanDefinition.getBeanClassName();
                val beanClass = ClzPath.findClass(beanClassName);

                if (logger.isDebugEnabled()) {
                    logger.debug("Creating " + factoryBeanClass.getSimpleName() + " with name '"
                            + holder.getBeanName() + "' and '" + beanClassName + "' xyzInterface");
                }

                // the mapper interface is the original class of the bean
                // but, the actual class of the bean is MapperFactoryBean
                beanDefinition.getPropertyValues().add("xyzInterface", beanClassName);
                beanDefinition.setBeanClass(factoryBeanClass);
                beanMethodDefinitions.addAll(scanBeanMethod(beanClass, beanName));
            }
            beanDefinitions.addAll(beanMethodDefinitions);
        }

        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return isNull(isCandidateClass) || isCandidateClass.test(beanDefinition.getMetadata());
    }

    @Override
    protected boolean checkCandidate(@Nonnull String beanName, @Nonnull BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping " + factoryBeanClass.getSimpleName() + " with name '" + beanName
                    + "' and '" + beanDefinition.getBeanClassName() + "' interface"
                    + ". Bean already defined with the same name!");
            return false;
        }
    }

    private Set<BeanDefinitionHolder> scanBeanMethod(Class beanClass, String beanName) {
        val beanMethodDefinitions = Sets.<BeanDefinitionHolder>newHashSet();
        if (isNull(beanClass)) return beanMethodDefinitions;

        val beanMethods = beanClass.getDeclaredMethods();
        for (Method beanMethod : beanMethods) {
            val beanMethodAnno = getMergedAnnotation(beanMethod, Bean.class);
            if (isNull(beanMethodAnno)) continue;

            val beanMethodName = beanMethod.getName();
            val defaultName = beanClass.getName() + "." + beanMethodName;
            val beanMethodAnnoName = beanMethodAnno.name();
            val names = new ArrayList<>(Arrays.asList(beanMethodAnnoName));
            val beanMethodBeanName = (!names.isEmpty() ? names.remove(0) : defaultName);
            for (String alias : names) {
                registry.registerAlias(beanMethodBeanName, alias);
            }
            val beanMethodDefinition = new RootBeanDefinition();
            beanMethodDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            beanMethodDefinition.setFactoryBeanName(beanName);
            beanMethodDefinition.setUniqueFactoryMethodName(beanMethodName);
            registry.registerBeanDefinition(beanMethodBeanName, beanMethodDefinition);

            if (logger.isTraceEnabled()) {
                logger.trace(String.format("Registering bean definition for @Bean method %s.%s()",
                        beanClass.getCanonicalName(), beanMethodBeanName));
            }

            beanMethodDefinitions.add(new BeanDefinitionHolder(
                    beanMethodDefinition, beanMethodBeanName));
        }
        return beanMethodDefinitions;
    }
}
