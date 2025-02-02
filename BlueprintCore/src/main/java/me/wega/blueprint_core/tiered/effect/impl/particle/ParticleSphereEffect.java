package me.wega.blueprint_core.tiered.effect.impl.particle;

import lombok.Getter;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import me.wega.blueprint_core.tiered.effect.type.AbstractParticleEffect;
import me.wega.blueprint_toolkit.shaded.particlenativeapi.api.packet.ParticlePacket;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an effect that will draw a sphere of particles at the target location for the viewers.
 */
@Getter
public class ParticleSphereEffect extends AbstractParticleEffect<List<ParticlePacket>> {
    private final @NotNull EffectPlaceholderableRandomRange<Float> radius;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> particlesCount;
    private final boolean fill;
    private final @Nullable EffectPlaceholderableRandomRange<Float> density;

    public ParticleSphereEffect(@NotNull TargetType @Nullable [] targetTypes,
                                @Nullable Boolean propagate,
                                @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                                @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                                @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                                @Nullable Boolean stopOnDeath,
                                @Nullable Boolean stopOnQuit,
                                @Nullable Boolean ignoreApply,
                                @Nullable Boolean ignoreUnApply,

                                @NotNull ViewerType @Nullable [] viewers,
                                @NotNull ParticleData particleData,
                                @JsonField("radius") @NotNull EffectPlaceholderableRandomRange<Float> radius,
                                @JsonField("particles") @Nullable EffectPlaceholderableRandomRange<Integer> particlesCount,
                                @JsonField("fill") @Nullable Boolean fill,
                                @JsonField("density") @Nullable EffectPlaceholderableRandomRange<Float> density) {
        super(TieredObjectEffectImpl.PARTICLE_SPHERE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, viewers, particleData);
        this.radius = radius;
        this.particlesCount = particlesCount;
        this.fill = (fill != null) ? fill : false;
        this.density = density;
    }


    @Override
    public @Nullable List<ParticlePacket> applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Location loc = data.getLocation(target.getKey());
        Entity targetEntity = target.getValue();
        List<ParticlePacket> packets = new ArrayList<>();

        List<Player> viewers = this.getOnlyPlayerViewers(data, target);

        float radius = this.radius.getRandom(data, targetEntity);

        if (!fill) {
            // Sphere surface drawing logic
            int particlesCount = (this.particlesCount == null) ? 1 : this.particlesCount.getRandom(data, targetEntity);
            for (int i = 0; i < particlesCount; i++) {
                double phi = Math.acos(1 - 2 * Math.random());
                double theta = 2 * Math.PI * Math.random();
                double x = loc.getX() + (radius * Math.sin(phi) * Math.cos(theta));
                double y = loc.getY() + (radius * Math.sin(phi) * Math.sin(theta));
                double z = loc.getZ() + (radius * Math.cos(phi));
                Location finalLoc = new Location(loc.getWorld(), x, y, z);

                ParticlePacket particlePacket = this.createParticlePacket(data, finalLoc, target.getValue());
                particlePacket.sendTo(viewers);
                packets.add(particlePacket);
            }
        } else {
            // Fill logic
            float density = (this.density == null) ? 0.4f : this.density.getRandom(data, targetEntity);
            for (double x = -radius; x <= radius; x += density) {
                for (double y = -radius; y <= radius; y += density) {
                    for (double z = -radius; z <= radius; z += density) {
                        if (x * x + y * y + z * z <= radius * radius) {
                            double worldX = loc.getX() + x;
                            double worldY = loc.getY() + y;
                            double worldZ = loc.getZ() + z;
                            Location finalLoc = new Location(loc.getWorld(), worldX, worldY, worldZ);

                            ParticlePacket particlePacket = this.createParticlePacket(data, finalLoc, target.getValue());
                            particlePacket.sendTo(viewers);
                            packets.add(particlePacket);
                        }
                    }
                }
            }
        }
        return packets;
    }
}