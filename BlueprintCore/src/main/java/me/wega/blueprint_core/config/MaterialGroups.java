package me.wega.blueprint_core.config;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A utility class holding various material groups.
 */
@UtilityClass
public class MaterialGroups {
    private static final String[] TRANSPARENT = {
            "LAVA", "WATER", "GRASS", "GLASS", "BAMBOO", "FLOWER", "MUSHROOM", "DEAD_BUSH",
            "SUGAR_CANE", "VINE", "NETHER_WART", "STEM", "SAPLING", "BARRIER", "BEETROOT",
            "BANNER", "CARPET", "BRAIN_CORAL", "CHORUS_FLOWER", "MOVING_PISTON",
            "PISTON_HEAD", "FIRE", "SNOW", "ICE", "PORTAL", "CAKE_BLOCK", "ENDER_PORTAL",
            "ROSE", "SUNFLOWER", "ORCHID", "CAMPFIRE", "DANDELION", "RAIL", "KELP", "CRYSTAL",
            "GATEWAY", "END_ROD", "FERN", "GLASS_PANE", "PRESSURE_PLATE", "IRON_BARS",
            "TRAPDOOR", "LADDER", "LILAC", "LILY", "TULIP", "BLUET", "POPPY", "POTTED",
            "ALLIUM", "REDSTONE", "SCAFFOLDING", "BERRY", "BERRIES", "TORCH", "HEAD",
            "WALL", "DOOR", "AIR", "SLAB", "FENCE", "LEAF", "LEAVES", "LIGHT", "FLOWER_POT",
            "CANDLE", "LEVER", "BUTTON", "HOPPER", "FENCE_GATE", "SEA_PICKLE", "COBWEB", "SIGN"
    };

    public static final @NotNull Set<@NotNull Material> TRANSPARENT_MATERIALS = Arrays.stream(Material.values())
            .filter(material -> Arrays.stream(TRANSPARENT)
                    .anyMatch(fragment -> material.name().contains(fragment)))
            .collect(Collectors.toSet());


    private static final String[] PASS_THROUGH = {
            "LAVA", "WATER", "GRASS", "BAMBOO", "FLOWER", "MUSHROOM", "DEAD_BUSH",
            "SUGAR_CANE", "VINE", "NETHER_WART", "STEM", "SAPLING", "BEETROOT",
            "BANNER", "CARPET", "BRAIN_CORAL", "CHORUS_FLOWER",
            "FIRE", "SNOW", "ICE", "CAKE_BLOCK",
            "ROSE", "SUNFLOWER", "ORCHID", "DANDELION", "RAIL", "KELP",
            "END_ROD", "FERN", "GLASS_PANE", "PRESSURE_PLATE",
            "LADDER", "LILAC", "LILY", "TULIP", "BLUET", "POPPY", "POTTED",
            "ALLIUM", "REDSTONE", "SCAFFOLDING", "BERRY", "BERRIES", "TORCH",
            "SIGN", "AIR", "LEAF", "LEAVES", "LIGHT", "FLOWER_POT",
            "CANDLE", "LEVER", "BUTTON", "SEA_PICKLE", "COBWEB"
    };

    public static final @NotNull Set<@NotNull Material> PASS_THROUGH_MATERIALS = Arrays.stream(Material.values())
            .filter(material -> Arrays.stream(PASS_THROUGH)
                    .anyMatch(fragment -> material.name().contains(fragment)))
            .collect(Collectors.toSet());


    /**
     * Gets a set of materials based on the specified parameters.
     *
     * @param transparent Whether to include transparent materials.
     * @param passThrough Whether to include pass-through materials.
     * @return A set of materials based on the specified parameters.
     */
    public static @Nullable Set<@NotNull Material> getMaterials(boolean transparent, boolean passThrough) {
        if (!transparent && !passThrough) return null;

        Set<Material> materials = TRANSPARENT_MATERIALS;
        if (passThrough) materials.addAll(PASS_THROUGH_MATERIALS);
        return materials;
    }
}
