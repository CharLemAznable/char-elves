package com.github.charlemaznable.core.lang.concurrent;

@FunctionalInterface
public interface Callable1<V, T1> {

    V call(T1 param1);
}
