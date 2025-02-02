package me.wega.blueprint_core.tiered;

import me.wega.blueprint_core.tiered.tier.TieredObjectTier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tiered object is an object that has tiers and serves as a base class for tiered objects.
 * Tiered objects should always implement some functionality and instances with tiers in mind.
 * Examples of usages could be: weapons, armor, spells, potions, etc.
 *
 * @param <T> The type of the tiered object instance.
 */
@Getter
@Setter
@RequiredArgsConstructor
public abstract class TieredObject<T extends TieredObjectInstance<?>> {
    private final @NotNull TieredObjectImpl impl;
    private final @NotNull String id;
    private final @NotNull TieredObjectTier @NotNull[] tiers;

    public abstract @Nullable T createInstance(int tierNum);
}
