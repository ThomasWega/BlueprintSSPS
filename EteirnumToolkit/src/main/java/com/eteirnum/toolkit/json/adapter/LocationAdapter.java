package com.eteirnum.toolkit.json.adapter;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {
    private final boolean ignoreWorld;
    private final boolean ignoreYawPitch;

    public LocationAdapter(boolean ignoreWorld, boolean ignoreYawPitch) {
        this.ignoreWorld = ignoreWorld;
        this.ignoreYawPitch = ignoreYawPitch;
    }

    public LocationAdapter() {
        this(true, false);
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        if (!ignoreWorld) jsonObject.add("world", context.serialize(location.getWorld(), World.class));
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        if (!ignoreYawPitch) {
            jsonObject.addProperty("yaw", location.getYaw());
            jsonObject.addProperty("pitch", location.getPitch());
        }
        return jsonObject;
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new Location(
                ignoreWorld ? null : context.deserialize(jsonObject.get("world"), World.class),
                jsonObject.get("x").getAsDouble(),
                jsonObject.get("y").getAsDouble(),
                jsonObject.get("z").getAsDouble(),
                ignoreYawPitch ? 0 : jsonObject.get("yaw").getAsFloat(),
                ignoreYawPitch ? 0 : jsonObject.get("pitch").getAsFloat()
        );
    }
}