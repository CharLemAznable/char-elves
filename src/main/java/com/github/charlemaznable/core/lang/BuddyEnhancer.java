package com.github.charlemaznable.core.lang;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.LoadedTypeInitializer.Compound;
import net.bytebuddy.implementation.LoadedTypeInitializer.ForStaticField;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.StubValue;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.Callable;

import static com.github.charlemaznable.core.lang.Clz.match;
import static com.github.charlemaznable.core.lang.Clz.types;
import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Listt.newArrayList;
import static java.lang.ClassLoader.getSystemClassLoader;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.not;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BuddyEnhancer {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = {};
    public static final DelegateFilter ALL_ZERO = invocation -> 0;
    public static final Delegate CALL_SUPER
            = invocation -> invocation.getSuperCall().call();

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Invocation {

        private Object thiz;
        private Object zuper;
        @Getter
        private Method method;
        @Getter
        private Object[] arguments;
        @Getter
        private Callable<Object> superCall;
        @Getter
        private Method superMethod;
        @Getter
        private Object stubValue;

        public Object getThis() {
            return thiz;
        }

        public Object getSuper() {
            return zuper;
        }
    }

    public interface Delegate {

        Object invoke(Invocation invocation) throws Exception;
    }

    @FunctionalInterface
    public interface DelegateFilter {

        int accept(Invocation invocation);
    }

    @AllArgsConstructor
    public static final class DelegateHandler {

        private DelegateFilter filter;
        private Delegate[] delegates;

        @RuntimeType
        public Object invoke(@This(optional = true) Object thiz,
                             @Super Object zuper,
                             @Origin Method method,
                             @AllArguments Object[] arguments,
                             @SuperCall(nullIfImpossible = true) Callable<Object> superCall,
                             @SuperMethod(nullIfImpossible = true) Method superMethod,
                             @StubValue Object stubValue) throws Exception {
            val invocation = new Invocation(thiz, zuper,
                    method, arguments, superCall, superMethod, stubValue);
            int index = filter.accept(invocation);
            if (index >= delegates.length) throw new IllegalArgumentException(
                    "Handler filter returned an index that is too large: " + index);
            val delegate = checkNotNull(delegates[index]);
            return Optional.ofNullable(delegate.invoke(invocation)).orElse(stubValue);
        }
    }

    ////////////////////////////////////////////////////////////

    public static Object create(Class<?> type,
                                Delegate delegate) {
        return newInstance(newType(type, delegate));
    }

    public static Object create(Class<?> type,
                                Object[] arguments,
                                Delegate delegate) {
        return newInstance(newType(type, delegate), arguments);
    }

    public static Object create(Class<?> superclass,
                                Class<?>[] interfaces,
                                Delegate delegate) {
        return newInstance(newType(superclass, interfaces, delegate));
    }

    public static Object create(Class<?> superclass,
                                Object[] arguments,
                                Class<?>[] interfaces,
                                Delegate delegate) {
        return newInstance(newType(superclass, interfaces, delegate), arguments);
    }

    public static Object create(Class<?> superclass,
                                Class<?>[] interfaces,
                                DelegateFilter filter,
                                Delegate[] delegates) {
        return newInstance(newType(superclass, interfaces, filter, delegates));
    }

    public static Object create(Class<?> superclass,
                                Object[] arguments,
                                Class<?>[] interfaces,
                                DelegateFilter filter,
                                Delegate[] delegates) {
        return newInstance(newType(superclass, interfaces, filter, delegates), arguments);
    }

    ////////////////////////////////////////////////////////////

    private static final String DELEGATE_HANDLER_FIELD = "BUDDY$DELEGATE_HANDLER";
    private static final String DELEGATE_FIELDS_PREFIX = "BUDDY$DELEGATE_";

    ////////////////////////////////////////////////////////////

    public static Class<?> newType(Class<?> type, Delegate delegate) {
        return newType(type, EMPTY_CLASS_ARRAY, delegate);
    }

    public static Class<?> newType(Class<?> superclass, Class<?>[] interfaces, Delegate delegate) {
        return newType(superclass, interfaces, ALL_ZERO, new Delegate[]{delegate});
    }

    public static Class<?> newType(Class<?> superclass, Class<?>[] interfaces,
                                    DelegateFilter filter, Delegate[] delegates) {
        DynamicType.Builder<?> builder = new ByteBuddy()
                .subclass(superclass).implement(interfaces)
                .method(not(isDeclaredBy(Object.class)))
                .intercept(MethodDelegation.withDefaultConfiguration()
                        .filter(isDeclaredBy(DelegateHandler.class))
                        .toField(DELEGATE_HANDLER_FIELD))
                .defineField(DELEGATE_HANDLER_FIELD,
                        DelegateHandler.class, Visibility.PRIVATE, Ownership.STATIC);
        val initializers = newArrayList(new ForStaticField(
                DELEGATE_HANDLER_FIELD, new DelegateHandler(filter, delegates)));
        for (int i = 0; i < delegates.length; i++) {
            val fieldName = DELEGATE_FIELDS_PREFIX + i;
            builder = builder.defineField(fieldName,
                    Delegate.class, Visibility.PRIVATE, Ownership.STATIC);
            initializers.add(new ForStaticField(fieldName, delegates[i]));
        }
        return builder.initializer(new Compound(initializers)).make()
                .load(getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();
    }

    ////////////////////////////////////////////////////////////

    public static Object newInstance(Class<?> type) {
        return newInstance(type, EMPTY_CLASS_ARRAY, null);
    }

    public static Object newInstance(Class<?> type, Object[] parameters) {
        return newInstance(type, types(parameters), parameters);
    }

    public static Object newInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) {
        return newInstance(getConstructor(type, parameterTypes), parameters);
    }

    ////////////////////////////////////////////////////////////

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
