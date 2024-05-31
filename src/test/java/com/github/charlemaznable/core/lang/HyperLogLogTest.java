package com.github.charlemaznable.core.lang;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.charlemaznable.core.lang.HyperLogLog.rsd;
import static java.lang.Runtime.getRuntime;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HyperLogLogTest {

    private final int COUNT = 1000;
    private final int TIMES = 10000;
    private final List<String> identifiers = new ArrayList<>();
    private final HyperLogLog hll = new HyperLogLog(14); // bucket count: 2^14 = 16384
    private final HyperLogLog hll2 = new HyperLogLog(rsd(14)); // bucket count: 2^14 = 16384
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicInteger index = new AtomicInteger(0);

    @BeforeAll
    public void setup() {
        for (int i = 0; i < COUNT; i++) {
            identifiers.add(Rand.randAlphanumeric(100));
        }
    }

    @SneakyThrows
    @Test
    public void testHyperLogLog() {
        int threads = getRuntime().availableProcessors() + 1;
        val service = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            service[i] = new Thread(this::batchRun);
            service[i].start();
        }

        for (int i = 0; i < threads; i++) {
            service[i].join();
        }

        await().timeout(Duration.ofSeconds(30)).untilAsserted(() -> {
            assertEquals(TIMES, counter.get());

            val cardinality = hll.cardinality();
            log.info("result cardinality: " + cardinality);
            // hyperloglog(16384) standard error 0.81%
            // 95%置信区间: count±2*0.81%
            assertEquals(1., 1. * cardinality / COUNT, 2 * 0.0081);

            val cardinality2 = hll2.cardinality();
            log.info("result cardinality2: " + cardinality2);
            // hyperloglog(16384) standard error 0.81%
            // 95%置信区间: count±2*0.81%
            assertEquals(1., 1. * cardinality2 / COUNT, 2 * 0.0081);

            val merged = hll.merge(hll2);
            val mergedCardinality = merged.cardinality();
            log.info("result merged: " + mergedCardinality);
            // hyperloglog(16384) standard error 0.81%
            // 95%置信区间: count±2*0.81%
            assertEquals(1., 1. * mergedCardinality / COUNT, 2 * 0.0081);
        });
    }

    public void batchRun() {
        while (true) {
            int curr = index.getAndIncrement();
            if (curr >= TIMES) return;

            String identifier;
            if (curr % 10 == 0) {
                identifier = identifiers.get(curr / 10);
            } else {
                identifier = identifiers.get(Rand.randInt(COUNT));
            }

            hll.offer(identifier);
            hll2.offer(identifiers.get(Rand.randInt(COUNT)));
            counter.incrementAndGet();
        }
    }
}
