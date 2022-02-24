package com.github.charlemaznable.core.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class VertxElf {

    private static final String CLUSTER_MANAGER_CLASS_PROPERTY = "vertx.cluster.managerClass";

    public static Vertx buildVertx(VertxOptions vertxOptions) {
        return buildVertx(vertxOptions, null);
    }

    @SneakyThrows
    public static Vertx buildVertx(VertxOptions vertxOptions, Function<Throwable, Vertx> exceptionFn) {
        if (nonNull(vertxOptions.getClusterManager()) ||
                nonNull(System.getProperty(CLUSTER_MANAGER_CLASS_PROPERTY))) {
            val cf = new CompletableFuture<Vertx>();
            val completableFuture = isNull(exceptionFn)
                    ? cf : cf.exceptionally(exceptionFn);
            Vertx.clusteredVertx(vertxOptions, asyncResult -> {
                if (asyncResult.failed()) {
                    completableFuture.completeExceptionally(asyncResult.cause());
                } else {
                    completableFuture.complete(asyncResult.result());
                }
            });
            return completableFuture.get();
        } else {
            return Vertx.vertx(vertxOptions);
        }
    }

    public static void closeVertx(Vertx vertx) {
        closeVertx(vertx, null);
    }

    @SneakyThrows
    public static void closeVertx(Vertx vertx, Function<Throwable, Void> exceptionFn) {
        if (isNull(vertx)) return;

        val cf = new CompletableFuture<Void>();
        val completableFuture = isNull(exceptionFn)
                ? cf : cf.exceptionally(exceptionFn);
        vertx.close(asyncResult -> {
            if (asyncResult.failed()) {
                completableFuture.completeExceptionally(asyncResult.cause());
            } else {
                completableFuture.complete(asyncResult.result());
            }
        });
        completableFuture.get();
    }

    public static void closeVertxImmediately(Vertx vertx) {
        if (isNull(vertx)) return;
        vertx.close();
    }

    public static <V> void executeBlocking(
            Handler<Promise<V>> blockingCodeHandler,
            Handler<AsyncResult<V>> resultHandler) {
        Vertx.currentContext().executeBlocking(block -> {
            try {
                blockingCodeHandler.handle(block);
            } catch (Exception e) {
                block.fail(e);
            }
        }, false, resultHandler);
    }
}
