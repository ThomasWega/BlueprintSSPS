package me.wega.blueprint_core.tiered.effect.handler;

import me.wega.blueprint_core.BlueprintCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class TieredObjectEffectListener implements Listener {

    public TieredObjectEffectListener() {
        Bukkit.getPluginManager().registerEvents(this, BlueprintCore.instance);
    }

    /**
     * Remove all effects that are set to stop on death
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        TieredObjectEffectHandler.getScheduled(player).values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.getEffect().isStopOnDeath())
                .forEach(entry -> TieredObjectEffectHandler.unApply(entry.getData(), true, true, entry.getEffect()));
    }

    /**
     * Remove all effects that are set to stop on death
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TieredObjectEffectHandler.getScheduled(player).values().stream()
                .flatMap(List::stream)
                .filter(entry -> entry.getEffect().isStopOnDeath())
                .forEach(entry -> TieredObjectEffectHandler.unApply(entry.getData(), true, true, entry.getEffect()));
    }
}