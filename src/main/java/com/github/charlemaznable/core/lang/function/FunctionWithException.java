package com.github.charlemaznable.core.lang.function;

@FunctionalInterface
public interface FunctionWithException<T, R> {

    R apply(T t) throws Exception;
}
