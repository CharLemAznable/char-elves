package com.github.charlemaznable.core.lang.function;

import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.lang.function.Unchecker.unchecked;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UncheckerTest {

    @Test
    public void testUnchecker() {
        assertThrows(Exception.class, () -> unchecked(ExceptionFunctions::consumer).accept("sneaky"));
        assertThrows(Exception.class, () -> unchecked(ExceptionFunctions::function).apply("sneaky"));
        assertThrows(Exception.class, () -> unchecked(ExceptionFunctions::supplier).get());

        assertDoesNotThrow(() -> unchecked(t -> {}).accept("sneaky"));
        assertDoesNotThrow(() -> unchecked(t -> t).apply("sneaky"));
        assertDoesNotThrow(() -> unchecked(() -> "sneaky").get());
    }

    public static class ExceptionFunctions {

        public static void consumer(String msg) throws Exception {
            throw new Exception(msg);
        }

        public static String function(String msg) throws Exception {
            throw new Exception(msg);
        }

        public static String supplier() throws Exception {
            throw new Exception("sneaky");
        }
    }
}
