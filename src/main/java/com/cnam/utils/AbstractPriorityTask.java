package com.cnam.utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Base class for tasks with priority. Tasks with the same priority value, sorted according submitTime.
 */
public abstract class AbstractPriorityTask implements Runnable {
    private final int priority;
    private final long submitTime;

    protected AbstractPriorityTask(int priority, long submitTime) {
        this.priority = priority;
        this.submitTime = submitTime;
    }


    public long getSubmitTime() {
        return submitTime;
    }

    public static final class PriorityComparator implements Comparator<Runnable>, Serializable {

        @Override
        public int compare(Runnable o1, Runnable o2) {
            if (o1 instanceof AbstractPriorityTask) {
                if (o2 instanceof AbstractPriorityTask) {
                    final AbstractPriorityTask p1 = (AbstractPriorityTask) o1;
                    final AbstractPriorityTask p2 = (AbstractPriorityTask) o2;
                    if (p1.priority == p2.priority) {
                        return Math.toIntExact(p1.getSubmitTime() - p2.getSubmitTime());
                    } else {
                        return p2.priority - p1.priority;
                    }
                } else {
                    return -1;
                }
            } else if (o2 instanceof AbstractPriorityTask) {
                return 1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof PriorityComparator;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
