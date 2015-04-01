package org.societies.request;

import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.shank.AbstractModule;
import org.societies.groups.request.Request;
import org.societies.groups.request.RequestFactory;
import org.societies.groups.request.simple.Choices;
import org.societies.groups.request.simple.SimpleRequest;

/**
 * Represents a RequestModule
 */
public class RequestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(new TypeLiteral<Request<Choices>>() {
                }, SimpleRequest.class)
                .build(new TypeLiteral<RequestFactory<Choices>>() {
                }));
    }
}
