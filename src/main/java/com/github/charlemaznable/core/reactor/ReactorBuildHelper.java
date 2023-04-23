package com.github.charlemaznable.core.reactor;

import io.vertx.core.Future;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ReactorBuildHelper {

    public static Mono<Object> buildMonoFromFuture(CompletableFuture<Object> future) {
        return Mono.fromFuture(future);
    }

    public static Mono<Object> buildMonoFromVertxFuture(Future<Object> future) {
        return Mono.fromFuture(future.toCompletionStage().toCompletableFuture());
    }
}
