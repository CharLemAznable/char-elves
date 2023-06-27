package com.github.charlemaznable.core.lang.function;

@FunctionalInterface
public interface ConsumerWithException<T> {

    void accept(T t) throws Exception;
}
