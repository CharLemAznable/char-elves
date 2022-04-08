package com.github.charlemaznable.core.lang.concurrent;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class BatchExecutorConfig {

    public static final int DEFAULT_MAX_BATCH_SIZE = 1024;
    public static final int DEFAULT_INITIAL_DELAY = 4;
    public static final int DEFAULT_DELAY = 4;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    private int maxBatchSize = DEFAULT_MAX_BATCH_SIZE;
    private long initialDelay = DEFAULT_INITIAL_DELAY;
    private long delay = DEFAULT_DELAY;
    private TimeUnit unit = DEFAULT_UNIT;
}
