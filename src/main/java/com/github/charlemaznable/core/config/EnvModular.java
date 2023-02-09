package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.guice.CommonModular;
import com.google.inject.Module;
import com.google.inject.Provider;

import static com.github.charlemaznable.core.config.EnvFactory.getEnv;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

public final class EnvModular extends CommonModular<EnvModular> {

    public EnvModular(Module... modules) {
        this(newArrayList(modules));
    }

    public EnvModular(Iterable<? extends Module> modules) {
        super(modules);
    }

    @Override
    public boolean isCandidateClass(Class<?> clazz) {
        return isAnnotated(clazz, EnvConfig.class);
    }

    @Override
    public <T> Provider<T> createProvider(Class<T> clazz) {
        return () -> getEnv(clazz);
    }
}
