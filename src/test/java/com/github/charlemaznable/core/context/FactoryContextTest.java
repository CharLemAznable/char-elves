package com.github.charlemaznable.core.context;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.context.FactoryContext.ReflectFactory.reflectFactory;
import static com.github.charlemaznable.core.spring.SpringFactory.springFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FactoryContextTest {

    @Test
    public void testFactoryContext() {
        assertNotNull(springFactory());
        assertSame(springFactory(), FactoryContext.get());

        FactoryContext.set(reflectFactory());
        assertNotSame(springFactory(), FactoryContext.get());

        FactoryContext.unload();
        assertSame(springFactory(), FactoryContext.get());

        assertNull(FactoryContext.build(
                reflectFactory(), TestInterface.class));
        assertNotNull(FactoryContext.build(
                reflectFactory(), TestClass.class));

        FactoryContext.accept(reflectFactory(),
                TestInterface.class, Assertions::assertNull);
        assertSame(springFactory(), FactoryContext.get());

        val desc = FactoryContext.apply(reflectFactory(),
                TestClass.class, TestClass::desc);
        assertEquals("TestClass", desc);
        assertSame(springFactory(), FactoryContext.get());
    }

    public interface TestInterface {}

    public static class TestClass {

        String desc() {
            return "TestClass";
        }
    }
}
