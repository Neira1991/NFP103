package com.cnam;

//CHECKSTYLE.OFF: ClassFanOutComplexity


import com.cnam.model.ErrorContext;
import com.cnam.model.trigger.TriggerEntry;
import com.cnam.utils.AbstractPriorityTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.cnam.Main.MDC_AID;
import static com.cnam.Main.MDC_CAID;
import static com.cnam.Main.MDC_CID;
import static com.cnam.Main.MDC_CTX;

@Slf4j
@Getter
public class ParallelizeProcessingFn<T extends TriggerEntry> {
    public static final int THREAD_KEEP_ALIVE_TIME = 10;
    public static final Duration INNER_GRACEFUL_SHUTDOWN_THRESHOLD = Duration.ofMinutes(25);

    private final String name;
    private final ThreadPoolExecutor processingPool;
    private final int poolSize;
    private final Map<T, Instant> messagesInProcess;

    public ParallelizeProcessingFn(String name, int poolSize) {
        this.name = name;
        this.poolSize = poolSize;
        PriorityBlockingQueue<Runnable> workQueue = new PriorityBlockingQueue<>(poolSize, new AbstractPriorityTask.PriorityComparator());
        processingPool = new ThreadPoolExecutor(poolSize, poolSize, THREAD_KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue,
                new ThreadFactoryBuilder().setNameFormat(name + "-thread-%d").build());
        messagesInProcess = new ConcurrentHashMap<>(poolSize);
    }

    public void process(Runnable onSuccess) {
        int priority = 0;
        CompletableFuture
                .runAsync(new AbstractPriorityTask(priority, System.currentTimeMillis()) {
                    @Override
                    public void run() {
                        onSuccess.run();
                    }
                }, processingPool)
                .whenComplete((r, e) -> {
                    if (e != null) {
                        log.error("fetched error:", e);
                    }
                    discardMDC();
                });
    }

    public int capacity() {
        return poolSize - processingPool.getActiveCount();
    }

    public void shutdown() {
        processingPool.shutdown();
        boolean terminating = false;
        Instant start = Instant.now();
        try {
            while (!processingPool.awaitTermination(1, TimeUnit.MINUTES)) {
                log.info("Waiting for termination for 1 more minute");
                if (!terminating && Instant.now().minus(INNER_GRACEFUL_SHUTDOWN_THRESHOLD).isAfter(start)) {
                    log.info("Initiating force shutdown");
                    terminating = true;
                    processingPool.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            log.error("Processing pool shutdown was interrupted");
        }
    }



    public static void enrichMDC(@Nonnull TriggerEntry data) {
        MDC.put(MDC_CID, data.getClientId());
        MDC.put(MDC_AID, data.getAccountId());
        MDC.put(MDC_CAID, data.getCampaignId());
    }

    public static void discardMDC() {
        MDC.remove(MDC_CID);
        MDC.remove(MDC_AID);
        MDC.remove(MDC_CAID);
    }
}
//CHECKSTYLE.ON: ClassFanOutComplexity
