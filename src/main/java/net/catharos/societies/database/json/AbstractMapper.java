package net.catharos.societies.database.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Represents a AbstractMapper
 */
public class AbstractMapper {

    private final JsonFactory factory = new ObjectMapper().getFactory();

    protected JsonParser createParser(String data) throws IOException {
        return factory.createParser(data);
    }

    protected JsonParser createParser(File file) throws IOException {
        return factory.createParser(file);
    }

    protected JsonGenerator createGenerator(File file) throws IOException {
        return factory.createGenerator(file, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(Writer writer) throws IOException {
        return factory.createGenerator(writer);
    }
}
