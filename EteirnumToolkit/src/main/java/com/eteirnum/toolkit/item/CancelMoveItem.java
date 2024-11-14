package com.eteirnum.toolkit.item;

import com.eteirnum.toolkit.EteirnumToolkit;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class to cancel moving items based on predicates.
 */
@UtilityClass
public class CancelMoveItem {

    static {
        Bukkit.getPluginManager().registerEvents(new Listeners(), EteirnumToolkit.instance);
    }

    private static final Set<Predicate<ItemStack>> ITEM_PREDICATES = new HashSet<>();
    // more performant than using itemstack predicates, because we can use the itemmeta directly
    private static final Set<Predicate<ItemMeta>> META_PREDICATES = new HashSet<>();

    /**
     * Register a predicate that will be used to check if an item should be cancelled when moved.
     * @param predicate the predicate to register
     * @see #registerMeta(Predicate)
     */
    public static void registerItem(@NotNull Predicate<@NotNull ItemStack> predicate) {
        ITEM_PREDICATES.add(predicate);
    }

    /**
     * Register a predicate that will be used to check if an item should be cancelled when moved.
     * More performant than using {@link ItemStack} predicates, because we can use the {@link ItemMeta} directly.
     * @param predicate the predicate to register
     */
    public static void registerMeta(@NotNull Predicate<@NotNull ItemMeta> predicate) {
        META_PREDICATES.add(predicate);
    }

    public static void unregisterItem(@NotNull Predicate<@NotNull ItemStack> predicate) {
        ITEM_PREDICATES.remove(predicate);
    }

    public static void unregisterMeta(@NotNull Predicate<@NotNull ItemMeta> predicate) {
        META_PREDICATES.remove(predicate);
    }

    private static class Listeners implements Listener {

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onDrop(PlayerDropItemEvent event) {
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
            ItemStack dropItem = event.getItemDrop().getItemStack();
            ItemMeta dropItemMeta = dropItem.getItemMeta();
            if (META_PREDICATES.stream().anyMatch(predicate -> predicate.test(dropItemMeta))) {
                event.setCancelled(true);
                return;
            }
            if (ITEM_PREDICATES.stream().anyMatch(predicate -> predicate.test(dropItem))) {
                event.setCancelled(true);
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onClick(InventoryClickEvent event) {
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            ItemStack currentItem = event.getCurrentItem();
            ItemStack cursorItem = event.getCursor();
            if (currentItem != null && !currentItem.getType().isEmpty()) {
                if (currentItem.hasItemMeta()) {
                    ItemMeta currentItemMeta = currentItem.getItemMeta();
                    if (META_PREDICATES.stream().anyMatch(predicate -> predicate.test(currentItemMeta))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (ITEM_PREDICATES.stream().anyMatch(predicate -> predicate.test(currentItem))) {
                    event.setCancelled(true);
                }
            }
            if (!cursorItem.getType().isEmpty()) {
                if (cursorItem.hasItemMeta()) {
                    ItemMeta cursorMeta = cursorItem.getItemMeta();
                    if (META_PREDICATES.stream().anyMatch(predicate -> predicate.test(cursorMeta))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (ITEM_PREDICATES.stream().anyMatch(predicate -> predicate.test(cursorItem))) {
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
        private void onDrag(InventoryDragEvent event) {
            if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) return;
            ItemStack cursorItem = event.getOldCursor();
            if (!cursorItem.getType().isEmpty()) {
                if (cursorItem.hasItemMeta()) {
                    ItemMeta cursorMeta = cursorItem.getItemMeta();
                    if (META_PREDICATES.stream().anyMatch(predicate -> predicate.test(cursorMeta))) {
                        event.setCancelled(true);
                        return;
                    }
                }
                if (ITEM_PREDICATES.stream().anyMatch(predicate -> predicate.test(cursorItem))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
