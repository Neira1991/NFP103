package com.cnam.monitoring;

import com.cnam.ParallelizeProcessingFn;
import com.cnam.ThreadPoolManager;
import com.cnam.options.Options;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RestServer {

    public static final int ERROR_CODE = 500;
    public static final int OK_CODE = 200;
    public static final int PORT = 8080;
    private final ServiceStateHolder serviceStateHolder;
    private Vertx vertx;
    private ThreadPoolManager threadPoolManager;

    public RestServer(ServiceStateHolder serviceStateHolder, Options options, PeriodicalLoggingTask periodicalLoggingTask) {
        this.serviceStateHolder = serviceStateHolder;
        this.threadPoolManager = new ThreadPoolManager(options, periodicalLoggingTask);
    }

    public void start() {
        vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        router.get("/*").handler(BodyHandler.create());
        router.get("/liveness").handler(new LivenessHandler(serviceStateHolder));
        router.get("/readiness").handler(new ReadinessHandler(serviceStateHolder));

        router.get("/scheduled/:numberOfRequests").handler(ctx -> handleRequest(ctx, "scheduled"));
        router.get("/priority/:numberOfRequests").handler(ctx -> handleRequest(ctx, "priority"));
        router.get("/heavy/:numberOfRequests").handler(ctx -> handleRequest(ctx, "heavy"));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, http -> {
                    if (http.failed()) {
                        throw new RuntimeException("Failed to start healthcheck listen", http.cause());
                    }
                });

    }

    private void handleRequest(RoutingContext ctx, String poolName) {
        int numberOfRequests = Integer.parseInt(ctx.pathParam("numberOfRequests"));
        ParallelizeProcessingFn currentPool = threadPoolManager.getPool(poolName);
        int capacity = currentPool.capacity();
        log.info("{} pool has a capacity of {} threads", poolName, capacity);
        if (capacity >= numberOfRequests) {
            String response = "{Pool:" + poolName +
                    ",\n queryLaunched :" + numberOfRequests +
                    ",\n newCapacity:" + (capacity - numberOfRequests) +
                    '}';
            ctx.response().end(response);
            for (int i = 0; i < numberOfRequests; i++) {
                int cunrrentQuery = i;
                currentPool.process(()->{
                    try {

                        TimeUnit.SECONDS.sleep(10);
                        log.info("Query "+ cunrrentQuery +" finished in "+poolName);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                });
            }

        } else {
            ctx.response().end(poolName+" has no "+numberOfRequests+" threads available" );
        }
    }

    public void stop() {
        vertx.close();
    }

    public static class LivenessHandler implements Handler<RoutingContext> {
        private final ServiceStateHolder serviceStateHolder;

        public LivenessHandler(ServiceStateHolder serviceStateHolder) {
            this.serviceStateHolder = serviceStateHolder;
        }

        @Override
        public void handle(RoutingContext event) {
            int statusCode;
            if (serviceStateHolder.isAlive()) {
                statusCode = OK_CODE;
            } else {
                statusCode = ERROR_CODE;
            }
            event.response().setStatusCode(statusCode).end(String.valueOf(statusCode));
        }
    }


    public static class ReadinessHandler implements Handler<RoutingContext> {

        private final ServiceStateHolder serviceStateHolder;

        public ReadinessHandler(ServiceStateHolder serviceStateHolder) {

            this.serviceStateHolder = serviceStateHolder;
        }

        @Override
        public void handle(RoutingContext event) {
            int statusCode;
            if (serviceStateHolder.isEnabled()) {
                statusCode = OK_CODE;
            } else {
                statusCode = ERROR_CODE;
            }
            event.response().setStatusCode(statusCode).end(String.valueOf(statusCode));
        }
    }

}
