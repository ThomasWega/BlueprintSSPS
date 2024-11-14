package com.eteirnum.toolkit.json.adapter;

import com.google.gson.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.lang.reflect.Type;

public class BlockAdapter implements JsonSerializer<Block>, JsonDeserializer<Block> {

    @Override
    public JsonElement serialize(Block block, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("world", jsonSerializationContext.serialize(block.getWorld(), World.class));
        jsonObject.addProperty("x", block.getX());
        jsonObject.addProperty("y", block.getY());
        jsonObject.addProperty("z", block.getZ());
        return jsonObject;
    }

    @Override
    public Block deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Location loc =  new Location(
                jsonDeserializationContext.deserialize(jsonObject.get("world"), World.class),
                jsonObject.get("x").getAsDouble(),
                jsonObject.get("y").getAsDouble(),
                jsonObject.get("z").getAsDouble()
        );
        return loc.getBlock();
    }
}