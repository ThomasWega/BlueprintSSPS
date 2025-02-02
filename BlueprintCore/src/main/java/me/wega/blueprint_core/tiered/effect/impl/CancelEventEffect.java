package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will cancel the event in the {@link TieredObjectEffectData}.
 */
public class CancelEventEffect extends TieredObjectEffect<Boolean> {

    public CancelEventEffect(@NotNull TargetType @Nullable [] targetTypes,
                             @Nullable Boolean propagate,
                             @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                             @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                             @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                             @Nullable Boolean stopOnDeath,
                             @Nullable Boolean stopOnQuit,
                             @Nullable Boolean ignoreApply,
                             @Nullable Boolean ignoreUnApply) {
        super(TieredObjectEffectImpl.CANCEL_EVENT, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
    }

    @Override
    public @Nullable Boolean applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Event event = data.getEvent();
        if (event instanceof Cancellable cancellable)
            cancellable.setCancelled(true);

        return event instanceof Cancellable;
    }
}
