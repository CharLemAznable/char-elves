package com.github.charlemaznable.core.config.ex;

import java.io.Serial;

public final class ConfigException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6278916573612646289L;

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
