package com.github.charlemaznable.core.lang;

import lombok.NoArgsConstructor;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Await {

    public static void awaitForMicros(long duration) {
        await(duration, TimeUnit.MICROSECONDS);
    }

    public static void awaitForMillis(long duration) {
        await(duration, TimeUnit.MILLISECONDS);
    }

    public static void awaitForSeconds(long duration) {
        await(duration, TimeUnit.SECONDS);
    }

    public static void await(long duration, TimeUnit unit) {
        await(unit.toMillis(duration));
    }

    public static void await(long millis) {
        Awaitility.await().forever().pollDelay(
                Duration.ofMillis(millis)).until(() -> true);
    }
}
