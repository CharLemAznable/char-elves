package com.github.charlemaznable.core.testing.mockito;

import com.github.charlemaznable.core.lang.ClzPath;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Nonnull;

import static org.springframework.core.annotation.AnnotatedElementUtils.hasAnnotation;

@Slf4j
public class MockitoSpyProxyBeanPostProcessor implements BeanPostProcessor {

    private static final boolean HAS_MOCKITO;

    static {
        HAS_MOCKITO = ClzPath.classExists("org.mockito.Mockito");
    }

    @Override
    public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        val mockBean = (HAS_MOCKITO && hasAnnotation(bean.getClass(), MockitoSpyForTesting.class)
                && !MockUtil.isSpy(bean)) ? Mockito.spy(bean) : bean;
        return BeanPostProcessor.super.postProcessAfterInitialization(mockBean, beanName);
    }
}
