package me.wega.blueprint_core.tiered.effect.impl.loc;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.type.AbstractLocationEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will send a message to the target.
 */
public class TeleportEffect extends AbstractLocationEffect<Location> {

    public TeleportEffect(@NotNull TargetType @Nullable [] targetTypes,
                          @Nullable Boolean propagate,
                          @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                          @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                          @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                          @Nullable Boolean stopOnDeath,
                          @Nullable Boolean stopOnQuit,
                          @Nullable Boolean ignoreApply,
                          @Nullable Boolean ignoreUnApply,

                          @Nullable EffectPlaceholderableRandomRange<Float> x,
                          @Nullable EffectPlaceholderableRandomRange<Float> y,
                          @Nullable EffectPlaceholderableRandomRange<Float> z,
                          @Nullable EffectPlaceholderableRandomRange<Float> yaw,
                          @Nullable EffectPlaceholderableRandomRange<Float> pitch,
                          @Nullable EffectPlaceholderableString world) {
        super(TieredObjectEffectImpl.TELEPORT, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, x, y, z, yaw, pitch, world);
    }

    @Override
    public @Nullable Location applyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        Location location = data.getLocation(target.getKey());

        double x = this.getX() != null ? this.getX().getRandom(data, targetEntity) : location.getX();
        double y = this.getY() != null ? this.getY().getRandom(data, targetEntity) : location.getY();
        double z = this.getZ() != null ? this.getZ().getRandom(data, targetEntity) : location.getZ();
        float yaw = this.getYaw() != null ? this.getYaw().getRandom(data, targetEntity) : location.getYaw();
        float pitch = this.getPitch() != null ? this.getPitch().getRandom(data, targetEntity) : location.getPitch();
        World world = this.getWorldName() != null ? Bukkit.getWorld(this.getWorldName().getString(data, targetEntity)) : location.getWorld();

        Location newLocation = new Location(world, x, y, z, yaw, pitch);
        targetEntity.teleport(newLocation);
        return location.clone();
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull Location savedValue) {
        target.getValue().teleport(savedValue);
    }
}
