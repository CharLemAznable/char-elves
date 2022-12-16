package com.github.charlemaznable.core.lang.pool;

import com.github.charlemaznable.core.lang.BuddyEnhancer;
import lombok.AllArgsConstructor;
import lombok.Lombok;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

/**
 * 对象池代理工具
 */
@NoArgsConstructor(access = PRIVATE)
public final class PoolProxy {

    public static <T> ObjectPoolBuilder<T> builder(@NonNull ObjectPool<T> pool) {
        return new ObjectPoolBuilder<>(pool);
    }

    public static <T> PooledObjectCreatorBuilder<T> builder(@NonNull PooledObjectCreator<T> creator) {
        return new PooledObjectCreatorBuilder<>(creator);
    }

    @RequiredArgsConstructor
    public static final class ObjectPoolBuilder<T> {

        @NonNull
        private ObjectPool<T> pool;
        private Object[] args = new Object[]{};

        public ObjectPoolBuilder<T> args(Object... args) {
            this.args = args;
            return this;
        }

        @SneakyThrows
        @SuppressWarnings("unchecked")
        public T build() {
            T poolObject = null;
            try {
                poolObject = pool.borrowObject();
                val poolObjectClass = poolObject.getClass();
                return (T) BuddyEnhancer.create(poolObjectClass,
                        new ObjectPoolProxy<>(pool), args);
            } finally {
                if (nonNull(poolObject)) pool.returnObject(poolObject);
            }
        }
    }

    @RequiredArgsConstructor
    public static final class PooledObjectCreatorBuilder<T> {

        @NonNull
        private PooledObjectCreator<T> creator;
        private GenericObjectPoolConfig<T> config;
        private Object[] args = new Object[]{};

        public PooledObjectCreatorBuilder<T> config(GenericObjectPoolConfig<T> config) {
            this.config = config;
            return this;
        }

        public PooledObjectCreatorBuilder<T> args(Object... args) {
            this.args = args;
            return this;
        }

        public T build() {
            val factory = new PoolProxyPooledObjectFactory<T>(creator, args);
            return new ObjectPoolBuilder<T>(new GenericObjectPool<>(factory,
                    nullThen(config, GenericObjectPoolConfig::new))).args(args).build();
        }
    }

    /**
     * 对象池代理
     * <p>
     * 从对象池取出对象完成任务
     * ==
     * 调用代理对象完成任务, 即由代理完成 [从对象池取出对象]->完成任务->[向对象池交还对象]
     */
    @AllArgsConstructor
    private static final class ObjectPoolProxy<T> implements BuddyEnhancer.Delegate {

        private ObjectPool<T> pool;

        @Override
        public Object invoke(Method method, Object[] args, Callable<Object> superCall) throws Throwable {
            T poolObject = null;
            try {
                poolObject = pool.borrowObject();
                return method.invoke(poolObject, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            } finally {
                if (nonNull(poolObject))
                    pool.returnObject(poolObject);
            }
        }
    }

    /**
     * 池化对象工厂封装
     */
    @AllArgsConstructor
    private static final class PoolProxyPooledObjectFactory<T> extends BasePooledObjectFactory<T> {

        private PooledObjectCreator<T> pooledObjectCreator;
        private Object[] createArguments;

        @Override
        public T create() {
            return pooledObjectCreator.create(createArguments);
        }

        @Override
        public PooledObject<T> wrap(T t) {
            return pooledObjectCreator.wrap(t);
        }
    }
}
