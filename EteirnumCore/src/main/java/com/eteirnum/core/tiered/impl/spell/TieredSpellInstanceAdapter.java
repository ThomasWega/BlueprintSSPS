package com.eteirnum.core.tiered.impl.spell;

import com.eteirnum.core.tiered.TieredObjectImpl;
import com.google.gson.*;

import java.lang.reflect.Type;

import static com.eteirnum.core.EteirnumCore.instance;

public class TieredSpellInstanceAdapter implements JsonSerializer<TieredSpellInstance>, JsonDeserializer<TieredSpellInstance> {

    @Override
    public JsonElement serialize(TieredSpellInstance tieredSpellInstance, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();

        object.addProperty("item-id", tieredSpellInstance.getObject().getId());
        object.addProperty("tier", tieredSpellInstance.getTierNum());

        return object;
    }

    @Override
    public TieredSpellInstance deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String itemId = object.get("item-id").getAsString();
        TieredSpell item = (TieredSpell) instance.getTieredObjectManager().get(TieredObjectImpl.SPELL, itemId);
        int tier = object.get("tier").getAsInt();

        return new TieredSpellInstance(item, tier);
    }
}
