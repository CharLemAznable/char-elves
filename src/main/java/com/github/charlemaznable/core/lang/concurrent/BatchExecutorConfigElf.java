package com.github.charlemaznable.core.lang.concurrent;

import com.google.common.primitives.Primitives;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static com.github.charlemaznable.core.lang.Condition.checkNotNull;
import static com.github.charlemaznable.core.lang.Objectt.setValue;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

@NoArgsConstructor(access = PRIVATE)
public final class BatchExecutorConfigElf {

    public static BatchExecutorConfig parsePropertiesToBatchExecutorConfig(Properties properties) {
        val config = new BatchExecutorConfig();
        for (val prop : properties.entrySet()) {
            setValue(config, Objects.toString(prop.getKey()), returnType -> {
                val value = Objects.toString(prop.getValue());
                val rt = Primitives.unwrap(checkNotNull(returnType));
                if (rt == int.class) return toInt(value);
                if (rt == long.class) return toLong(value);
                if (rt == TimeUnit.class) return TimeUnit.valueOf(value);
                return null;
            });
        }
        return config;
    }
}
