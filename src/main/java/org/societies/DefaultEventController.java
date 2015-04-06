package org.societies;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.common.Properties;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import org.apache.logging.log4j.Logger;
import org.societies.groups.event.Event;
import org.societies.groups.event.EventController;

/**
 * Represents a DefaultEventController
 */
@Singleton
public class DefaultEventController implements EventController {

    private final MBassador<Event> mBassador;


    @Inject
    public DefaultEventController(final Logger logger) {
        this.mBassador = new MBassador<Event>(new BusConfiguration()
                .addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default())
                .setProperty(Properties.Common.Id, "global bus")
                .setProperty(Properties.Handler.PublicationError, new IPublicationErrorHandler() {
                    @Override
                    public void handleError(PublicationError publicationError) {
                        logger.catching(publicationError.getCause());
                    }
                }));
    }

    @Override
    public void publish(Event event) {
        mBassador.publish(event);
    }

    @Override
    public void subscribe(Object listener) {
        mBassador.subscribe(listener);
    }
}
