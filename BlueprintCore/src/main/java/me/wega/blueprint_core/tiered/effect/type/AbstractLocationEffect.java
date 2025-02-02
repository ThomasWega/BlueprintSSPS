package me.wega.blueprint_core.tiered.effect.type;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an effect that uses information about {@link Location}.
 */
@Getter
public abstract class AbstractLocationEffect<T> extends TieredObjectEffect<T> {
    private final @Nullable EffectPlaceholderableRandomRange<Float> x;
    private final @Nullable EffectPlaceholderableRandomRange<Float> y;
    private final @Nullable EffectPlaceholderableRandomRange<Float> z;
    private final @Nullable EffectPlaceholderableRandomRange<Float> yaw;
    private final @Nullable EffectPlaceholderableRandomRange<Float> pitch;
    private final @Nullable EffectPlaceholderableString worldName;

    public AbstractLocationEffect(@NotNull TieredObjectEffectImpl impl,
                                  @NotNull TargetType @Nullable [] targetTypes,
                                  @Nullable Boolean propagate,
                                  @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                  @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                  @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                  @Nullable Boolean stopOnDeath,
                                  @Nullable Boolean stopOnQuit,
                                  @Nullable Boolean ignoreApply,
                                  @Nullable Boolean ignoreUnApply,

                                  @JsonField("x") @Nullable EffectPlaceholderableRandomRange<Float> x,
                                  @JsonField("y") @Nullable EffectPlaceholderableRandomRange<Float> y,
                                  @JsonField("z") @Nullable EffectPlaceholderableRandomRange<Float> z,
                                  @JsonField("yaw") @Nullable EffectPlaceholderableRandomRange<Float> yaw,
                                  @JsonField("pitch") @Nullable EffectPlaceholderableRandomRange<Float> pitch,
                                  @JsonField("world") @Nullable EffectPlaceholderableString worldName) {
        super(impl, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.worldName = worldName;
    }
}
