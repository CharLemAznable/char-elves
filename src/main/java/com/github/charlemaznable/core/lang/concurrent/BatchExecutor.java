package com.github.charlemaznable.core.lang.concurrent;

import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class BatchExecutor<T> {

    private int maxBatchSize;
    private long initialDelay;
    private long delay;
    private TimeUnit unit;

    private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private ScheduledExecutorService scheduler;

    public BatchExecutor(int maxBatchSize, long initialDelay, long delay, TimeUnit unit) {
        this.maxBatchSize = maxBatchSize;
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.unit = unit;
    }

    public void start() {
        if (null == scheduler || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(
                    this::rotateExecute, initialDelay, delay, unit);
        }
    }

    public void stop() {
        if (null != scheduler) scheduler.shutdownNow();
    }

    public void add(T item) {
        queue.add(item);

        if (queue.size() >= maxBatchSize) {
            scheduler.schedule(this::rotateExecute, 0, TimeUnit.SECONDS);
        }
    }

    public abstract void batchExecute(List<T> items);

    private void rotateExecute() {
        val items = new ArrayList<T>();
        queue.drainTo(items);
        batchExecute(items);
    }
}
