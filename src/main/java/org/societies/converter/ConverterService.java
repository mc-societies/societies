package org.societies.converter;

import com.google.inject.Inject;
import org.shank.service.AbstractService;
import org.shank.service.lifecycle.LifecycleContext;
import org.societies.api.converter.Converter;

/**
 * Represents a ConverterService
 */
public class ConverterService extends AbstractService {

    private final Converter converter;

    @Inject
    public ConverterService(Converter converter) {
        this.converter = converter;
    }

    @Override
    public void init(LifecycleContext context) throws Exception {
        converter.convert();
    }
}
