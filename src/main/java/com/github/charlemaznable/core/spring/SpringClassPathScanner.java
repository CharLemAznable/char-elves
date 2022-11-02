package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.lang.ClzPath;
import lombok.val;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class SpringClassPathScanner extends ClassPathBeanDefinitionScanner {

    private final Class factoryBeanClass;
    private final Predicate<ClassMetadata> isCandidateClass;
    private final Predicate<Class> isPrimaryCandidate;
    private final Class<? extends Annotation>[] annotationClasses;
    private final SpringClassPathMethodBeanLoader methodBeanLoader;

    @SafeVarargs
    public SpringClassPathScanner(BeanDefinitionRegistry registry,
                                  Class factoryBeanClass,
                                  Predicate<ClassMetadata> isCandidateClass,
                                  Predicate<Class> isPrimaryCandidate,
                                  Class<? extends Annotation>... annotationClasses) {
        super(registry, false);
        this.factoryBeanClass = factoryBeanClass;
        this.isCandidateClass = isCandidateClass;
        this.isPrimaryCandidate = isPrimaryCandidate;
        this.annotationClasses = annotationClasses;
        this.methodBeanLoader = new SpringClassPathMethodBeanLoader(
                getRegistry(), getEnvironment(), getResourceLoader(), logger);
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
            logger.warn(String.format("No %s was found in [%s] package. Please check your configuration.",
                    factoryBeanClass.getSimpleName(), Arrays.toString(basePackages)));
        } else {
            for (val holder : beanDefinitions) {
                val beanName = holder.getBeanName();
                val beanDefinition = (AbstractBeanDefinition) holder.getBeanDefinition();
                val beanClassName = checkNotNull(beanDefinition.getBeanClassName());
                val beanClass = checkNotNull(ClzPath.findClass(beanClassName));

                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Creating %s with name '%s' and '%s' xyzInterface",
                            factoryBeanClass.getSimpleName(), beanName, beanClassName));
                }

                // the mapper interface is the original class of the bean
                // but, the actual class of the bean is MapperFactoryBean
                beanDefinition.getPropertyValues().add("xyzInterface", beanClassName);
                beanDefinition.setBeanClass(factoryBeanClass);
                beanDefinition.setPrimary(isPrimaryCandidate(beanClass));

                methodBeanLoader.loadBeanMethodMetadataSet(beanDefinition, beanClassName, beanClass)
                        .forEach(methodMetadata ->
                                methodBeanLoader.loadBeanDefinitionForBeanMethod(methodMetadata, beanName));
            }
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
            logger.warn(String.format("Skipping %s with name '%s' and '%s' interface. " +
                            "Bean already defined with the same name!",
                    factoryBeanClass.getSimpleName(), beanName, beanDefinition.getBeanClassName()));
            return false;
        }
    }

    protected boolean isPrimaryCandidate(Class beanClass) {
        return nonNull(isPrimaryCandidate) && isPrimaryCandidate.test(beanClass);
    }
}
