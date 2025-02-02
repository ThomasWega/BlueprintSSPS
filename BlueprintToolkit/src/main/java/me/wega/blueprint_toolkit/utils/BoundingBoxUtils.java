package me.wega.blueprint_toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for {@link BoundingBox}.
 */
@UtilityClass
public class BoundingBoxUtils {

    /**
     * Gets an amount of random locations within the bounding box
     * that are at least a certain distance apart.
     *
     * @param world the world
     * @param boundingBox the bounding box
     * @param count the number of locations to get
     * @param minDistance the minimum distance between the locations
     * @return a set of random locations within the bounding box
     */
    public static @NotNull Set<@NotNull Location> getRandomLocations(@Nullable World world, @NotNull BoundingBox boundingBox, int count, double minDistance) {
        Set<Location> locations = new HashSet<>();
        Random random = ThreadLocalRandom.current();
        int maxAttempts = count * 20;

        for (int attempts = 0; locations.size() < count && attempts < maxAttempts; attempts++) {
            Location newLoc = new Location(
                    world,
                    boundingBox.getMinX() + (boundingBox.getMaxX() - boundingBox.getMinX()) * random.nextDouble(),
                    boundingBox.getMinY() + (boundingBox.getMaxY() - boundingBox.getMinY()) * random.nextDouble(),
                    boundingBox.getMinZ() + (boundingBox.getMaxZ() - boundingBox.getMinZ()) * random.nextDouble()
            );

            if (locations.stream().noneMatch(loc -> loc.distance(newLoc) < minDistance)) {
                locations.add(newLoc);
            }
        }

        return locations;
    }
}
