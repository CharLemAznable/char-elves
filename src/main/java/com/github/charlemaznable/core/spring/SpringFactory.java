package com.github.charlemaznable.core.spring;

import com.github.charlemaznable.core.lang.Factory;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class SpringFactory implements Factory {

    public static SpringFactory getInstance() {
        return SpringFactoryHolder.instance;
    }

    public static SpringFactory springFactory() {
        return getInstance();
    }

    @Override
    public <T> T build(Class<T> clazz) {
        return SpringContext.getBeanOrCreate(clazz);
    }

    private static class SpringFactoryHolder {

        private static SpringFactory instance = new SpringFactory();
    }
}
