package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableString;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will set a placeholder in the {@link TieredObjectEffectData} object.
 */
public class SetPlaceholderEffect extends TieredObjectEffect<Pair<String, String>> {
    private final @NotNull String key;
    private final @NotNull EffectPlaceholderableString value;
    private final boolean overwrite;

    public SetPlaceholderEffect(@NotNull TargetType @Nullable [] targetTypes,
                                @Nullable Boolean propagate,
                                @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                @Nullable Boolean stopOnDeath,
                                @Nullable Boolean stopOnQuit,
                                @Nullable Boolean ignoreApply,
                                @Nullable Boolean ignoreUnApply,

                                @JsonField("key") @NotNull String key,
                                @JsonField("value") @NotNull EffectPlaceholderableString value,
                                @JsonField("overwrite") @Nullable Boolean overwrite) {
        super(TieredObjectEffectImpl.SET_PLACEHOLDER, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.key = key;
        this.value = value;
        this.overwrite = (overwrite != null) ? overwrite : true;
    }

    @Override
    public @Nullable Pair<String, String> applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        if (!this.overwrite && data.getPlaceholders().containsKey(this.key))
            return null;
        String value = this.value.getString(data, target.getValue());
        data.getPlaceholders().put(this.key, value);
        return Pair.of(this.key, value);
    }

    @Override
    public void unApplyInternal(@NotNull TieredObjectEffectData data, Map.@NotNull Entry<@NotNull TargetType, @NotNull Entity> target, @NotNull Pair<String, String> savedValue) {
        String key = savedValue.getLeft();
        data.getPlaceholders().remove(key);
    }
}
