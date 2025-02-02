package me.wega.blueprint_core.tiered.effect.impl.loc;

import lombok.Getter;
import me.wega.blueprint_core.config.MaterialGroups;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will save the location the target is looking at in the {@link TieredObjectEffectData}.
 */
@Getter
public class LookLocEffect extends TieredObjectEffect<Location> {
    private final @Nullable EffectPlaceholderableRandomRange<Integer> maxDistance;

    public LookLocEffect(@NotNull TargetType @Nullable [] targetTypes,
                         @Nullable Boolean propagate,
                         @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                         @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                         @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                         @Nullable Boolean stopOnDeath,
                         @Nullable Boolean stopOnQuit,
                         @Nullable Boolean ignoreApply,
                         @Nullable Boolean ignoreUnApply,
                         @JsonField("max-distance") @Nullable EffectPlaceholderableRandomRange<Integer> maxDistance) {
        super(TieredObjectEffectImpl.LOOK_LOC, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.maxDistance = maxDistance;
    }

    @Override
    public @Nullable Location applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            this.reportLivingEntityIssue(data, target);
            return null;
        }
        int maxDistance = this.maxDistance != null ? this.maxDistance.getRandom(data, livingEntity) : 10;
        Location lookLoc = livingEntity.getLastTwoTargetBlocks(MaterialGroups.TRANSPARENT_MATERIALS, maxDistance).get(1).getLocation();
        data.setLocation(lookLoc);
        return lookLoc;
    }
}
