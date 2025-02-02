package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableUUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Represents an uuid that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableUUID {
    private final @NotNull PlaceholderableUUID internalUUID;

    public @NotNull UUID getUUID(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return internalUUID.getUUID(parser, data.getPlaceholders());
    }
}
