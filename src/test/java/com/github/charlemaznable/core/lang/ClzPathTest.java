package com.github.charlemaznable.core.lang;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.lang.ClzPath.classResourceAsSubstitutor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClzPathTest {

    @Test
    public void testClassExists() {
        assertFalse(ClzPath.classExists("com.github.charlemaznable.core.lang.ClzPathNon"));
        assertTrue(ClzPath.classExists("com.github.charlemaznable.core.lang.ClzPath"));
    }

    @Test
    public void testFindClass() {
        assertNull(ClzPath.findClass(""));
        assertNull(ClzPath.findClass("com.github.charlemaznable.core.lang.ClzPathNon"));
        assertNotNull(ClzPath.findClass("com.github.charlemaznable.core.lang.ClzPath"));
    }

    @Test
    public void testTryLoadClass() {
        assertNull(ClzPath.tryLoadClass(""));
        assertNull(ClzPath.tryLoadClass("com.github.charlemaznable.core.lang.ClzPathNon"));
        assertNotNull(ClzPath.tryLoadClass("com.github.charlemaznable.core.lang.ClzPath"));
    }

    @Test
    public void testSubstitutor() {
        val envspace = classResourceAsSubstitutor("envspace.props");
        assertEquals("customconfig", envspace.replace("${bizconfigdir}"));
    }
}
