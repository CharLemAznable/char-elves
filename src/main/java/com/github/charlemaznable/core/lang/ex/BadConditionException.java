package com.github.charlemaznable.core.lang.ex;

import java.io.Serial;

public final class BadConditionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6872275206318165314L;

    public BadConditionException() {
        super();
    }

    public BadConditionException(String s) {
        super(s);
    }
}
