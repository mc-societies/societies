package org.societies.converter;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.societies.api.converter.Converter;

/**
 * Represents a DummyConverter
 */
public class DummyConverter implements Converter {

    private final Logger logger;

    @Inject
    public DummyConverter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void convert() {
        logger.info("Noting to convert");
    }
}
