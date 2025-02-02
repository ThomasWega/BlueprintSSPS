package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will apply damage to the target entity.
 */
public class DamageEffect extends TieredObjectEffect<Float> {
    private final @Nullable EffectPlaceholderableRandomRange<Float> damage;

    public DamageEffect(@NotNull TargetType @Nullable [] targetTypes,
                        @Nullable Boolean propagate,
                        @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                        @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                        @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                        @Nullable Boolean stopOnDeath,
                        @Nullable Boolean stopOnQuit,
                        @Nullable Boolean ignoreApply,
                        @Nullable Boolean ignoreUnApply,
                        @JsonField("damage") @Nullable EffectPlaceholderableRandomRange<Float> damage) {
        super(TieredObjectEffectImpl.DAMAGE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.damage = damage;
    }


    @Override
    public @Nullable Float applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        LivingEntity invoker = data.getInvoker();
        Entity targetEntity = target.getValue();

        if (targetEntity instanceof LivingEntity livingEntity) {
            float damage = this.damage == null ? 1.0f : this.damage.getRandom(data, targetEntity);
            livingEntity.damage(damage, invoker);
            return damage;
        }
        return null;
    }
}
