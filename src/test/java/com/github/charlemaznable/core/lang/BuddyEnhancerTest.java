package com.github.charlemaznable.core.lang;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuddyEnhancerTest {

    @Test
    public void testEnhancerrCreate() {
        ActualClass actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Interceptor());
        actual.method();
        assertEquals(1, Interceptor.count);

        actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Class[]{},
                new Interceptor());
        actual.method();
        assertEquals(2, Interceptor.count);

        actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Class[]{}, method -> 0,
                new BuddyEnhancer.Delegate[]{new Interceptor()});
        actual.method();
        assertEquals(3, Interceptor.count);

        val params = new Object[]{new ActualParamType()};

        actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Interceptor(), params);
        actual.method();
        assertEquals(4, Interceptor.count);

        actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Class[]{},
                new Interceptor(), params);
        actual.method();
        assertEquals(5, Interceptor.count);

        actual = (ActualClass) BuddyEnhancer.create(
                ActualClass.class, new Class[]{}, method -> 0,
                new BuddyEnhancer.Delegate[]{new Interceptor()}, params);
        actual.method();
        assertEquals(6, Interceptor.count);
    }

    static class ActualClass {

        protected ActualClass() {
        }

        public ActualClass(ParamType init) {
        }

        public void method() {
        }
    }

    static class ParamType {
    }

    static class ActualParamType extends ParamType {
    }

    static class Interceptor implements BuddyEnhancer.Delegate {

        static int count = 0;

        @Override
        public Object invoke(Method method, Object[] args, Callable<Object> superCall) throws Throwable {
            count++;
            return superCall.call();
        }
    }
}
