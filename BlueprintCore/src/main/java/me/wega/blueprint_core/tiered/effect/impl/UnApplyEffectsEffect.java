package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.handler.TieredObjectEffectHandler;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Represents an effect that will unapply all effects of the same object from the target.
 */
public class UnApplyEffectsEffect extends TieredObjectEffect<Void> {

    public UnApplyEffectsEffect(@NotNull TargetType @Nullable [] targetTypes,
                                @Nullable Boolean propagate,
                                @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                @Nullable Boolean stopOnDeath,
                                @Nullable Boolean stopOnQuit,
                                @Nullable Boolean ignoreApply,
                                @Nullable Boolean ignoreUnApply
    ) {
        super(TieredObjectEffectImpl.UNAPPLY_EFFECTS, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
    }

    @Override
    public @Nullable Void applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        TieredObjectEffectHandler.getScheduled(targetEntity).values().stream()
                .flatMap(List::stream)
                .filter(scheduledEffectData -> scheduledEffectData.getData().getObjectInstance().getObject().getId().equals(data.getObjectInstance().getObject().getId()))
                .forEach(scheduledEffectData -> TieredObjectEffectHandler.unApply(scheduledEffectData.getData(), true, false, scheduledEffectData.getEffect()));
        return null;
    }
}
