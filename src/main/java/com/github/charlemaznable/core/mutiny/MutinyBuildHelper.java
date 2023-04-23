package com.github.charlemaznable.core.mutiny;

import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MutinyBuildHelper {

    public static Uni<Object> buildUniFromFuture(java.util.concurrent.Future<Object> future) {
        return Uni.createFrom().future(future);
    }

    public static Uni<Object> buildUniFromVertxFuture(io.vertx.core.Future<Object> future) {
        return Uni.createFrom().emitter(emitter -> future.onComplete(ar -> {
            if (ar.succeeded()) {
                emitter.complete(ar.result());
            } else {
                emitter.fail(ar.cause());
            }
        }));
    }
}
