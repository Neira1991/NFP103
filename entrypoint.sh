#!/bin/bash

LOG_LEVEL=${LOG_LEVEL:="INFO"}

POOL_SIZE="50"
PRIORITY_POOL_SIZE=${PRIORITY_POOL_SIZE:="10"}
HEAVY_POOL_SIZE="5"


EXTRA_JVM_OPTS=${EXTRA_JVM_OPTS="-XX:MaxRAM=1073641824 -Xmx800m "}

#exec is important to propagate SIGTERM to application
exec java \
 -XX:+UseG1GC \
 ${EXTRA_JVM_OPTS} \
 -jar ./build/libs/app.jar\
    scheduledPoolSize=${POOL_SIZE} \
    priorityPoolSize=${PRIORITY_POOL_SIZE} \
    heavyPoolSize=${HEAVY_POOL_SIZE}
