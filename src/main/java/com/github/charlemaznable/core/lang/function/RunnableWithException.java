package com.github.charlemaznable.core.lang.function;

@FunctionalInterface
public interface RunnableWithException {

    void run() throws Exception;
}
