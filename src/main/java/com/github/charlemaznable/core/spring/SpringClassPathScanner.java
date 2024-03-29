package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.lang.ClzPath;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.notNullThenRun;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.springframework.context.annotation.TypeFilterUtils.createTypeFiltersFor;

public class SpringClassPathScanner extends ClassPathBeanDefinitionScanner {

    private final Class<?> factoryBeanClass;
    private final Predicate<ClassMetadata> isCandidateClass;
    private final Predicate<Class<?>> isPrimaryCandidate;
    private final Consumer<BeanDefinition> beanDefinitionPostProcessor;
    private final Class<? extends Annotation>[] annotationClasses;
    private final SpringClassPathMethodBeanLoader methodBeanLoader;

    @SafeVarargs
    public SpringClassPathScanner(BeanDefinitionRegistry registry,
                                  Class<?> factoryBeanClass,
                                  Predicate<ClassMetadata> isCandidateClass,
                                  Predicate<Class<?>> isPrimaryCandidate,
                                  Consumer<BeanDefinition> beanDefinitionPostProcessor,
                                  Class<? extends Annotation>... annotationClasses) {
        super(registry, false);
        this.factoryBeanClass = factoryBeanClass;
        this.isCandidateClass = isCandidateClass;
        this.isPrimaryCandidate = isPrimaryCandidate;
        this.beanDefinitionPostProcessor = beanDefinitionPostProcessor;
        this.annotationClasses = annotationClasses;
        this.methodBeanLoader = new SpringClassPathMethodBeanLoader(
                getRegistry(), getEnvironment(), getResourceLoader(), logger);
    }

    public void registerFilters(AnnotationAttributes[] includeFilterAttributes,
                                AnnotationAttributes[] excludeFilterAttributes) {
        List<TypeFilter> includeFilters = newArrayList();
        for (val includeFilterAttribute : includeFilterAttributes) {
            includeFilters.addAll(createTypeFiltersFor(includeFilterAttribute,
                    this.getEnvironment(), this.getResourceLoader(), requireNonNull(this.getRegistry())));
        }
        if (includeFilters.isEmpty()) includeFilters.add((metadataReader, metadataReaderFactory) -> true);
        for (TypeFilter includeFilter : includeFilters) {
            for (val annotationClass : annotationClasses) {
                addIncludeFilter(new CombineTypeFilter(
                        new AnnotationTypeFilter(annotationClass), includeFilter));
            }
        }

        for (val excludeFilterAttribute : excludeFilterAttributes) {
            val typeFilters = createTypeFiltersFor(excludeFilterAttribute,
                    this.getEnvironment(), this.getResourceLoader(), requireNonNull(this.getRegistry()));
            for (val typeFilter : typeFilters) {
                addExcludeFilter(typeFilter);
            }
        }
    }

    @Nonnull
    @Override
    public Set<BeanDefinitionHolder> doScan(@Nonnull String... basePackages) {
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
                    logger.debug(String.format("Creating %s with name '%s' and beanClassName '%s'",
                            factoryBeanClass.getSimpleName(), beanName, beanClassName));
                }

                beanDefinition.setPrimary(isPrimaryCandidate(beanClass));
                postProcessBeanDefinition(beanDefinition);
                beanDefinition.setBeanClass(factoryBeanClass);

                methodBeanLoader.loadBeanMethodMetadataSet(beanDefinition, beanClassName, beanClass)
                        .forEach(methodMetadata ->
                                methodBeanLoader.loadBeanDefinitionForBeanMethod(methodMetadata, beanName));
            }
        }
        return beanDefinitions;
    }

    @Override
    protected boolean isCandidateComponent(@Nonnull AnnotatedBeanDefinition beanDefinition) {
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

    protected boolean isPrimaryCandidate(Class<?> beanClass) {
        return nonNull(isPrimaryCandidate) && isPrimaryCandidate.test(beanClass);
    }

    protected void postProcessBeanDefinition(BeanDefinition beanDefinition) {
        notNullThenRun(beanDefinitionPostProcessor, processor -> processor.accept(beanDefinition));
    }

    @AllArgsConstructor
    private static class CombineTypeFilter implements TypeFilter {

        private AnnotationTypeFilter annotationTypeFilter;
        private TypeFilter customTypeFilter;

        @Override
        public boolean match(@Nonnull MetadataReader metadataReader,
                             @Nonnull MetadataReaderFactory metadataReaderFactory) throws IOException {
            return annotationTypeFilter.match(metadataReader, metadataReaderFactory) &&
                    customTypeFilter.match(metadataReader, metadataReaderFactory);
        }
    }
}
