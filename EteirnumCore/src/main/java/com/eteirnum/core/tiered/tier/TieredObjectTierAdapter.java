package com.eteirnum.core.tiered.tier;

import com.eteirnum.core.tiered.effect.TieredObjectEffect;
import com.eteirnum.core.tiered.effect.TieredObjectEffectImpl;
import com.eteirnum.toolkit.builder.ConfigItemBuilder;
import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for all {@link TieredObjectTier} implementations.
 */
public class TieredObjectTierAdapter implements JsonDeserializer<TieredObjectTier> {

    @Override
    public TieredObjectTier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();

        // Deserialize ItemStack
        JsonObject itemObject = jsonObject.getAsJsonObject("item");
        ItemStack itemStack = new ConfigItemBuilder(itemObject).build();

        // Deserialize effects
        JsonElement effectsElement = jsonObject.get("effects");
        TieredObjectEffect<?>[] effects = deserializeEffects(context, getEffectsArray(effectsElement));

        return new TieredObjectTier(itemStack, effects);
    }

    public static JsonArray getEffectsArray(JsonElement effectsElement) {
        JsonArray effectsArray = new JsonArray();
        if (effectsElement != null) {
            if (effectsElement.isJsonArray()) {
                effectsArray = effectsElement.getAsJsonArray();
            } else {
                effectsArray = new JsonArray();
                effectsArray.add(effectsElement);
            }
        }
        return effectsArray;
    }

    public static TieredObjectEffect<?>[] deserializeEffects(JsonDeserializationContext context, JsonArray effectsArray) {
        List<TieredObjectEffect<?>> effectsList = new ArrayList<>();

        for (JsonElement effectElement : effectsArray) {
            JsonObject effectObject = effectElement.getAsJsonObject();
            // Gets the name of the effect (json object)
            String effectName = effectObject.keySet().iterator().next();
            TieredObjectEffectImpl effectImpl = TieredObjectEffectImpl.valueOf(effectName.toUpperCase());

            JsonElement effectDataElement = effectObject.get(effectName);
            // Check if the effect data is an array or a single object
            if (effectDataElement.isJsonArray()) {
                // It's an array, iterate over each element
                JsonArray effectDataArray = effectDataElement.getAsJsonArray();
                for (JsonElement dataElement : effectDataArray)
                    effectsList.add(getEffectInstance(context, effectImpl, dataElement));
            } else {
                // It's a single object, proceed with existing logic
                effectsList.add(getEffectInstance(context, effectImpl, effectDataElement));
            }
        }

        return effectsList.toArray(new TieredObjectEffect<?>[0]);
    }

    private static TieredObjectEffect<?> getEffectInstance(JsonDeserializationContext context, TieredObjectEffectImpl effectImpl, JsonElement dataElement) {
        JsonObject effectData = dataElement.getAsJsonObject();
        // TODO Implement effect deserialization
        return null;
    }
}
