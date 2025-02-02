package me.wega.blueprint_core.json;

import me.wega.blueprint_core.tiered.TieredObject;
import me.wega.blueprint_core.tiered.TieredObjectAdapter;
import me.wega.blueprint_core.tiered.effect.type.AbstractParticleEffect;
import me.wega.blueprint_core.tiered.impl.spell.TieredSpellInstance;
import me.wega.blueprint_core.tiered.impl.spell.TieredSpellInstanceAdapter;
import me.wega.blueprint_core.tiered.tier.TieredObjectTier;
import me.wega.blueprint_core.tiered.tier.TieredObjectTierAdapter;
import me.wega.blueprint_toolkit.json.adapter.*;
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
            .registerTypeAdapter(AbstractParticleEffect.ParticleData.class, new AbstractParticleEffect.ParticleData.Adapter())
            .registerTypeAdapter(BoundingBox.class, new BoundingBoxAdapter())
            .create();
}
