package com.github.charlemaznable.core.lang.concurrent;

@FunctionalInterface
public interface Callable2<V, T1, T2> {

    V call(T1 param1, T2 param2);
}
