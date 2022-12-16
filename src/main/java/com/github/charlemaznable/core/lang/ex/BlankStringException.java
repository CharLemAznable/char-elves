package com.github.charlemaznable.core.lang.ex;

import java.io.Serial;

public final class BlankStringException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1676373107317262934L;

    public BlankStringException() {
        super();
    }

    public BlankStringException(String s) {
        super(s);
    }
}
