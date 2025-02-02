package me.wega.blueprint_core.tiered.effect.impl.loc;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_core.tiered.effect.type.AbstractLocationEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;

/**
 * Represents an effect that will offset the location stored in the {@link TieredObjectEffectData} object.
 */
@Getter
public class LocOffsetEffect extends AbstractLocationEffect<LocOffsetEffect.LocOffset> {
    private static final Random RANDOM = new Random();
    private final @Nullable EffectPlaceholderableRandomRange<Float> minSpreadX;
    private final @Nullable EffectPlaceholderableRandomRange<Float> minSpreadY;
    private final @Nullable EffectPlaceholderableRandomRange<Float> minSpreadZ;
    private final @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadX;
    private final @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadY;
    private final @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadZ;

    public LocOffsetEffect(@NotNull TargetType @Nullable [] targetTypes,
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
                           @Nullable EffectPlaceholderableString world,
                           @JsonField("min-spread-x") @Nullable EffectPlaceholderableRandomRange<Float> minSpreadX,
                           @JsonField("min-spread-y") @Nullable EffectPlaceholderableRandomRange<Float> minSpreadY,
                           @JsonField("min-spread-z") @Nullable EffectPlaceholderableRandomRange<Float> minSpreadZ,
                           @JsonField("max-spread-x") @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadX,
                           @JsonField("max-spread-y") @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadY,
                           @JsonField("max-spread-z") @Nullable EffectPlaceholderableRandomRange<Float> maxSpreadZ) {
        super(TieredObjectEffectImpl.LOC_OFFSET, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, x, y, z, yaw, pitch, world);
        this.minSpreadX = minSpreadX;
        this.minSpreadY = minSpreadY;
        this.minSpreadZ = minSpreadZ;
        this.maxSpreadX = maxSpreadX;
        this.maxSpreadY = maxSpreadY;
        this.maxSpreadZ = maxSpreadZ;
    }

    @Override
    public @Nullable LocOffset applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();

        float offsetX = calculateOffset(this.getX(), this.getMinSpreadX(), this.getMaxSpreadX(), data, targetEntity);
        float offsetY = calculateOffset(this.getY(), this.getMinSpreadY(), this.getMaxSpreadY(), data, targetEntity);
        float offsetZ = calculateOffset(this.getZ(), this.getMinSpreadZ(), this.getMaxSpreadZ(), data, targetEntity);
        float yaw = this.getYaw() != null ? this.getYaw().getRandom(data, targetEntity) : 0;
        float pitch = this.getPitch() != null ? this.getPitch().getRandom(data, targetEntity) : 0;

        Location newLoc = data.getLocation(target.getKey()).clone().add(offsetX, offsetY, offsetZ);

        World world = this.getWorldName() != null ? Bukkit.getWorld(this.getWorldName().getString(data, targetEntity)) : null;

        newLoc.setYaw(newLoc.getYaw() + yaw);
        newLoc.setPitch(newLoc.getPitch() + pitch);

        data.setLocation(newLoc);
        return new LocOffset(offsetX, offsetY, offsetZ, yaw, pitch, world);
    }

    private float calculateOffset(@Nullable EffectPlaceholderableRandomRange<Float> offset,
                                  @Nullable EffectPlaceholderableRandomRange<Float> minSpread,
                                  @Nullable EffectPlaceholderableRandomRange<Float> maxSpread,
                                  @NotNull TieredObjectEffectData data,
                                  @NotNull Entity targetEntity) {
        float result = offset != null ? offset.getRandom(data, targetEntity) : 0;

        if (minSpread != null && maxSpread != null) {
            float min = minSpread.getRandom(data, targetEntity);
            float max = maxSpread.getRandom(data, targetEntity);
            result += min + RANDOM.nextFloat() * (max - min);
            result *= (RANDOM.nextBoolean() ? 1 : -1);
        }

        return result;
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull LocOffset savedValue) {
        Location oldLoc = data.getLocation(target.getKey()).clone().subtract(savedValue.offsetX(), savedValue.offsetY(), savedValue.offsetZ());
        oldLoc.setYaw(oldLoc.getYaw() - savedValue.yaw());
        oldLoc.setPitch(oldLoc.getPitch() - savedValue.pitch());

        if (savedValue.oldWorld != null)
            oldLoc.setWorld(savedValue.oldWorld);

        data.setLocation(oldLoc);
    }

    public record LocOffset(float offsetX, float offsetY, float offsetZ, float yaw, float pitch,
                            @Nullable World oldWorld) {
    }
}