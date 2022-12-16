package com.github.charlemaznable.core.context;

import com.github.charlemaznable.core.lang.Factory;
import com.github.charlemaznable.core.spring.SpringFactory;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.charlemaznable.core.lang.Clz.isConcrete;
import static lombok.AccessLevel.PRIVATE;
import static org.joor.Reflect.onClass;

@NoArgsConstructor(access = PRIVATE)
public final class FactoryContext {

    private static final ThreadLocal<Factory> local =
            new InheritableThreadLocal<>() {
                @Override
                protected Factory initialValue() {
                    return SpringFactory.getInstance();
                }
            };

    public static void set(Factory factory) {
        local.set(factory);
    }

    public static Factory get() {
        return local.get();
    }

    public static void unload() {
        local.remove();
    }

    public static <T> T build(Factory factory, Class<T> clazz) {
        val temp = local.get();
        local.set(factory);
        try {
            return factory.build(clazz);
        } finally {
            local.set(temp);
        }
    }

    public static <T> void accept(Factory factory, Class<T> clazz,
                                  Consumer<T> consumer) {
        val temp = local.get();
        local.set(factory);
        try {
            consumer.accept(factory.build(clazz));
        } finally {
            local.set(temp);
        }
    }

    public static <T, R> R apply(Factory factory, Class<T> clazz,
                                 Function<T, R> function) {
        val temp = local.get();
        local.set(factory);
        try {
            return function.apply(factory.build(clazz));
        } finally {
            local.set(temp);
        }
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class ReflectFactory implements Factory {

        public static ReflectFactory getInstance() {
            return ReflectFactoryHolder.instance;
        }

        public static ReflectFactory reflectFactory() {
            return getInstance();
        }

        @Override
        public <T> T build(Class<T> clazz) {
            if (!isConcrete(clazz)) return null;
            return onClass(clazz).create().get();
        }

        private static class ReflectFactoryHolder {

            private static final ReflectFactory instance = new ReflectFactory();
        }
    }
}
