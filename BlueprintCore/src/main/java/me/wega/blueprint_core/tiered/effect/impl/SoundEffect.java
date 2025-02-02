package me.wega.blueprint_core.tiered.effect.impl;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_core.tiered.effect.type.ViewableTieredObjectEffect;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will play a sound at the target location for the viewers.
 */
@Getter
public class SoundEffect extends ViewableTieredObjectEffect<Pair<Audience, Sound>> {
    private final @NotNull EffectPlaceholderableString stringSound;
    private final @Nullable EffectPlaceholderableRandomRange<Float> volume;
    private final @Nullable EffectPlaceholderableRandomRange<Float> pitch;

    public SoundEffect(@NotNull TargetType @Nullable [] targetTypes,
                       @Nullable Boolean propagate,
                       @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                       @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                       @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                       @Nullable Boolean stopOnDeath,
                       @Nullable Boolean stopOnQuit,
                       @Nullable Boolean ignoreApply,
                       @Nullable Boolean ignoreUnApply,
                       @NotNull ViewerType @Nullable [] viewers,
                       @JsonField("sound") @NotNull EffectPlaceholderableString stringSound,
                       @JsonField("volume") @Nullable EffectPlaceholderableRandomRange<Float> volume,
                       @JsonField("pitch") @Nullable EffectPlaceholderableRandomRange<Float> pitch) {
        super(TieredObjectEffectImpl.SOUND, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, viewers);
        this.stringSound = stringSound;
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public @Nullable Pair<Audience, Sound> applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        Location location = data.getLocation(target.getKey());
        float volume = this.volume == null ? 1 : this.volume.getRandom(data, targetEntity);
        float pitch = this.pitch == null ? 1 : this.pitch.getRandom(data, targetEntity);
        String sSound = this.stringSound.getString(data, targetEntity);
        Sound sound = Sound.sound(Key.key(sSound), Sound.Source.PLAYER, volume, pitch);
        Audience audience = Audience.audience(this.getOnlyPlayerViewers(data, target));
        audience.playSound(sound, location.getX(), location.getY(), location.getZ());
        return Pair.of(audience, sound);
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull Pair<Audience, Sound> savedValue) {
        savedValue.getLeft().stopSound(savedValue.getRight());
    }
}
