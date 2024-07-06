package com.cnam.monitoring;

import com.cnam.ParallelizeProcessingFn;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Map.Entry.comparingByValue;

@Slf4j
public class PeriodicalLoggingTask implements Runnable {

    private final Duration entryProcessingDurationThreshold = Duration.ofMinutes(30);
    private final ServiceStateHolder serviceStateHolder;
    private final List<ParallelizeProcessingFn<?>> pools = new ArrayList<>();

    public PeriodicalLoggingTask(ServiceStateHolder serviceStateHolder) {
        this.serviceStateHolder = serviceStateHolder;
    }

    public void addPool(ParallelizeProcessingFn<?> pool) {
        pools.add(pool);
    }

    @Override
    public void run() {
        try {
            while (serviceStateHolder.isEnabled()) {
                log.info("--------PeriodicalLoggingTask---------");
                pools.forEach(this::logPoolInfo);
                log.info("-----------------");
                TimeUnit.SECONDS.sleep(10);
            }
        } catch (InterruptedException e) {
            log.info("Logging thread interrupted");
        }
    }



    public void logMemoryUsage() {
        log.info("Heap: {}", ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
        log.info("NonHeap: {}", ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage());
        List<MemoryPoolMXBean> beans = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean bean : beans) {
            log.info("Pool {}: {}", bean.getName(), bean.getUsage());
        }

        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            log.info("GC {}: count {} time {}", bean.getName(), bean.getCollectionCount(), bean.getCollectionTime());
        }
    }

    private void logPoolInfo(ParallelizeProcessingFn<?> pool) {
        log.info("Pool {} has capacity {}", pool.getName(), pool.capacity());
        final Map<?, Instant> messagesInProcess = pool.getMessagesInProcess();
        messagesInProcess.entrySet().stream()
                .sorted(comparingByValue())
                .forEach(entry -> {
                    Object message = entry.getKey();
                    Instant time = entry.getValue();
                    final Duration entryProcessingCurrentTime = Duration.between(time, Instant.now());
                    if (entryProcessingCurrentTime.compareTo(entryProcessingDurationThreshold) > 0) {
                        log.warn("Message {} has been in process for {} already", message, entryProcessingCurrentTime);
                    }
                });
    }
}
