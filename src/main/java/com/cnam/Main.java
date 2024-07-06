package com.cnam;


import ch.qos.logback.classic.LoggerContext;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.cnam.monitoring.PeriodicalLoggingTask;
import com.cnam.monitoring.RestServer;
import com.cnam.monitoring.ServiceStateHolder;
import com.cnam.options.Options;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.pubsub.v1.ReceivedMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Slf4j
public final class Main {

    // Constants for MDC logging
    public static final String MDC_CTX = "ctx";
    public static final String MDC_CID = "cID";
    public static final String MDC_AID = "aID";
    public static final String MDC_CAID = "caID";

    public static final int DEFAULT_PRIORITY = 70;
    private static final Gson GSON_INPUT = new Gson();

    private Main() {
    }

    public static void main(String[] args) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        Options options = new Options();
        JCommander commander = JCommander.newBuilder().addObject(options).build();
        try {
            commander.parse(args);
            log.info("Parsed options: {}", options);
        } catch (ParameterException e) {
            commander.usage();
            log.info("Parsed options error: {}", e.toString());
            return;
        }
        ServiceStateHolder serviceStateHolder = new ServiceStateHolder();
        serviceStateHolder.setEnabled(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            serviceStateHolder.setEnabled(false);
            log.info("Termination signal received");
        }));
        final PeriodicalLoggingTask periodicalLoggingTask = new PeriodicalLoggingTask(serviceStateHolder);
        RestServer server = new RestServer(serviceStateHolder, options, periodicalLoggingTask);
        new Thread(periodicalLoggingTask).start();
        try {
            server.start();
        } catch (Exception e) {
            log.error("Error occurred in main method", e);
        }
    }


    private static Thread init(
            int poolSize
    ) {

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("addShutdownHook");
        }));
        Thread thread = new Thread(()->log.info("Processing messages from finished"));
        thread.setName(poolSize + "-size");
        thread.start();
        return thread;
    }

    private static <T> T parseTrigger(ReceivedMessage message, Class<T> messageType) {
        return GSON_INPUT.fromJson(message.getMessage().getData().toStringUtf8(), messageType);
    }

}
