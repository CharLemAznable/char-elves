package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.config.EnvFactory.EnvLoader;
import com.github.charlemaznable.core.guice.CommonModular;
import com.google.inject.Module;
import com.google.inject.Provider;
import lombok.experimental.Delegate;

import static com.github.charlemaznable.core.config.EnvFactory.envLoader;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

public final class EnvModular extends CommonModular<EnvModular> {

    @Delegate
    private final EnvLoader envLoader;

    public EnvModular(Module... modules) {
        this(newArrayList(modules));
    }

    public EnvModular(Iterable<? extends Module> modules) {
        super(modules);
        this.envLoader = envLoader(guiceFactory);
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
