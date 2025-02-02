package me.wega.blueprint_toolkit.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public class FAWEUtils {
    private static final WorldEditPlugin PLUGIN = WorldEditPlugin.getInstance();

    /**
     * Get the region selection of the player
     *
     * @param player Player to get the region from
     * @return Optional of the selected region
     * @throws IncompleteRegionException if the region is incomplete
     */
    public static Optional<Region> getSelectedRegion(@NotNull Player player) throws IncompleteRegionException {
        LocalSession session = PLUGIN.getSession(player);
        if (session == null) return Optional.empty();
        return Optional.ofNullable(session.getSelection(session.getSelectionWorld()));
    }
}
