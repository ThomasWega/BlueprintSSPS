package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableRandomRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a random range that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableRandomRange<T extends Number> {
    private final @NotNull PlaceholderableRandomRange<T> internalRange;

    public @NotNull T getMin(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalRange.getMin(parser, data.getPlaceholders());
    }

    public @NotNull T getMax(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalRange.getMax(parser, data.getPlaceholders());
    }

    public @NotNull T getRandom(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalRange.getRandom(parser, data.getPlaceholders());
    }
}
