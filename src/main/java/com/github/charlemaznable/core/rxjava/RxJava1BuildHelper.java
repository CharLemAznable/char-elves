package com.github.charlemaznable.core.rxjava;

import lombok.NoArgsConstructor;
import rx.Single;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class RxJava1BuildHelper {

    public static Single<Object> buildSingle(java.util.concurrent.Future<Object> future) {
        return Single.from(future);
    }

    public static Single<Object> buildSingle(io.vertx.core.Future<Object> future) {
        return Single.create(sub -> future.onComplete(ar -> {
            if (!sub.isUnsubscribed()) {
                if (ar.succeeded()) {
                    sub.onSuccess(ar.result());
                } else {
                    sub.onError(ar.cause());
                }
            }
        }));
    }
}
