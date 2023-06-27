package com.github.charlemaznable.core.lang.function;

@FunctionalInterface
public interface SupplierWithException<T> {

    T get() throws Exception;
}
