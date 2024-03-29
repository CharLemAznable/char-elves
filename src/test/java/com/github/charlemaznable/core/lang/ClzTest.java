package com.github.charlemaznable.core.lang;

import com.github.charlemaznable.core.lang.Clz.DepthComparator;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static com.github.charlemaznable.core.lang.Clz.getConstructorParameterTypes;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClzTest {

    @Test
    public void testAssignable() {
        assertFalse(Clz.isAssignable(Integer.class, String.class));
        assertTrue(Clz.isAssignable(Integer.class, Number.class));
        assertTrue(Clz.isAssignable(Integer.class, Integer.class));

        Object[] objects = {1};
        assertTrue(Clz.isAssignable(objects[0].getClass(), Integer.class));
        assertTrue(Clz.isAssignable(objects[0].getClass(), int.class));
    }

    @Test
    public void testConcrete() {
        assertFalse(Clz.isConcrete(Serializable.class));
        assertFalse(Clz.isConcrete(Number.class));
        assertTrue(Clz.isConcrete(Integer.class));
    }

    @Test
    public void testGetMethod() {
        assertThrows(NoSuchMethodException.class, () -> Clz.getMethod(Integer.class, "nonExistsMethod"));
        assertDoesNotThrow(() -> Clz.getMethod(Integer.class, "toString"));
    }

    @Test
    public void testGetConstructorParameterTypes() {
        val testTypeClass = TestType.class;

        Class<?>[] types = getConstructorParameterTypes(testTypeClass);
        assertNotNull(types);
        assertEquals(0, types.length);

        types = getConstructorParameterTypes(testTypeClass, 1);
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals(int.class, types[0]);

        types = getConstructorParameterTypes(testTypeClass, new SubParamType());
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals(ParamType.class, types[0]);

        types = getConstructorParameterTypes(testTypeClass, null, null);
        assertNotNull(types);
        assertEquals(2, types.length);
        assertEquals(ParamType.class, types[0]);
        assertEquals(ParamType.class, types[1]);

        assertThrows(IllegalArgumentException.class,
                () -> getConstructorParameterTypes(testTypeClass, "abc"));
    }

    @Test
    public void testDepthComparator() {
        val list = Listt.newArrayList(TestType.class, ParamType.class, SubParamType.class);

        val result1 = list.stream()
                .sorted(new DepthComparator(new SubParamType()))
                .filter(c -> Clz.isAssignable(SubParamType.class, c)).toList();
        assertEquals(2, result1.size());
        assertEquals(SubParamType.class, result1.get(0));
        assertEquals(ParamType.class, result1.get(1));

        val result2 = list.stream()
                .sorted(new DepthComparator(ParamType.class))
                .filter(c -> Clz.isAssignable(ParamType.class, c)).toList();
        assertEquals(1, result2.size());
        assertEquals(ParamType.class, result2.get(0));
    }

    static class TestType {

        public TestType() {
        }

        public TestType(int i) {
        }

        public TestType(ParamType p) {
        }

        public TestType(ParamType p1, ParamType p2) {
        }
    }

    static class ParamType {}

    static class SubParamType extends ParamType {}
}
