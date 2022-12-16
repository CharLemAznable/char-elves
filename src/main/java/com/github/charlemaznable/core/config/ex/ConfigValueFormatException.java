package com.github.charlemaznable.core.config.ex;

import java.io.Serial;

public final class ConfigValueFormatException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7056317457622699829L;

    public ConfigValueFormatException(String msg) {
        super(msg);
    }
}
