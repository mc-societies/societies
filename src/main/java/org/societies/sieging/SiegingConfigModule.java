package org.societies.sieging;

import com.typesafe.config.Config;
import org.joda.time.Duration;
import org.societies.AbstractConfigModule;

import java.util.concurrent.TimeUnit;

/**
 * Represents a ConfigModule
 */
class SiegingConfigModule extends AbstractConfigModule {


    public SiegingConfigModule(Config config) {
        super(config);
    }

    @Override
    protected void configure() {
        bindNamed("sieging.min-distance", "sieging.min-distance", double.class);
        bindNamed("sieging.start-duration", Duration.class).toInstance(new Duration(config
                .getDuration("sieging.start-duration", TimeUnit.MILLISECONDS)));
    }
}
