package me.wega.blueprint_toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for potion effects.
 */
@UtilityClass
public class PotionUtils {

    /**
     * Gets a potion effect type by name.
     * First tries to get the effect type by the name, then by the key.
     *
     * @param name the name of the potion effect type
     * @return the potion effect type, or null if not found
     */
    public static @Nullable PotionEffectType getPotionEffectType(@NotNull String name) {
        @Nullable PotionEffectType effectType = PotionEffectType.getByName(name);
        if (effectType == null)
            effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
        return effectType;
    }
}
