package com.eteirnum.core.tiered.tier;

import com.eteirnum.core.tiered.effect.TieredObjectEffect;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a tier of a tiered object.
 */
@Getter
@RequiredArgsConstructor
public class TieredObjectTier {
    private final @NotNull ItemStack itemStack;
    private final @NotNull TieredObjectEffect<?> @NotNull[] effects;
}
