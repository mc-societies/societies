package org.societies.database.json;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.migcomponents.migbase64.Base64;
import org.bukkit.Server;
import org.societies.api.math.Location;
import org.societies.util.uuid.UUIDGen;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.UUID;

/**
 * Represents a AbstractMapper
 */
public class AbstractMapper {

    protected ObjectMapper mapper = new ObjectMapper();
    private JsonFactory factory;

    private final Server worldResolver;

    public AbstractMapper(Server worldResolver) {
        this.worldResolver = worldResolver;
        factory = mapper.getFactory();
    }

    protected JsonNode createNode(String data) throws IOException {
        return mapper.readTree(data);
    }

    protected JsonNode createNode(File file) throws IOException {
        return mapper.readTree(file);
    }

    protected boolean isEmpty(TreeNode node) {
        return node.isContainerNode() && node.size() == 0;
    }

    protected JsonGenerator createGenerator(File file) throws IOException {

        return factory.createGenerator(file, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(OutputStream stream) throws IOException {
        return factory.createGenerator(stream, JsonEncoding.UTF8);
    }

    protected JsonGenerator createGenerator(Writer writer) throws IOException {
        return factory.createGenerator(writer);
    }


    public UUID toUUID(JsonNode node) {
        return UUIDGen.toUUID(Base64.decode(node.asText()));
    }

    public String toText(UUID uuid) {
        return Base64.encodeToString(UUIDGen.toByteArray(uuid), false);
    }

    public Location toLocation(JsonNode node) {
        if (node.isMissingNode()) {
            return null;
        }
        return new Location(worldResolver.getWorld(node.path("world").asText()),
                node.path("x").asDouble(),
                node.path("y").asDouble(),
                node.path("z").asDouble(),
                (float) node.path("pitch").asDouble(),
                (float) node.path("yaw").asDouble(),
                (float) node.path("roll").asDouble());
    }

    public JsonNode toNode(Location location) {
        ObjectNode node = mapper.createObjectNode();

        node.put("world", location.getWorld().getName());
        node.put("x", location.getX());
        node.put("y", location.getY());
        node.put("z", location.getZ());
        node.put("pitch", location.getPitch());
        node.put("yaw", location.getYaw());
        node.put("roll", location.getRoll());
        return node;
    }
}
