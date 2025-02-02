package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.handler.TieredObjectEffectHandler;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents an effect that will apply a set of effects to entities in range of the target location.
 */
public class InRangeEffect extends TieredObjectEffect<Collection<LivingEntity>> {
    private final @NotNull EffectPlaceholderableRandomRange<Float> radius;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> entityLimit;
    private final boolean ignoreInvoker;
    private final @NotNull TieredObjectEffect<?> @NotNull [] effects;

    public InRangeEffect(@NotNull TargetType @Nullable [] targetTypes,
                         @Nullable Boolean propagate,
                         @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                         @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                         @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                         @Nullable Boolean stopOnDeath,
                         @Nullable Boolean stopOnQuit,
                         @Nullable Boolean ignoreApply,
                         @Nullable Boolean ignoreUnApply,
                         @JsonField("radius") @NotNull EffectPlaceholderableRandomRange<Float> radius,
                         @JsonField("entity-limit") @Nullable EffectPlaceholderableRandomRange<Integer> entityLimit,
                         @JsonField("ignore-invoker") @Nullable Boolean ignoreInvoker,
                         @JsonField("effects") @NotNull TieredObjectEffect<?> @NotNull [] effects) {
        super(TieredObjectEffectImpl.IN_RANGE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.radius = radius;
        this.entityLimit = entityLimit;
        this.ignoreInvoker = (ignoreInvoker != null) ? ignoreInvoker : false;
        this.effects = effects;
    }

    @Override
    public @Nullable Collection<LivingEntity> applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        TargetType targetType = target.getKey();
        Location loc = data.getLocation(targetType);
        float radius = this.radius.getRandom(data, targetEntity);
        List<LivingEntity> entities = new ArrayList<>(loc.getNearbyLivingEntities(radius));

        if (ignoreInvoker)
            entities.remove(data.getInvoker());

        if (entityLimit != null) {
            int limit = this.entityLimit.getRandom(data, targetEntity);
            if (entities.size() > limit)
                entities = entities.stream()
                        .skip(limit)
                        .toList();
        }

        data.setDepth(data.getDepth() + 1);

        data.getPlaceholders().put("%in_range_entity_count%", String.valueOf(entities.size()));
        for (int i = 0; i < entities.size(); i++) {
            data.getPlaceholders().put("%in_range_entity_index%", String.valueOf(i));
            data.setTarget(entities.get(i));
            TieredObjectEffectHandler.handleApply(targetType, data, effects);
        }

        data.setDepth(data.getDepth() - 1);

        return entities;
    }
}
