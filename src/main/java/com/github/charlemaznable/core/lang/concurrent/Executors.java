package com.github.charlemaznable.core.lang.concurrent;

import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Executors {

    public static ExecutorService parallelismExecutor() {
        return parallelismExecutor(1); // default maximum pool size: Runtime.getRuntime().availableProcessors()
    }

    public static ExecutorService parallelismExecutor(int parallelism) {
        return parallelismExecutor(0, parallelism); // default core pool size: 0
    }

    public static ExecutorService parallelismExecutor(int corePoolSize, int parallelism) {
        return parallelismExecutor(corePoolSize, parallelism, java.util.concurrent.Executors.defaultThreadFactory());
    }

    public static ExecutorService parallelismExecutor(int corePoolSize, int parallelism, ThreadFactory threadFactory) {
        return limitQueueExecutor(corePoolSize, Runtime.getRuntime().availableProcessors() * parallelism, threadFactory);
    }

    public static ExecutorService limitQueueExecutor(int corePoolSize, int maximumPoolSize, ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
    }
}
