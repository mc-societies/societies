package org.societies.bukkit.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Represents a LoggerWrapper
 */
public class LoggerWrapper extends AbstractLogger {

    public static final long serialVersionUID = 1421715L;

    private transient final Logger logger;

    public LoggerWrapper(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Message message, Throwable t) {
        return true;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, Object message, Throwable t) {
        return true;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Throwable t) {
        return true;
    }

    @Override
    public void log(Marker marker, String fqcn, Level level, Message data, Throwable t) {
        logMessage(fqcn, level, marker, data, t);
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message) {
        return true;
    }

    @Override
    public boolean isEnabled(Level level, Marker marker, String message, Object... params) {
        return true;
    }


    public void logMessage(String string, Level level, Marker marker, Message message, Throwable t) {
        String msg = message.getFormattedMessage();

        msg = MessageFormat.format(msg, message.getParameters());

        if (level == Level.ALL) {
            logger.log(java.util.logging.Level.ALL, msg, t);
        } else if (level == Level.DEBUG) {
            logger.log(java.util.logging.Level.FINEST, msg, t);
        } else if (level == Level.ERROR) {
            logger.log(java.util.logging.Level.SEVERE, msg, t);
        } else if (level == Level.FATAL) {
            logger.log(java.util.logging.Level.SEVERE, msg, t);
        } else if (level == Level.INFO) {
            logger.log(java.util.logging.Level.INFO, msg, t);
        } else if (level == Level.OFF) {
            logger.log(java.util.logging.Level.OFF, msg, t);
        } else if (level == Level.TRACE) {
            logger.log(java.util.logging.Level.SEVERE, msg, t);
        } else if (level == Level.WARN) {
            logger.log(java.util.logging.Level.WARNING, msg, t);
        } else {
            logger.log(java.util.logging.Level.INFO, msg, t);
        }
    }

    public Level getLevel() {
        return Level.INFO;
    }
}
