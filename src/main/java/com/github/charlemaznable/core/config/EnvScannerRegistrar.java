package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.config.EnvFactory.EnvLoader;
import com.github.charlemaznable.core.spring.SpringFactoryBean;
import com.github.charlemaznable.core.spring.SpringScannerRegistrar;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.ClassMetadata;

import static com.github.charlemaznable.core.config.EnvFactory.springEnvLoader;

public final class EnvScannerRegistrar extends SpringScannerRegistrar {

    private final EnvLoader envLoader;

    public EnvScannerRegistrar() {
        super(EnvScan.class, EnvFactoryBean.class, EnvConfig.class);
        this.envLoader = springEnvLoader();
    }

    @Override
    protected boolean isCandidateClass(ClassMetadata classMetadata) {
        return classMetadata.isInterface();
    }

    @Override
    protected void postProcessBeanDefinition(BeanDefinition beanDefinition) {
        super.postProcessBeanDefinition(beanDefinition);
        beanDefinition.getPropertyValues().add("envLoader", envLoader);
    }

    public static class EnvFactoryBean extends SpringFactoryBean {

        @Setter
        private EnvLoader envLoader;

        @Override
        public Object buildObject(Class<?> xyzInterface) {
            return envLoader.getEnv(xyzInterface);
        }
    }
}
