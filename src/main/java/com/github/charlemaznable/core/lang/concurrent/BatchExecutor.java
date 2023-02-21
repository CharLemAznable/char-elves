package com.github.charlemaznable.core.lang.concurrent;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Runtime.getRuntime;
import static java.util.Objects.nonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;

public abstract class BatchExecutor<T> extends EventBusExecutor {

    @Getter
    private final int maxBatchSize;
    private final long initialDelay;
    private final long delay;
    private final TimeUnit unit;

    private volatile boolean running;

    private final LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private ThreadPoolExecutor eventExecutor;
    private final Object eventObject = new Object();
    private final Executor batchEventExecutor;

    public BatchExecutor(BatchExecutorConfig config) {
        this.maxBatchSize = config.getMaxBatchSize();
        this.initialDelay = config.getInitialDelay();
        this.delay = config.getDelay();
        this.unit = config.getUnit();
        this.batchEventExecutor = batchEventExecutor();
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        post(eventObject, initialDelay, unit);
        getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
    }

    public void add(T item) {
        queue.add(item);

        if (queue.size() >= maxBatchSize) {
            post(new Object());
        }
    }

    public abstract void batchExecute(List<T> items);

    @AllowConcurrentEvents
    @Subscribe
    public void subscribeEvent(Object event) {
        val items = new ArrayList<T>();
        queue.drainTo(items);
        if (!items.isEmpty()) {
            this.batchEventExecutor.execute(() -> batchExecute(items));
        }
        if (running && eventObject == event)
            post(event, delay, unit);
    }

    @Override
    protected Executor eventBusExecutor() {
        if (nonNull(eventExecutor)) return eventExecutor;
        eventExecutor = new ThreadPoolExecutor(2, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        return eventExecutor;
    }

    protected Executor batchEventExecutor() {
        return newCachedThreadPool();
    }
}
