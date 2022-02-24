package com.github.charlemaznable.core.lang;

import lombok.val;
import org.junit.jupiter.api.Test;

import static com.github.charlemaznable.core.lang.Await.awaitForMicros;
import static com.github.charlemaznable.core.lang.Await.awaitForMillis;
import static com.github.charlemaznable.core.lang.Await.awaitForSeconds;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AwaitTest {

    @Test
    public void testAwait() {
        val time0 = currentTimeMillis();
        awaitForMicros(1000_000);
        val time1 = currentTimeMillis();
        awaitForMillis(1000);
        val time2 = currentTimeMillis();
        awaitForSeconds(1);
        val time3 = currentTimeMillis();

        assertTrue(time1 >= time0 + 1000);
        assertTrue(time2 >= time1 + 1000);
        assertTrue(time3 >= time2 + 1000);
    }
}
