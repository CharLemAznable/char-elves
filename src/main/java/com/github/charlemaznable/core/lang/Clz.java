package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Condition.checkNull;
import static java.lang.reflect.Modifier.isAbstract;
import static java.util.Objects.isNull;
import static lombok.AccessLevel.PRIVATE;
import static org.joor.Reflect.wrapper;

@NoArgsConstructor(access = PRIVATE)
public final class Clz {

    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (val toClass : toClasses)
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;

        return false;
    }

    public static boolean isConcrete(Class<?> clazz) {
        return !clazz.isInterface() && !isAbstract(clazz.getModifiers());
    }

    /**
     * Get method.
     *
     * @param clazz      class
     * @param methodName method name
     * @return method
     */
    @SneakyThrows
    public static Method getMethod(Class<?> clazz, String methodName) {
        return clazz.getMethod(methodName);
    }

    /**
     * 安静的调用对象的方法。
     *
     * @param target 对象
     * @param method 方法
     * @param args   参数
     * @return 方法返回
     */
    public static Object invokeQuietly(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalArgumentException | IllegalAccessException ignored) {
            // ignored
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
        }

        return null;
    }

    public static Class<?>[] types(Object... values) {
        if (isNull(values)) return new Class[0];

        val result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = checkNull(values[i], () -> NULL.class, Object::getClass);
        }
        return result;
    }

    public static boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                val actualType = actualTypes[i];
                if (actualType == NULL.class ||
                        wrapper(declaredTypes[i]).isAssignableFrom(
                                wrapper(actualType))) continue;
                return false;
            }
            return true;
        } else return false;
    }

    public static Class<?>[] getConstructorParameterTypes(Class<?> clazz, Object... arguments) {
        val types = types(arguments);

        try {
            return clazz.getDeclaredConstructor(types).getParameterTypes();
        } catch (NoSuchMethodException e) {
            for (val constructor : clazz.getDeclaredConstructors()) {
                val parameterTypes = constructor.getParameterTypes();
                if (match(parameterTypes, types)) return parameterTypes;
            }
            throw new IllegalArgumentException(clazz
                    + "'s Constructor with such arguments Not Found");
        }
    }

    public static final class DepthComparator implements Comparator<Class<?>> {

        private final Class<?> targetClass;

        public DepthComparator(Object target) {
            this.targetClass = checkNotNull(target.getClass(), "Target must not be null");
        }

        public DepthComparator(Class<?> targetClass) {
            this.targetClass = checkNotNull(targetClass, "Target type must not be null");
        }

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            int depth1 = getDepth(o1, this.targetClass, 0);
            int depth2 = getDepth(o2, this.targetClass, 0);
            return (depth1 - depth2);
        }

        private int getDepth(Class<?> declaredClass, Class<?> classToMatch, int depth) {
            // Found it!
            if (classToMatch.equals(declaredClass)) return depth;
            // If we've gone as far as we can go and haven't found it...
            if (classToMatch == Object.class) return Integer.MAX_VALUE;
            return getDepth(declaredClass, classToMatch.getSuperclass(), depth + 1);
        }
    }

    private interface NULL {}
}
