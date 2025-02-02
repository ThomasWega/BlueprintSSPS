package me.wega.blueprint_toolkit.json.adapter;

import me.wega.blueprint_toolkit.builder.ItemBuilder;
import me.wega.blueprint_toolkit.utils.ColorUtils;
import com.google.gson.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConfigItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        JsonObject itemObject = new JsonObject();

        // Add material
        itemObject.addProperty("material", itemStack.getType().name());

        // Add display name
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName())
            itemObject.addProperty("display", MiniMessage.miniMessage().serialize(itemStack.getItemMeta().displayName()));

        // Add lore
        JsonArray loreArray = new JsonArray();
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore())
            for (Component lore : itemStack.getItemMeta().lore())
                loreArray.add(MiniMessage.miniMessage().serialize(lore));

        itemObject.add("lore", loreArray);

        // Add custom model data
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData())
            itemObject.addProperty("custom-model", itemStack.getItemMeta().getCustomModelData());

        return itemObject;
    }

    @Override
    public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject itemObject = jsonElement.getAsJsonObject();
        Material material = Material.matchMaterial(itemObject.get("material").getAsString());

        @Nullable String display = itemObject.has("display") ? itemObject.get("display").getAsString() : null;
        JsonArray loreArray = itemObject.getAsJsonArray("lore");
        List<String> lore = new ArrayList<>();
        for (JsonElement loreElement : loreArray)
            lore.add(loreElement.getAsString());

        @Nullable Integer customModelData = itemObject.has("custom-model") ? itemObject.get("custom-model").getAsInt() : null;

        ItemBuilder builder = new ItemBuilder(material)
                .lore(ColorUtils.color(lore))
                .hideFlags();

        if (customModelData != null)
            builder.customModel(customModelData);

        if (display != null)
            builder.displayName(ColorUtils.color(display));

        return builder.build();
    }
}