package com.github.charlemaznable.core.config;

import com.github.charlemaznable.core.config.ex.EnvConfigException;
import com.github.charlemaznable.core.config.impl.BaseConfigable;
import com.github.charlemaznable.core.lang.EasyEnhancer;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Primitives;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.apache.commons.text.StringSubstitutor;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Properties;

import static com.github.charlemaznable.core.config.Arguments.argumentsAsProperties;
import static com.github.charlemaznable.core.lang.ClzPath.classResourceAsProperties;
import static com.github.charlemaznable.core.lang.Condition.blankThen;
import static com.github.charlemaznable.core.lang.LoadingCachee.get;
import static com.github.charlemaznable.core.lang.LoadingCachee.simpleCache;
import static com.github.charlemaznable.core.lang.Propertiess.ssMap;
import static com.google.common.cache.CacheLoader.from;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotation;
import static org.springframework.core.annotation.AnnotatedElementUtils.isAnnotated;

@NoArgsConstructor(access = PRIVATE)
public final class EnvFactory {

    private static final Properties envClassPathProperties;
    private static final LoadingCache<Class<?>, Object> envCache = simpleCache(from(EnvFactory::loadEnv));

    static {
        envClassPathProperties = classResourceAsProperties("config.env.props");
    }

    @SuppressWarnings("unchecked")
    public static <T> T getEnv(Class<T> envClass) {
        return (T) get(envCache, envClass);
    }

    static String substitute(String source) {
        return new StringSubstitutor(ssMap(argumentsAsProperties(
                envClassPathProperties))).replace(source);
    }

    @Nonnull
    private static <T> Object loadEnv(@Nonnull Class<T> envClass) {
        ensureClassIsAnInterface(envClass);
        checkEnvConfig(envClass);

        val envProxy = new EnvProxy(envClass);
        return EasyEnhancer.create(EnvDummy.class,
                new Class[]{envClass, Configable.class},
                method -> {
                    if (method.isDefault() || method.getDeclaringClass()
                            .equals(EnvDummy.class)) return 1;
                    return 0;
                },
                new Callback[]{envProxy, NoOp.INSTANCE},
                new Object[]{envClass});
    }

    private static <T> void ensureClassIsAnInterface(Class<T> clazz) {
        if (clazz.isInterface()) return;
        throw new EnvConfigException(clazz + " is not An Interface");
    }

    private static <T> void checkEnvConfig(Class<T> clazz) {
        if (isAnnotated(clazz, EnvConfig.class)) return;
        throw new EnvConfigException(clazz + " has no EnvConfig annotation");
    }

    @AllArgsConstructor
    private static class EnvDummy {

        @Nonnull
        private Class<?> implClass;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EnvDummy && hashCode() == obj.hashCode();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public String toString() {
            return "Env:" + implClass.getSimpleName() + "@" + Integer.toHexString(hashCode());
        }
    }

    @AllArgsConstructor
    private static class EnvProxy implements MethodInterceptor {

        private Class<?> envClass;

        @Override
        public Object intercept(Object o, Method method, Object[] args,
                                MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass().equals(Configable.class)) {
                return method.invoke(Config.getConfigImpl(), args);
            }

            val envConfig = getMergedAnnotation(method, EnvConfig.class);
            val configKey = checkEnvConfigKey(envConfig);
            val defaultValue = checkEnvDefaultValue(envConfig);
            val defaultArgument = args.length > 0 ? args[0] : null;

            val key = blankThen(configKey, method::getName);
            val value = Config.getStr(key);
            try {
                if (nonNull(value)) return parseValue(key, value, method);
            } catch (Exception e) {
                if (nonNull(defaultArgument)) return defaultArgument;
                if (nonNull(defaultValue)) return parseValue(key, defaultValue, method);
                throw e;
            }
            if (nonNull(defaultArgument)) return defaultArgument;
            if (nonNull(defaultValue)) return parseValue(key, defaultValue, method);
            return null;
        }

        private String checkEnvConfigKey(EnvConfig envConfig) {
            if (isNull(envConfig)) return "";
            return substitute(envConfig.configKey());
        }

        private String checkEnvDefaultValue(EnvConfig envConfig) {
            if (isNull(envConfig)) return null;
            return substitute(blankThen(envConfig.defaultValue(), () -> null));
        }

        private Object parseValue(String key, String value, Method method) {
            val rt = Primitives.unwrap(method.getReturnType());
            if (rt == String.class) return value;
            if (rt.isPrimitive()) return parsePrimitive(rt, key, value);

            val grt = method.getGenericReturnType();
            val isCollection = grt instanceof ParameterizedType
                    && Collection.class.isAssignableFrom(rt);
            if (!isCollection) return parseObject(rt, key, value);

            return parseObjects((Class<?>) ((ParameterizedType) grt)
                    .getActualTypeArguments()[0], key, value);
        }

        public Object parsePrimitive(Class<?> rt, String key, String value) {
            BaseConfigable baseConfigable = (BaseConfigable) Config.getConfigImpl();
            if (rt == boolean.class) return baseConfigable.parseBool(key, value);
            if (rt == short.class) return (short) baseConfigable.parseInt(key, value);
            if (rt == int.class) return baseConfigable.parseInt(key, value);
            if (rt == long.class) return baseConfigable.parseLong(key, value);
            if (rt == float.class) return baseConfigable.parseFloat(key, value);
            if (rt == double.class) return baseConfigable.parseDouble(key, value);
            if (rt == byte.class) return Byte.parseByte(value);
            if (rt == char.class) return value.length() > 0 ? value.charAt(0) : '\0';
            return null;
        }

        private Object parseObject(Class<?> rt, String key, String value) {
            return ((BaseConfigable) Config.getConfigImpl()).parseBean(key, value, rt);
        }

        private Object parseObjects(Class<?> rt, String key, String value) {
            return ((BaseConfigable) Config.getConfigImpl()).parseBeans(key, value, rt);
        }
    }
}
