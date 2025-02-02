package me.wega.blueprint_core.tiered.effect.parameter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.wega.blueprint_core.particle.ParticleResolver;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_toolkit.placeholder.PlaceholderableString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a particle type that will be parsed with PAPI and additional placeholders from the {@link TieredObjectEffectData} object.
 */
@RequiredArgsConstructor
@Getter
public class EffectPlaceholderableParticleType {
    private final @NotNull PlaceholderableString internalString;

    public @NotNull Object getParticleType(@NotNull TieredObjectEffectData data, @Nullable Object parser) {
        return ParticleResolver.getParticleType(internalString.getString(parser, data.getPlaceholders()));
    }


}
