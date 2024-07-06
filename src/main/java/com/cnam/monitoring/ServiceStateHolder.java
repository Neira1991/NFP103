package com.cnam.monitoring;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceStateHolder {

    public static final Duration DEFAULT_JOB_FINISH_THRESHOLD = Duration.ofMinutes(15);

    private final Duration jobFinishThreshold;
    private volatile boolean enabled;
    private volatile Instant lastActionTime = Instant.now();

    public ServiceStateHolder() {
        this(DEFAULT_JOB_FINISH_THRESHOLD);
    }

    public ServiceStateHolder(Duration jobFinishThreshold) {
        this.jobFinishThreshold = jobFinishThreshold;
    }

    public boolean isAlive() {
        return lastActionTime.isAfter(Instant.now().minus(jobFinishThreshold));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


}
