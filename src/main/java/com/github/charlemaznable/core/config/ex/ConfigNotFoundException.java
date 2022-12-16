package com.github.charlemaznable.core.config.ex;

import java.io.Serial;

public final class ConfigNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4768838575212716181L;

    public ConfigNotFoundException(String msg) {
        super(msg);
    }
}
