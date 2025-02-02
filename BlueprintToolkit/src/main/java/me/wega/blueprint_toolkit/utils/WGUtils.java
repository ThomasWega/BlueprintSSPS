package me.wega.blueprint_toolkit.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for WorldGuard
 */
@UtilityClass
public class WGUtils {

    /**
     * Get the region at the given location
     *
     * @param loc Location to check
     * @return Region at the location
     */
    public static Optional<ProtectedRegion> getRegionAt(@NotNull Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

        return set.getRegions().stream()
                .max(Comparator.comparingInt(ProtectedRegion::getPriority))
                .stream().findFirst();
    }

    /**
     * Get the region at the given entity's location
     *
     * @param entity Entity to check
     * @return Region at the entity's location
     */
    public static Optional<ProtectedRegion> getRegionAt(@NotNull LivingEntity entity) {
        return getRegionAt(entity.getLocation());
    }

    /**
     * Get all regions
     *
     * @return All regions
     */
    public static Map<String, ProtectedRegion> getRegions() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().stream()
                .flatMap(regionManager -> regionManager.getRegions().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get all region names
     *
     * @return All region names
     */
    public static Set<String> getRegionNames() {
        return getRegions().keySet();
    }

    /**
     * Get all regions in the given world
     *
     * @param world World to check
     * @return All regions in the world
     */
    public static Map<String, ProtectedRegion> getRegions(@NotNull World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions();
    }

    /**
     * Get all region names in the given world
     *
     * @param world World to check
     * @return All region names in the world
     */
    public static Set<String> getRegionNames(@NotNull World world) {
        return getRegions(world).keySet();
    }

    /**
     * Get the region with the given ID in the given world
     *
     * @param world World to check
     * @param id    ID of the region
     * @return Region with the given ID
     */
    public static Optional<ProtectedRegion> getRegion(@NotNull World world, @NotNull String id) {
        return Optional.ofNullable(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(id));
    }

    /**
     * Get the region with the given ID
     *
     * @param id ID of the region
     * @return Region with the given ID
     */
    @SuppressWarnings("DataFlowIssue")
    public static Optional<ProtectedRegion> getRegion(@NotNull String id) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().stream()
                .filter(regionManager -> regionManager.hasRegion(id))
                .map(regionManager -> regionManager.getRegion(id))
                .findAny();
    }

    /**
     * Get the center location of the given region
     *
     * @param world  World of the region
     * @param region Region to get the center of
     * @return Center location of the region
     */
    public static @NotNull Location getRegionCenter(@NotNull World world, @NotNull ProtectedRegion region) {
        // Get top and bottom locations
        Location top = new Location(world, region.getMaximumPoint().x(), region.getMaximumPoint().y(), region.getMaximumPoint().z());
        Location bottom = new Location(world, region.getMinimumPoint().x(), region.getMinimumPoint().y(), region.getMinimumPoint().z());

        // Calculate and return center location
        return new Location(world, (bottom.getX() + top.getX()) / 2, (bottom.getY() + top.getY()) / 2, (bottom.getZ() + top.getZ()) / 2);
    }

    /**
     * Check if the given region is valid and still exists
     *
     * @param region Region to check
     * @return Whether the region is valid
     */
    public static boolean isValid(@NotNull ProtectedRegion region) {
        Optional<ProtectedRegion> optReg = getRegion(region.getId());
        return optReg.isPresent() && optReg.get().getMaximumPoint().equals(region.getMaximumPoint()) && optReg.get().getMinimumPoint().equals(region.getMinimumPoint());
    }
}
