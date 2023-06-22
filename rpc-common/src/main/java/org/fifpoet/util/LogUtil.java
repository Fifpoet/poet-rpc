package org.fifpoet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final String ERROR_LOGGER = "error";
    private static final String WARN_LOGGER = "warn";
    private static final String INFO_LOGGER = "info";
    private static final String DEBUG_LOGGER = "debug";

    public static Logger ERROR() {
        return LoggerFactory.getLogger(ERROR_LOGGER);
    }
    public static Logger WARN() {
        return LoggerFactory.getLogger(WARN_LOGGER);
    }
    public static Logger INFO() {
        return LoggerFactory.getLogger(INFO_LOGGER);
    }
    public static Logger DEBUG() {
        return LoggerFactory.getLogger(DEBUG_LOGGER);
    }
}
