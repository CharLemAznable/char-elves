package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.spring.SpringFactoryBean;
import com.github.charlemaznable.core.spring.SpringScannerRegistrar;
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

    public static class EnvFactoryBean extends SpringFactoryBean {

        @Override
        public Object buildObject(Class<?> xyzInterface) {
            return getEnv(xyzInterface);
        }
    }
}
