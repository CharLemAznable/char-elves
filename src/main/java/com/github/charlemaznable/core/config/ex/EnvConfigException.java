package com.github.charlemaznable.core.config.ex;

import java.io.Serial;

public final class EnvConfigException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7130269736955691055L;

    public EnvConfigException(String msg) {
        super(msg);
    }
}
