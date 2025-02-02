package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableString {
    private final @NotNull PlaceholderableString internalString;

    public @NotNull String getString(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalString.getString(parser, data.getPlaceholders());
    }
}
