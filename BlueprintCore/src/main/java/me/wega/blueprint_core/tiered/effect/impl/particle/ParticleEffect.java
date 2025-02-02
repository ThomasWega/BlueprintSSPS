package me.wega.blueprint_core.tiered.effect.impl.particle;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.type.AbstractParticleEffect;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.packet.ParticlePacket;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will spawn a particle at the target location for the viewers.
 */
@Getter
public class ParticleEffect extends AbstractParticleEffect<ParticlePacket> {

    public ParticleEffect(@NotNull TargetType @Nullable [] targetTypes,
                          @Nullable Boolean propagate,
                          @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                          @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                          @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                          @Nullable Boolean stopOnDeath,
                          @Nullable Boolean stopOnQuit,
                          @Nullable Boolean ignoreApply,
                          @Nullable Boolean ignoreUnApply,

                          @NotNull ViewerType @Nullable [] viewers,
                          @NotNull ParticleData particleData) {
        super(TieredObjectEffectImpl.PARTICLE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, viewers, particleData);
    }

    @Override
    public @Nullable ParticlePacket applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Location location = data.getLocation(target.getKey());
        ParticlePacket particlePacket = this.createParticlePacket(data, location, target.getValue());

        particlePacket.sendTo(this.getOnlyPlayerViewers(data, target));
        return particlePacket;
    }
}
