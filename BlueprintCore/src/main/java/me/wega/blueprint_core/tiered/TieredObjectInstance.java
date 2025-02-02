package me.wega.blueprint_core.tiered;

import me.wega.blueprint_core.tiered.tier.TieredObjectTier;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an instance of a {@link TieredObject}, which has specific data for a specific tier.
 *
 * @param <T> The type of the tiered object.
 */
@Getter
public abstract class TieredObjectInstance<T extends TieredObject<?>> {
    private final @NotNull T object;
    private final int tierNum;
    private final @NotNull TieredObjectTier tier;

    public TieredObjectInstance(@NotNull T object, int tierNum) {
        this.object = object;
        this.tierNum = tierNum;
        this.tier = object.getTiers()[tierNum];
    }

    public abstract void use(@NotNull Player player);

    public @NotNull ItemStack getItemStack() {
        return TieredObjectStackGen.getItemStackFor(this);
    }
}
