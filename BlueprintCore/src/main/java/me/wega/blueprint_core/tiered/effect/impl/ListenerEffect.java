package me.wega.blueprint_core.tiered.effect.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.BlueprintCore;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.handler.TieredObjectEffectHandler;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableUUID;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents an effect that will listen to an event and apply effects when the event is triggered.
 */
public class ListenerEffect extends TieredObjectEffect<Listener> {
    private final @NotNull EventType eventType;
    private final @Nullable EffectPlaceholderableNumber<Integer> listenLimit;
    private final @NotNull TieredObjectEffect<?> @NotNull [] effects;
    private final @NotNull EffectPlaceholderableUUID @Nullable [] targetUUIDs;

    public ListenerEffect(@NotNull TargetType @Nullable [] targetTypes,
                          @Nullable Boolean propagate,
                          @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                          @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                          @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                          @Nullable Boolean stopOnDeath,
                          @Nullable Boolean stopOnQuit,
                          @Nullable Boolean ignoreApply,
                          @Nullable Boolean ignoreUnApply,
                          @JsonField("event") @NotNull EventType eventType,
                          @JsonField("listen-limit") @Nullable EffectPlaceholderableNumber<Integer> listenLimit,
                          @JsonField("effects") @NotNull TieredObjectEffect<?> @NotNull [] effects,
                          @JsonField("target-uuid") @NotNull EffectPlaceholderableUUID @Nullable [] targetUUIDs) {
        super(TieredObjectEffectImpl.LISTENER, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.eventType = eventType;
        this.listenLimit = listenLimit;
        this.effects = effects;
        this.targetUUIDs = targetUUIDs;
    }

    @Override
    public @Nullable Listener applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Listener listener = new Listener() {
        };
        Entity targetEntity = target.getValue();
        final int[] iterations = {0};
        Bukkit.getPluginManager().registerEvent(
                eventType.getEventClass(),
                listener,
                EventPriority.HIGH,
                (l, e) -> {
                    boolean result = this.eventType.getEventConsumer().apply(new EventData(this, e, data, targetEntity));
                    if (!result) return;

                    int limit = (this.listenLimit != null) ? this.listenLimit.getNumber(data, targetEntity) : 1;
                    if (iterations[0] >= limit) {
                        this.unApply(data);
                        return;
                    }
                    iterations[0]++;

                    data.setDepth(data.getDepth() + 1);
                    data.setEvent(e);
                    TieredObjectEffectHandler.handleApply(target.getKey(), data, this.effects);
                    data.setDepth(data.getDepth() - 1);

                }, BlueprintCore.instance, true
        );

        return listener;
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull Listener savedValue) {
        HandlerList.unregisterAll(savedValue);
    }


    @Getter
    @RequiredArgsConstructor
    public enum EventType {
        // FIXME DO DAMAGE EVENT
        PROJECTILE_HIT(ProjectileHitEvent.class, eventData -> {
            ProjectileHitEvent event = (ProjectileHitEvent) eventData.event;
            if (notCorrectInvoker(eventData, event.getEntity())) return false;
            LivingEntity target = (LivingEntity) event.getHitEntity();
            TieredObjectEffectData d = eventData.data;
            d.setLocation(target == null ? event.getEntity().getLocation() : target.getLocation());
            d.setTarget(target);
            return true;
        });

        private final @NotNull Class<? extends Event> eventClass;
        private final @NotNull Function<EventData, Boolean> eventConsumer;

        private static boolean notCorrectInvoker(@NotNull EventData data, @Nullable Entity eventInvoker) {
            if (data.listenerEffect.targetUUIDs == null)
                return !data.target.equals(eventInvoker);
            if (eventInvoker == null) return true;
            return Arrays.stream(data.listenerEffect.targetUUIDs)
                    .map(obj -> obj.getUUID(data.data, data.target))
                    .anyMatch(e -> e.equals(eventInvoker.getUniqueId()));
        }
    }

    private record EventData(@NotNull ListenerEffect listenerEffect, @NotNull Event event,
                             @NotNull TieredObjectEffectData data, @NotNull Entity target) {
    }
}
