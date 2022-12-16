package com.github.charlemaznable.core.lang;

import lombok.val;
import net.jodah.expiringmap.ExpiringMap;
import net.jodah.expiringmap.ExpiringValue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static com.github.charlemaznable.core.lang.Await.awaitForSeconds;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExpiringTest {

    private final ExpiringMap<String, Long> testCache
            = ExpiringMapp.expiringMap(ExpiringEntryLoaderr.from(this::loadCache));

    @Test
    public void testExpiring() {
        val v1 = testCache.get("ABC");
        awaitForSeconds(1);
        val v2 = testCache.get("ABC");
        awaitForSeconds(2);
        val v3 = testCache.get("ABC");

        assertEquals(v1, v2);
        assertNotEquals(v2, v3);
    }

    public ExpiringValue<Long> loadCache(String key) {
        return new ExpiringValue<>(currentTimeMillis(), 2, TimeUnit.SECONDS);
    }
}
