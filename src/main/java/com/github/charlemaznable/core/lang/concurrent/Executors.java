package com.github.charlemaznable.core.lang.concurrent;

import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Executors {

    public static ExecutorService unlimitExecutor() {
        return unlimitExecutor(0);
    }

    public static ExecutorService unlimitExecutor(int corePoolSize) {
        return defaultExecutor(corePoolSize, Integer.MAX_VALUE);
    }

    public static ExecutorService parallelismExecutor() {
        return parallelismExecutor(0);
    }

    public static ExecutorService parallelismExecutor(int corePoolSize) {
        return parallelismExecutor(corePoolSize, 1);
    }

    public static ExecutorService parallelismExecutor(int corePoolSize, int parallelism) {
        return defaultExecutor(corePoolSize, Runtime.getRuntime().availableProcessors() * parallelism);
    }

    public static ExecutorService defaultExecutor(int corePoolSize, int maximumPoolSize) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }
}
