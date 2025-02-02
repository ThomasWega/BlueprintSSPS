package me.wega.blueprint_core.raytrace;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a custom ray tracing implementation for Bukkit/Spigot.
 * This class allows for sophisticated ray tracing in a Minecraft world,
 * with customizable parameters for collision detection with blocks and entities.
 * All distance measurements are in Minecraft blocks.
 */
public class CustomRayTrace {
    private final @NotNull Entity origin;
    private final @NotNull World world;
    private final @NotNull Vector start;
    private final @NotNull Vector direction;
    private final double maxDistance;
    private final double step;
    private final double rayLength;
    private final double rayWidth;
    private final double rayHeight;
    private final @Nullable Predicate<@NotNull RayTraceResult> filter;
    private final int entityLimit;
    private final boolean checkEntities;
    private final @Nullable Consumer<@NotNull Location> stepAction;

    /**
     * Constructs a new CustomRayTrace instance.
     *
     * @param origin        The entity that the ray trace is originating from.
     * @param start         The starting location of the ray.
     * @param direction     The direction vector of the ray.
     * @param maxDistance   The maximum distance the ray should travel, in blocks.
     * @param step          The step size for each iteration of the ray trace, in blocks.
     * @param rayLength     The length of the ray for entity collision detection, in blocks.
     * @param rayWidth      The length of the ray for entity collision detection, in blocks.
     * @param rayHeight     The length of the ray for entity collision detection, in blocks.
     * @param filter        A function that determines whether a location should be considered as a collision
     * @param checkEntities Whether to check for entity collisions
     * @param entityLimit   The maximum number of entities that can be hit by the ray
     * @param stepAction    A consumer that is called for each step of the ray trace.
     */
    public CustomRayTrace(@NotNull Entity origin,
                          @NotNull Location start,
                          @NotNull Vector direction,
                          double maxDistance,
                          double step,
                          double rayLength,
                          double rayWidth,
                          double rayHeight,
                          @Nullable Predicate<@NotNull RayTraceResult> filter,
                          boolean checkEntities,
                          int entityLimit,
                          @Nullable Consumer<@NotNull Location> stepAction) {
        this.origin = origin;
        this.world = start.getWorld();
        this.start = start.toVector();
        this.direction = direction.normalize();
        this.maxDistance = maxDistance;
        this.step = step;
        this.rayLength = rayLength;
        this.rayWidth = rayWidth;
        this.rayHeight = rayHeight;
        this.filter = filter;
        this.entityLimit = entityLimit;
        this.checkEntities = checkEntities;
        this.stepAction = stepAction;
    }

    /**
     * Performs the ray trace operation.
     *
     * @return A RayTraceResult containing information about entities hit and the final block location.
     */
    public @NotNull RayTraceResult trace() {
        Set<LivingEntity> hitEntities = new HashSet<>();
        Block hitBlock = null;
        Vector current = start.clone();
        Location finalLoc = current.toLocation(world);

        for (double distance = 0; distance <= maxDistance; distance += step) {
            current.add(direction.clone().multiply(step));
            Location loc = current.toLocation(world);
            finalLoc = loc;

            // Call stepAction if it is not null
            if (this.stepAction != null) {
                this.stepAction.accept(loc);
            }

            // Skip collision detection if ignoreAllCollisions is true
            // Check if the location should be considered for collision detection
            if (this.filter != null && this.filter.test(new RayTraceResultImpl(hitEntities, loc.getBlock(), loc))) {
                hitBlock = loc.getBlock();
                break;
            }

            // Check for entity collision
            if (checkEntities) {
                // FIXME
                //   I don't really know how to fix this.
                //   Having the length and stuff would mean that theres some complex maths behind this that doesn't work with the getNearbyEntities method
                for (Entity entity : world.getNearbyEntities(loc, rayLength, rayHeight, rayWidth, entity ->
                        !entity.equals(origin)
                                && entity instanceof LivingEntity
                                && !hitEntities.contains(entity))) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    hitEntities.add(livingEntity);
                    if (hitEntities.size() >= entityLimit) {
                        finalLoc = livingEntity.getLocation(); // Update final location to the last hit entity's location
                        break;
                    }
                }
                if (hitEntities.size() >= entityLimit) break;
            }
        }

        return new RayTraceResultImpl(hitEntities, hitBlock, finalLoc);
    }

    /**
     * Checks if a point intersects with a living entity's bounding box.
     *
     * @param point  The point to check.
     * @param entity The entity to check against.
     * @return true if the point intersects with the entity's bounding box, false otherwise.
     * @deprecated THIS IS PROBABLY NOT NEEDED???
     */
    @Deprecated
    private boolean intersects(Vector point, LivingEntity entity) {
        // Simple bounding box check
        // FIXME
        //  This doesn't work, but the method is deprecated and this is just a temporary fix
        double expandFactor = Math.max(rayLength, rayWidth) / 2;
        double yExpandFactor = rayHeight / 2;
        return point.isInAABB(
                entity.getBoundingBox().getMin().subtract(new Vector(expandFactor, yExpandFactor, expandFactor)),
                entity.getBoundingBox().getMax().add(new Vector(expandFactor, yExpandFactor, expandFactor))
        );
    }

    /**
     * Represents the result of a ray trace operation.
     */
    public interface RayTraceResult {
        /**
         * Gets the set of living entities hit by the ray.
         */
        @NotNull Set<@NotNull LivingEntity> getHitEntities();

        /**
         * Gets the location of the first non-transparent block hit by the ray, if any.
         */
        @Nullable Block getHitBlock();

        /**
         * Gets the location the ray ended at.
         */
        @NotNull Location getEndLocation();
    }

    @Getter
    @Setter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class RayTraceResultImpl implements RayTraceResult {
        private @NotNull Set<@NotNull LivingEntity> hitEntities;
        private @Nullable Block hitBlock;
        private @NotNull Location endLocation;
    }
}