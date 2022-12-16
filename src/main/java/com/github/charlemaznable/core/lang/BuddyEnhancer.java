package com.github.charlemaznable.core.lang;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.github.charlemaznable.core.lang.Clz.match;
import static com.github.charlemaznable.core.lang.Clz.types;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static java.lang.ClassLoader.getSystemClassLoader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuddyEnhancer {

    @FunctionalInterface
    public interface DelegateFilter {

        int accept(Method method);
    }

    @FunctionalInterface
    public interface Delegate {

        Object invoke(Method method, Object[] args, Callable<Object> superCall) throws Exception;
    }

    public static final Delegate CallSuper = (m, a, superCall) -> superCall.call();

    @AllArgsConstructor
    public static final class DelegateHandler {

        private static final Map<Class<?>, Object> primitiveDefault = Map.of(
                boolean.class, false, short.class, (short) 0, int.class, 0,
                long.class, 0L, float.class, 0.0f, double.class, 0.0d,
                byte.class, (byte) 0, char.class, '\u0000'
        );

        private DelegateFilter filter;
        private Delegate[] delegates;

        @RuntimeType
        public Object invoke(@Origin Method method, @AllArguments Object[] args,
                             @SuperCall(nullIfImpossible = true) Callable<Object> superCall) throws Exception {
            int index = filter.accept(method);
            if (index >= delegates.length) throw new IllegalArgumentException(
                    "Handler filter returned an index that is too large: " + index);
            return Optional.ofNullable(checkNotNull(delegates[index]).invoke(method, args, superCall))
                    .orElseGet(() -> primitiveDefault.get(method.getReturnType()));
        }
    }

    public static Object create(Class<?> type, Delegate delegate) {
        return newInstance(newType(type, delegate));
    }

    public static Object create(Class<?> type, Delegate delegate, Object[] arguments) {
        return newInstance(newType(type, delegate), arguments);
    }

    public static Object create(Class<?> superclass, Class<?>[] interfaces,
                                Delegate delegate) {
        return newInstance(newType(superclass, interfaces, delegate));
    }

    public static Object create(Class<?> superclass, Class<?>[] interfaces,
                                Delegate delegate, Object[] arguments) {
        return newInstance(newType(superclass, interfaces, delegate), arguments);
    }

    public static Object create(Class<?> superclass, Class<?>[] interfaces,
                                DelegateFilter filter, Delegate[] delegates) {
        return newInstance(newType(superclass, interfaces, filter, delegates));
    }

    public static Object create(Class<?> superclass, Class<?>[] interfaces,
                                DelegateFilter filter, Delegate[] delegates, Object[] arguments) {
        return newInstance(newType(superclass, interfaces, filter, delegates), arguments);
    }

    ////////////////////////////////////////////////////////////

    private static final Class<?>[] EMPTY_CLASS_ARRAY = {};

    private static Class<?> newType(Class<?> type, Delegate delegate) {
        return newType(type, EMPTY_CLASS_ARRAY, delegate);
    }

    private static Class<?> newType(Class<?> superclass, Class<?>[] interfaces, Delegate delegate) {
        return newType(superclass, interfaces, m -> 0, new Delegate[]{delegate});
    }

    private static Class<?> newType(Class<?> superclass, Class<?>[] interfaces,
                                    DelegateFilter filter, Delegate[] delegates) {
        return new ByteBuddy().subclass(superclass).implement(interfaces)
                .method(ElementMatchers.not(ElementMatchers.isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .filter(ElementMatchers.isDeclaredBy(DelegateHandler.class))
                        .to(new DelegateHandler(filter, delegates)))
                .make().load(getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
    }

    private static Object newInstance(Class<?> type) {
        return newInstance(type, EMPTY_CLASS_ARRAY, null);
    }

    private static Object newInstance(Class<?> type, Object[] parameters) {
        return newInstance(type, types(parameters), parameters);
    }

    private static Object newInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {
        return newInstance(getConstructor(type, parameterTypes), parameters);
    }

    @SuppressWarnings("deprecation")
    @SneakyThrows
    private static Object newInstance(final Constructor<?> constructor, final Object[] parameters) {
        boolean flag = constructor.isAccessible();
        try {
            if (!flag) constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        } finally {
            if (!flag) constructor.setAccessible(false);
        }
    }

    private static Constructor<?> getConstructor(Class<?> type, Class<?>[] parameterTypes) {
        try {
            val constructor = type.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            for (val constructor : type.getDeclaredConstructors()) {
                val types = constructor.getParameterTypes();
                if (match(types, parameterTypes)) {
                    constructor.setAccessible(true);
                    return constructor;
                }
            }
            throw new IllegalArgumentException(type.getSuperclass()
                    + "'s Constructor with such arguments Not Found");
        }
    }
}
