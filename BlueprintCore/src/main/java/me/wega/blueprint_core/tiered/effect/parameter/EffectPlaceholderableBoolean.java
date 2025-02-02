package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a string that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableBoolean {
    private final @NotNull PlaceholderableBoolean internalBoolean;

    public boolean getBoolean(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalBoolean.getBoolean(parser, data.getPlaceholders());
    }
}
