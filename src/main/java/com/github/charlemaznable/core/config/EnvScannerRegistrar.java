package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.spring.SpringFactoryBean;
import com.github.charlemaznable.core.spring.SpringScannerRegistrar;
import lombok.val;
import org.springframework.core.type.ClassMetadata;

import static com.github.charlemaznable.core.config.EnvFactory.getEnv;

public final class EnvScannerRegistrar extends SpringScannerRegistrar {

    public EnvScannerRegistrar() {
        super(EnvScan.class, EnvFactoryBean.class, EnvConfig.class);
    }

    @Override
    protected boolean isCandidateClass(ClassMetadata classMetadata) {
        return classMetadata.isInterface();
    }

    public static EnvFactoryBean buildFactoryBean(Class<?> xyzInterface) {
        val factoryBean = new EnvFactoryBean();
        factoryBean.setXyzInterface(xyzInterface);
        return factoryBean;
    }

    public static class EnvFactoryBean extends SpringFactoryBean {

        @Override
        public Object buildObject(Class<?> xyzInterface) {
            return getEnv(xyzInterface);
        }
    }
}
