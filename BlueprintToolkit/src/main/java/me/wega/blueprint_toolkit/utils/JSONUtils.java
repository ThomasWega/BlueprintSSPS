package me.wega.blueprint_toolkit.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the conversion from JSON to other Types
 */
@UtilityClass
public class JSONUtils {

    /**
     * All data will be preserved (colors, events, ...)
     *
     * @param json Json to convert
     * @return Converted JSON to Component
     */
    public static @NotNull Component toComponent(@NotNull JsonObject json) {
        return GsonComponentSerializer.gson().deserialize(json.toString());
    }

    /**
     * Converts a JsonArray to a List of Strings.
     *
     * @param jsonArray The JsonArray to convert
     * @return A List of Strings containing the elements of the JsonArray
     */
    public static @NotNull List<@NotNull String> jsonArrayToList(@Nullable JsonArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray == null)
            return list;
        for (JsonElement element : jsonArray)
            list.add(element.getAsString());
        return list;
    }

    /**
     * Safely gets a String from a JsonObject.
     *
     * @param jsonObject   The JsonObject to get the string from
     * @param key          The key of the string in the JsonObject
     * @param defaultValue The default value to return if the key doesn't exist or isn't a string
     * @return The string value, or the default value if not found
     */
    @Contract("_, _, !null -> !null")
    public static @Nullable String getStringOrDefault(@NotNull JsonObject jsonObject, @NotNull String key, @Nullable String defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
            return element.getAsString();
        return defaultValue;
    }

    /**
     * Safely gets an int from a JsonObject.
     *
     * @param jsonObject   The JsonObject to get the int from
     * @param key          The key of the int in the JsonObject
     * @param defaultValue The default value to return if the key doesn't exist or isn't an int
     * @return The int value, or the default value if not found
     */
    @Contract("_, _, !null -> !null")
    public static @Nullable Integer getIntOrDefault(@NotNull JsonObject jsonObject, @NotNull String key, @Nullable Integer defaultValue) {
        JsonElement element = jsonObject.get(key);
        if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber())
            return element.getAsInt();
        return defaultValue;
    }
}