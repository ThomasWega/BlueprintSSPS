package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a number that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableNumber<T extends Number> {
    private final @NotNull PlaceholderableNumber<T> internalNumber;

    public @NotNull T getNumber(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalNumber.getNumber(parser, data.getPlaceholders());
    }
}
