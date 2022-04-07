package com.github.charlemaznable.core.lang.concurrent;

import com.github.charlemaznable.core.lang.Rand;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.charlemaznable.core.lang.Await.awaitForMillis;
import static java.lang.Runtime.getRuntime;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BatchExecutorTest {

    private List<String> result = new ArrayList<>();
    private BatchExecutor<String> batchExecutor = new BatchExecutor<String>(10, 0, 1, TimeUnit.SECONDS) {

        @Override
        public void batchExecute(List<String> items) {
            result.addAll(items);
        }
    };

    @SneakyThrows
    @Test
    public void testBatchExecutor() {
        batchExecutor.start();

        int threads = getRuntime().availableProcessors() + 1;
        val service = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            service[i] = new Thread(() -> batchRun(1000));
            service[i].start();
        }

        for (int i = 0; i < threads; i++) {
            service[i].join();
        }

        await().untilAsserted(() -> assertEquals(threads * 1000, result.size()));

        batchExecutor.stop();
    }

    public void batchRun(int times) {
        for (int i = 0; i < times; ++i) {
            awaitForMillis(10);
            batchExecutor.add(Rand.randAlphanumeric(10));
        }
    }
}
