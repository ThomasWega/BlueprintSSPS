package me.wega.blueprint_core.tiered.effect.impl.potion;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_toolkit.utils.PotionUtils;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will apply a potion effect to the target.
 */
@Getter
public class RemovePotionEffect extends TieredObjectEffect<PotionEffect> {
    private final @NotNull EffectPlaceholderableString stringEffectType;

    public RemovePotionEffect(@NotNull TargetType @Nullable [] targetTypes,
                              @Nullable Boolean propagate,
                              @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                              @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                              @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                              @Nullable Boolean stopOnDeath,
                              @Nullable Boolean stopOnQuit,
                              @Nullable Boolean ignoreApply,
                              @Nullable Boolean ignoreUnApply,
                              @JsonField("potion") @NotNull EffectPlaceholderableString stringEffectType) {
        super(TieredObjectEffectImpl.REMOVE_POTION, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.stringEffectType = stringEffectType;
    }

    @Override
    public @Nullable PotionEffect applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            this.reportLivingEntityIssue(data, target);
            return null;
        }
        String sEffectType = stringEffectType.getString(data, livingEntity);
        PotionEffectType effectType = PotionUtils.getPotionEffectType(sEffectType);
        if (effectType == null) {
            this.reportIssue(data, target, "Invalid potion effect type: " + sEffectType);
            return null;
        }

        PotionEffect effect = livingEntity.getPotionEffect(effectType);
        livingEntity.removePotionEffect(effectType);
        return effect;
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull PotionEffect savedValue) {
        Entity targetEntity = target.getValue();
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            this.reportLivingEntityIssue(data, target);
            return;
        }
        livingEntity.addPotionEffect(savedValue);
    }
}
