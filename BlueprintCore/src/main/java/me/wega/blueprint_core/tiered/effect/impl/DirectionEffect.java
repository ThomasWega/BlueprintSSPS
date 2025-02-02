package me.wega.blueprint_core.tiered.effect.impl;

import me.wega.blueprint_core.tiered.effect.TieredObjectEffect;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectData.TargetType;
import me.wega.blueprint_core.tiered.effect.TieredObjectEffectImpl;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableNumber;
import me.wega.blueprint_core.tiered.effect.parameter.EffectPlaceholderableRandomRange;
import me.wega.blueprint_core.tiered.effect.parameter.JsonField;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an effect that will apply a direction to the target entity.
 */
public class DirectionEffect extends TieredObjectEffect<Vector> {
    private final @Nullable EffectPlaceholderableRandomRange<Float> forwardMultiplier;
    private final @Nullable EffectPlaceholderableRandomRange<Float> upwardMultiplier;
    private final @Nullable EffectPlaceholderableRandomRange<Float> rightMultiplier;
    private final @Nullable EffectPlaceholderableRandomRange<Float> yawRotation;
    private final @Nullable EffectPlaceholderableRandomRange<Float> pitchRotation;
    private final boolean addToCurrentVelocity;
    private final boolean setOnTarget;

    public DirectionEffect(@NotNull TargetType @Nullable [] targetTypes,
                           @Nullable Boolean propagate,
                           @Nullable EffectPlaceholderableNumber<Integer> propagateDepth,
                           @NotNull EffectPlaceholderableNumber<Integer> @Nullable [] intervalTicks,
                           @Nullable EffectPlaceholderableNumber<Integer> repeatCount,
                           @Nullable Boolean stopOnDeath,
                           @Nullable Boolean stopOnQuit,
                           @Nullable Boolean ignoreApply,
                           @Nullable Boolean ignoreUnApply,
                           @JsonField("forward") @Nullable EffectPlaceholderableRandomRange<Float> forwardMultiplier,
                           @JsonField("upward") @Nullable EffectPlaceholderableRandomRange<Float> upwardMultiplier,
                           @JsonField("right") @Nullable EffectPlaceholderableRandomRange<Float> rightMultiplier,
                           @JsonField("yaw") @Nullable EffectPlaceholderableRandomRange<Float> yawRotation,
                           @JsonField("pitch") @Nullable EffectPlaceholderableRandomRange<Float> pitchRotation,
                           @JsonField("add-to-velocity") @Nullable Boolean addToCurrentVelocity,
                           @JsonField("set-on-target") @Nullable Boolean setOnTarget) {
        super(TieredObjectEffectImpl.DIRECTION, targetTypes, propagate, propagateDepth, intervalTicks, repeatCount, stopOnDeath, stopOnQuit, ignoreApply, ignoreUnApply);
        this.forwardMultiplier = forwardMultiplier;
        this.upwardMultiplier = upwardMultiplier;
        this.rightMultiplier = rightMultiplier;
        this.yawRotation = yawRotation;
        this.pitchRotation = pitchRotation;
        this.addToCurrentVelocity = (addToCurrentVelocity != null) ? addToCurrentVelocity : false;
        this.setOnTarget = (setOnTarget != null) ? setOnTarget : false;
    }

    @Override
    public @Nullable Vector applyInternal(@NotNull TieredObjectEffectData data, @NotNull Map.Entry<@NotNull TargetType, @NotNull Entity> target) {
        Entity targetEntity = target.getValue();
        Location targetLocation = data.getLocation(target.getKey());

        Vector direction = targetLocation.getDirection().clone();

        // Apply rotations
        if (yawRotation != null || pitchRotation != null) {
            float yawRot = yawRotation != null ? yawRotation.getRandom(data, targetEntity) : 0;
            float pitchRot = pitchRotation != null ? pitchRotation.getRandom(data, targetEntity) : 0;
            direction = rotateVector(direction, yawRot, pitchRot);
        }

        // Apply multipliers
        float upwardMult = upwardMultiplier != null ? upwardMultiplier.getRandom(data, targetEntity) : 1;
        float forwardMult = forwardMultiplier != null ? forwardMultiplier.getRandom(data, targetEntity) : 1;
        Vector result = new Vector(
                direction.getX() * forwardMult,
                direction.getY() * upwardMult,
                direction.getZ() * forwardMult
        );

        // Apply right direction
        if (rightMultiplier != null) {
            Vector rightDirection = rotateVectorRight(direction);
            result.add(rightDirection.multiply(rightMultiplier.getRandom(data, targetEntity)));
        }

        // Apply the velocity
        if (addToCurrentVelocity)
            result.add(targetLocation.getDirection().clone());

        // Set the velocity
        if (setOnTarget)
            targetEntity.setVelocity(result);

        data.setLocation(targetLocation.clone().add(result));
        return result;
    }

    private Vector rotateVector(Vector vector, double yaw, double pitch) {
        // Convert to radians
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double cosYaw = Math.cos(yawRad);
        double sinYaw = Math.sin(yawRad);
        double cosPitch = Math.cos(pitchRad);
        double sinPitch = Math.sin(pitchRad);

        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        // Rotate around Y-axis (yaw)
        double newX = x * cosYaw - z * sinYaw;
        double newZ = x * sinYaw + z * cosYaw;

        // Rotate around X-axis (pitch)
        double newY = y * cosPitch - newZ * sinPitch;
        newZ = y * sinPitch + newZ * cosPitch;

        return new Vector(newX, newY, newZ);
    }

    private Vector rotateVectorRight(Vector original) {
        double x = original.getX();
        double z = original.getZ();
        // Rotation transformation for 90 degrees right
        return new Vector(-z, original.getY(), x);
    }
}