package com.github.charlemaznable.core.gm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class GMTest {

    @Test
    public void testGM() {
        assertThrows(UnsupportedOperationException.class, GM::new);
    }
}
