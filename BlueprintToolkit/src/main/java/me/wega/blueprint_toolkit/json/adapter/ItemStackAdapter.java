package me.wega.blueprint_toolkit.json.adapter;

import me.wega.blueprint_toolkit.utils.ItemStackUtils;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(ItemStackUtils.toNBTString(itemStack));
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ItemStackUtils.fromNBTString(jsonElement.getAsString());
    }
}