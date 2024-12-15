package com.eteirnum.core.json;

import com.eteirnum.core.tiered.TieredObject;
import com.eteirnum.core.tiered.TieredObjectAdapter;
import com.eteirnum.core.tiered.impl.spell.TieredSpellInstance;
import com.eteirnum.core.tiered.impl.spell.TieredSpellInstanceAdapter;
import com.eteirnum.core.tiered.tier.TieredObjectTier;
import com.eteirnum.core.tiered.tier.TieredObjectTierAdapter;
import com.eteirnum.toolkit.json.adapter.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

/**
 * Gson handler for the plugin
 */
public class GsonHandler {

    /**
     * GSON instance containing all the necessary adapters for the plugin
     */
    public static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setLenient()
            .registerTypeHierarchyAdapter(World.class, new WorldAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter(false, false))
            .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
            .registerTypeAdapter(TieredObject.class, new TieredObjectAdapter())
            .registerTypeAdapter(TieredObjectTier.class, new TieredObjectTierAdapter())
            .registerTypeAdapter(TieredSpellInstance.class, new TieredSpellInstanceAdapter())
            .registerTypeAdapter(BoundingBox.class, new BoundingBoxAdapter())
            .create();
}
