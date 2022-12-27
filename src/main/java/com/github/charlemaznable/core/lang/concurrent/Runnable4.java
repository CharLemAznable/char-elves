package com.github.charlemaznable.core.lang.concurrent;

@FunctionalInterface
public interface Runnable4<T1, T2, T3, T4> {

    void run(T1 param1, T2 param2, T3 param3, T4 param4);
}
