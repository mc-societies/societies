package org.societies;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.name.Named;
import gnu.trove.map.hash.THashMap;
import org.apache.logging.log4j.Logger;
import org.societies.api.NameProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Represents a MojangNameProvider
 */
public class MojangNameProvider implements NameProvider {

    private final ListeningExecutorService service;
    private static final JsonFactory factory = new JsonFactory();

    private final Map<UUID, String> names = Collections.synchronizedMap(new THashMap<UUID, String>());

    private final Logger logger;

    @Inject
    public MojangNameProvider(@Named("worker-executor") ListeningExecutorService service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    public ListenableFuture<String> findName(final UUID uuid) {
        return service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String uuidString = uuid.toString().replace("-", "");
                URL url = new URL("http", "api.mojang.com", "/user/profiles/" + uuidString + "/names");

                InputStream stream = url.openStream();

                JsonParser parser = factory.createParser(stream);

                if (parser.nextToken() != JsonToken.START_ARRAY) {
                    throw new IOException("Expected data to start with an Array, but was " + parser.getCurrentToken());
                }

                JsonToken firstName = parser.nextToken();

                if (firstName != JsonToken.VALUE_STRING) {
                    throw new IOException("No name found for uuid " + uuidString + "!");
                }

                String name = parser.getText();
                parser.close();
                return name;
            }
        });
    }

    @Override
    public String getName(final UUID uuid) {
        String name = names.get(uuid);

        if (name == null) {
            Futures.addCallback(findName(uuid), new FutureCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    names.put(uuid, result);
                }

                @Override
                public void onFailure(Throwable t) {
                    logger.catching(t);
                }
            });

            return null;
        }

        return name;
    }
}
