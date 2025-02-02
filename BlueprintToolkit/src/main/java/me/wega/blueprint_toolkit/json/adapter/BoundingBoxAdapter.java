package me.wega.blueprint_toolkit.json.adapter;

import com.google.gson.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public class BoundingBoxAdapter implements JsonSerializer<BoundingBox>, JsonDeserializer<BoundingBox> {

    @Override
    public JsonElement serialize(BoundingBox box, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("min-x", box.getMinX());
        jsonObject.addProperty("min-y", box.getMinY());
        jsonObject.addProperty("min-z", box.getMinZ());
        jsonObject.addProperty("max-x", box.getMaxX());
        jsonObject.addProperty("max-y", box.getMaxY());
        jsonObject.addProperty("max-z", box.getMaxZ());
        return jsonObject;
    }

    @Override
    public BoundingBox deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        double minX = jsonObject.get("min-x").getAsDouble();
        double minY = jsonObject.get("min-y").getAsDouble();
        double minZ = jsonObject.get("min-z").getAsDouble();
        double maxX = jsonObject.get("max-x").getAsDouble();
        double maxY = jsonObject.get("max-y").getAsDouble();
        double maxZ = jsonObject.get("max-z").getAsDouble();
        Vector min = new Vector(minX, minY, minZ);
        Vector max = new Vector(maxX, maxY, maxZ);
        return BoundingBox.of(min, max);
    }
}