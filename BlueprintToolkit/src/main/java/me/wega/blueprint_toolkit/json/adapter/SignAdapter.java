package me.wega.blueprint_toolkit.json.adapter;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;

import java.lang.reflect.Type;

public class SignAdapter implements JsonSerializer<Sign>, JsonDeserializer<Sign> {

    @Override
    public JsonElement serialize(Sign sign, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("world", context.serialize(sign.getWorld(), World.class));
        jsonObject.addProperty("x", sign.getX());
        jsonObject.addProperty("y", sign.getY());
        jsonObject.addProperty("z", sign.getZ());
        return jsonObject;
    }

    @Override
    public Sign deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Location loc =  new Location(
                context.deserialize(jsonObject.get("world"), World.class),
                jsonObject.get("x").getAsDouble(),
                jsonObject.get("y").getAsDouble(),
                jsonObject.get("z").getAsDouble()
        );
        return ((Sign) loc.getBlock().getState());
    }
}