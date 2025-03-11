// src/main/java/com/siga/utils/Logger.java

package com.siga.utils;

import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void debug(String message) {
        logger.debug(message);
    }
}