package com.cnam;

import com.cnam.model.trigger.TriggerEntry;
import com.cnam.monitoring.PeriodicalLoggingTask;
import com.cnam.options.Options;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.HashMap;
import java.util.Map;

public class ThreadPoolManager {

    private final Map<String, ParallelizeProcessingFn> pools = new HashMap<>();

    public ThreadPoolManager(Options options, PeriodicalLoggingTask periodicalLoggingTask) {
        ParallelizeProcessingFn<TriggerEntry> scheduled = new ParallelizeProcessingFn<>("scheduled", options.getScheduledPoolSize());
        ParallelizeProcessingFn<TriggerEntry> heavy = new ParallelizeProcessingFn<>("heavy", options.getHeavyPoolSize());
        ParallelizeProcessingFn<TriggerEntry> priority = new ParallelizeProcessingFn<>("priority", options.getPriorityPoolSize());
        pools.put("scheduled", scheduled);
        pools.put("heavy", heavy);
        pools.put("priority", priority);

        periodicalLoggingTask.addPool(scheduled);
        periodicalLoggingTask.addPool(heavy);
        periodicalLoggingTask.addPool(priority);
    }

    public ParallelizeProcessingFn getPool(String poolName) {
        return pools.get(poolName);
    }

    public void shutdown() {
        pools.values().forEach(ParallelizeProcessingFn::shutdown);
    }
}
