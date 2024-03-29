package com.github.charlemaznable.core.lang.ex;

import java.io.Serial;

public final class EmptyObjectException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8336321392485797324L;

    public EmptyObjectException() {
        super();
    }

    public EmptyObjectException(String s) {
        super(s);
    }
}
