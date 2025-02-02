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
 * Represents an effect that will draw a circle of particles at the target location for the viewers.
 */
@Getter
public class ParticleCircleEffect extends AbstractParticleEffect<List<ParticlePacket>> {
    private final @NotNull EffectPlaceholderableRandomRange<Float> radius;
    private final @Nullable EffectPlaceholderableRandomRange<Integer> particlesCount;
    private final boolean fill;
    private final @Nullable EffectPlaceholderableRandomRange<Float> density;
    private final @Nullable EffectPlaceholderableRandomRange<Float> rotationX;
    private final @Nullable EffectPlaceholderableRandomRange<Float> rotationY;
    private final @Nullable EffectPlaceholderableRandomRange<Float> rotationZ;

    public ParticleCircleEffect(@NotNull TargetType @Nullable [] targetTypes,
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
                                @JsonField("density") @Nullable EffectPlaceholderableRandomRange<Float> density,
                                @JsonField("rotation-x") @Nullable EffectPlaceholderableRandomRange<Float> rotationX,
                                @JsonField("rotation-y") @Nullable EffectPlaceholderableRandomRange<Float> rotationY,
                                @JsonField("rotation-z") @Nullable EffectPlaceholderableRandomRange<Float> rotationZ) {
        super(TieredObjectEffectImpl.PARTICLE_CIRCLE, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply, viewers, particleData);
        this.radius = radius;
        this.particlesCount = particlesCount;
        this.fill = (fill != null) ? fill : false;
        this.density = density;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
    }


    @Override
    public @Nullable List<ParticlePacket> applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Location loc = data.getLocation(target.getKey());
        Entity targetEntity = target.getValue();
        List<ParticlePacket> packets = new ArrayList<>();
        List<Player> viewers = this.getOnlyPlayerViewers(data, target);

        float radius = this.radius.getRandom(data, targetEntity);
        float rotationXAngle = this.rotationX != null ? (float) Math.toRadians(this.rotationX.getRandom(data, targetEntity)) : 0;
        float rotationYAngle = this.rotationY != null ? (float) Math.toRadians(this.rotationY.getRandom(data, targetEntity)) : 0;
        float rotationZAngle = this.rotationZ != null ? (float) Math.toRadians(this.rotationZ.getRandom(data, targetEntity)) : 0;

        if (!fill) {
            int particlesCount = (this.particlesCount == null) ? 1 : this.particlesCount.getRandom(data, targetEntity);
            final double increment = (2 * Math.PI) / particlesCount;
            for (int i = 0; i < particlesCount; i++) {
                double angle = i * increment;
                double x = radius * Math.cos(angle);
                double y = radius * Math.sin(angle);
                double z = 0;

                // Apply rotations
                double[] rotated = rotatePoint(x, y, z, rotationXAngle, rotationYAngle, rotationZAngle);

                double worldX = loc.getX() + rotated[0];
                double worldY = loc.getY() + rotated[1];
                double worldZ = loc.getZ() + rotated[2];
                Location finalLoc = new Location(loc.getWorld(), worldX, worldY, worldZ);

                ParticlePacket particlePacket = this.createParticlePacket(data, finalLoc, target.getValue());
                particlePacket.sendTo(viewers);
                packets.add(particlePacket);
            }
        } else {
            float density = (this.density == null) ? 0.4f : this.density.getRandom(data, targetEntity);
            for (double x = -radius; x <= radius; x += density) {
                for (double y = -radius; y <= radius; y += density) {
                    if (x * x + y * y <= radius * radius) {
                        double[] rotated = rotatePoint(x, y, 0, rotationXAngle, rotationYAngle, rotationZAngle);

                        double worldX = loc.getX() + rotated[0];
                        double worldY = loc.getY() + rotated[1];
                        double worldZ = loc.getZ() + rotated[2];
                        Location finalLoc = new Location(loc.getWorld(), worldX, worldY, worldZ);

                        ParticlePacket particlePacket = this.createParticlePacket(data, finalLoc, target.getValue());
                        particlePacket.sendTo(viewers);
                        packets.add(particlePacket);
                    }
                }
            }
        }
        return packets;
    }

    private double[] rotatePoint(double x, double y, double z, double rotationX, double rotationY, double rotationZ) {
        // Rotate around X axis
        double tempY = y * Math.cos(rotationX) - z * Math.sin(rotationX);
        double tempZ = y * Math.sin(rotationX) + z * Math.cos(rotationX);

        // Rotate around Y axis
        double tempX = x * Math.cos(rotationY) + tempZ * Math.sin(rotationY);
        z = -x * Math.sin(rotationY) + tempZ * Math.cos(rotationY);

        // Rotate around Z axis
        x = tempX * Math.cos(rotationZ) - tempY * Math.sin(rotationZ);
        y = tempX * Math.sin(rotationZ) + tempY * Math.cos(rotationZ);

        return new double[]{x, y, z};
    }
}
