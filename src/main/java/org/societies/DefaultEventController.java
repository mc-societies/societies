package org.societies;

import com.google.inject.Singleton;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import org.societies.groups.event.Event;
import org.societies.groups.event.EventController;

/**
 * Represents a DefaultEventController
 */
@Singleton
public class DefaultEventController implements EventController {

    private final MBassador<Event> mBassador;

    public DefaultEventController() {
        BusConfiguration config = new BusConfiguration();
        config.addFeature(Feature.SyncPubSub.Default());
        config.addFeature(Feature.AsynchronousHandlerInvocation.Default());
        config.addFeature(Feature.AsynchronousMessageDispatch.Default());

        this.mBassador = new MBassador<Event>(config);
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