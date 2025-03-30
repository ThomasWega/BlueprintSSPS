package me.wega.blueprint_core.tiered.effect.impl.potion;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_toolkit.utils.PotionUtils;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will apply a potion effect to the target.
 */
@Getter
public class PotionEffect extends TieredObjectEffect<org.bukkit.potion.PotionEffect> {
    private final @NotNull EffectPlaceholderableString stringEffectType;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> durationTicks;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> amplifier;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;
    private final boolean extendDuration;

    public PotionEffect(@NotNull TargetType @Nullable [] targetTypes,
                        @Nullable Boolean propagate,
                        @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                        @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                        @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                        @Nullable Boolean stopOnDeath,
                        @Nullable Boolean stopOnQuit,
                        @Nullable Boolean ignoreApply,
                        @Nullable Boolean ignoreUnApply,
                        @JsonField("potion") @NotNull EffectPlaceholderableString stringEffectType,
                        @JsonField("duration-ticks") @Nullable EffectPlaceholderableRandomRange<Integer> durationTicks,
                        @JsonField("amplifier") @Nullable EffectPlaceholderableRandomRange<Integer> amplifier,
                        @JsonField("ambient") @Nullable Boolean ambient,
                        @JsonField("particles") @Nullable Boolean particles,
                        @JsonField("icon") @Nullable Boolean icon,
                        @JsonField("extend") @Nullable Boolean extendDuration) {
        super(TieredObjectEffectImpl.POTION, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.stringEffectType = stringEffectType;
        this.durationTicks = durationTicks;
        this.amplifier = amplifier;
        this.ambient = (ambient != null) ? ambient : false;
        this.particles = (particles != null) ? particles : true;
        this.icon = (icon != null) ? icon : true;
        this.extendDuration = (extendDuration != null) ? extendDuration : false;
    }

    @Override
    public @Nullable org.bukkit.potion.PotionEffect applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
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

        int durTicks = durationTicks != null ? durationTicks.getRandom(data, livingEntity) : 200;
        if (extendDuration) {
            org.bukkit.potion.PotionEffect currentEffect = livingEntity.getPotionEffect(effectType);
            if (currentEffect != null) {
                durTicks += currentEffect.getDuration();
            }
        }

        int amp = amplifier != null ? amplifier.getRandom(data, livingEntity) : 0;
        org.bukkit.potion.PotionEffect effect = new org.bukkit.potion.PotionEffect(effectType, durTicks, amp, ambient, particles, icon);
        livingEntity.addPotionEffect(effect);
        return effect;
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, org.bukkit.potion.@NotNull PotionEffect savedValue) {
        Entity targetEntity = target.getValue();
        if (!(targetEntity instanceof LivingEntity livingEntity)) {
            this.reportLivingEntityIssue(data, target);
            return;
        }
        livingEntity.removePotionEffect(savedValue.getType());
    }
}
