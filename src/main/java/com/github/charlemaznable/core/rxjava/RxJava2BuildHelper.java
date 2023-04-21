package com.github.charlemaznable.core.rxjava;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RxJava2BuildHelper {

    public static Single<Object> buildSingle(java.util.concurrent.Future<Object> future) {
        return Single.fromFuture(future);
    }

    @SuppressWarnings("DuplicatedCode")
    public static Single<Object> buildSingle(io.vertx.core.Future<Object> future) {
        return RxJavaPlugins.onAssembly(new Single<>() {
            @Override
            protected void subscribeActual(@NotNull SingleObserver<? super Object> observer) {
                val disposed = new AtomicBoolean();
                observer.onSubscribe(new Disposable() {
                    @Override
                    public void dispose() {
                        disposed.set(true);
                    }

                    @Override
                    public boolean isDisposed() {
                        return disposed.get();
                    }
                });
                if (!disposed.get()) {
                    try {
                        future.onComplete(ar -> {
                            if (!disposed.getAndSet(true)) {
                                if (ar.succeeded()) {
                                    try {
                                        observer.onSuccess(ar.result());
                                    } catch (Throwable t) {
                                        Exceptions.throwIfFatal(t);
                                        RxJavaPlugins.onError(t);
                                    }
                                } else if (ar.failed()) {
                                    try {
                                        observer.onError(ar.cause());
                                    } catch (Throwable t) {
                                        Exceptions.throwIfFatal(t);
                                        RxJavaPlugins.onError(t);
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        if (!disposed.getAndSet(true)) {
                            try {
                                observer.onError(e);
                            } catch (Throwable t) {
                                Exceptions.throwIfFatal(t);
                                RxJavaPlugins.onError(t);
                            }
                        }
                    }
                }
            }
        });
    }
}
