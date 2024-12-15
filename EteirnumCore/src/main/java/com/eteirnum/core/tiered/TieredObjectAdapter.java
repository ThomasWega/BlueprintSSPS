package com.eteirnum.core.tiered;

import com.eteirnum.core.tiered.impl.spell.TieredSpell;
import com.eteirnum.core.tiered.tier.TieredObjectTier;
import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Adapter for all {@link TieredObject} implementations.
 */
public class TieredObjectAdapter implements JsonDeserializer<TieredObject<?>> {

    @Override
    public TieredObject<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String id = jsonObject.get("id").getAsString();
        TieredObjectImpl impl = context.deserialize(jsonObject.get("impl"), TieredObjectImpl.class);
        TieredObjectTier[] tiers = context.deserialize(jsonObject.getAsJsonArray("tiers"), TieredObjectTier[].class);

        return switch (impl) {
            case SPELL -> {
                @Nullable Boolean overwrite = (jsonObject.has("overwrite")) ? jsonObject.get("overwrite").getAsBoolean() : null;
                yield new TieredSpell(id, tiers, overwrite);
            }
        };
    }
}