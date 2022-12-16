package com.github.charlemaznable.core.lang.concurrent;

@FunctionalInterface
public interface Runnable2<T1, T2> {

    void run(T1 param1, T2 param2);
}
