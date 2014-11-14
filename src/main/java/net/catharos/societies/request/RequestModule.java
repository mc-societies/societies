package net.catharos.societies.request;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import net.catharos.groups.request.Request;
import net.catharos.groups.request.RequestFactory;
import net.catharos.groups.request.simple.Choices;
import net.catharos.groups.request.simple.SimpleRequest;
import net.catharos.lib.shank.AbstractModule;

/**
 * Represents a RequestModule
 */
public class RequestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(new TypeLiteral<Request<Choices>>() {}, SimpleRequest.class)
                .build(new TypeLiteral<RequestFactory<Choices>>() {}));
    }
}
