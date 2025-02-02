package me.wega.blueprint_core.tiered.effect.impl.loc;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.type.AbstractLocationEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will offset the location stored in the {@link TieredObjectEffectData} object.
 */
@Getter
public class LocSetEffect extends AbstractLocationEffect<Location> {

    public LocSetEffect(@NotNull TargetType @Nullable [] targetTypes,
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
        super(TieredObjectEffectImpl.LOC_SET, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, x, y, z, yaw, pitch, world);
    }

    @Override
    public @Nullable Location applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        Location currentLoc = data.getLocation(target.getKey());

        float x = this.getX() != null ? this.getX().getRandom(data, targetEntity) : (float) currentLoc.getX();
        float y = this.getY() != null ? this.getY().getRandom(data, targetEntity) : (float) currentLoc.getY();
        float z = this.getZ() != null ? this.getZ().getRandom(data, targetEntity) : (float) currentLoc.getZ();
        float yaw = this.getYaw() != null ? this.getYaw().getRandom(data, targetEntity) : currentLoc.getYaw();
        float pitch = this.getPitch() != null ? this.getPitch().getRandom(data, targetEntity) : currentLoc.getPitch();

        Location newLoc = new Location(currentLoc.getWorld(), x, y, z, yaw, pitch);
        data.setLocation(newLoc);
        return newLoc;
    }
}