package com.eteirnum.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class InvUtils {

    /**
     * Adds the items to the inventory on the first available slot (merging supported).
     *
     * @param currCont  the current inventory contents
     * @param addStacks the items to add
     * @return the new inventory contents
     * @implNote Inventory needs to be the size of any chest. PlayerInventory is not supported.
     */
    public static @Nullable ItemStack @NotNull [] merge(@Nullable ItemStack @NotNull [] currCont, @Nullable ItemStack @NotNull ... addStacks) {
        Inventory inventory = Bukkit.createInventory(null, currCont.length);
        inventory.setContents(currCont.clone());
        inventory.addItem(Arrays.stream(addStacks)
                .filter(Objects::nonNull)
                .map(ItemStack::clone)
                .toArray(ItemStack[]::new)
        );
        return inventory.getContents();
    }

    /**
     * Checks whether items can fit into the given inventory.
     *
     * @param inv   Inventory to run check for
     * @param items Items that need to fit
     * @return <strong>true</strong> whether all items can fit, otherwise <strong>false</strong>
     * @author <a href="https://github.com/ThomasWega">Tomáš Weglarz</a>
     */
    // why is this not made by new inventory with same contents and addItem method and then check if the returned map is empty?
    // > because you cannot create an inventory the same size as PlayerInventory and this might even be faster
    public static boolean canFit(@NotNull Inventory inv, @NotNull ItemStack... items) {
        final ItemStack[] contents = inv.getStorageContents().clone();
        for (ItemStack itemStack : items) {
            boolean itemInserted = false;

            for (int i = 0; i < contents.length; i++) {
                ItemStack current = contents[i];

                if (current == null) {
                    contents[i] = itemStack;
                    itemInserted = true;
                    break; // Empty slot, item fits
                }

                if (current.isSimilar(itemStack) && current.getAmount() + itemStack.getAmount() <= current.getMaxStackSize()) {
                    ItemStack currentClone = current.clone();
                    currentClone.setAmount(current.getAmount() + itemStack.getAmount());
                    contents[i] = currentClone;
                    itemInserted = true;
                    break; // Item can stack in the existing slot
                }
            }

            if (!itemInserted) {
                return false; // Item doesn't fit in any slot
            }
        }

        return true;
    }

    /**
     * Checks whether items can fit into the given inventory.
     *
     * @param inv   Inventory to run check for
     * @param items Items that need to fit
     * @return <strong>true</strong> whether all items can fit, otherwise <strong>false</strong>
     * @author <a href="https://github.com/ThomasWega">Tomáš Weglarz</a>
     */
    public static boolean canFit(Inventory inv, Collection<ItemStack> items) {
        return canFit(inv, items.toArray(ItemStack[]::new));
    }

    /**
     * Counts the number of empty slots in the given inventory.
     *
     * @param inv the inventory to check
     * @return the number of empty slots in the inventory
     */
    public static int getEmptySlots(Inventory inv) {
        return (int) Arrays.stream(inv.getContents())
                .filter(item -> item == null || item.getType() == Material.AIR)
                .count();
    }

    public static @NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> getMainItems(@NotNull LivingEntity player) {
        boolean filterBody = player instanceof Player;
        Map<EquipmentSlot, ItemStack> items = Arrays.stream(EquipmentSlot.values())
                // not valid for players
                .filter(equipmentSlot -> !filterBody || equipmentSlot != EquipmentSlot.BODY)
                .collect(Collectors.toMap(
                        slot -> slot,
                        slot -> player.getEquipment() != null ? player.getEquipment().getItem(slot) : new ItemStack(Material.AIR)
                ));

        Arrays.stream(EquipmentSlot.values()).forEach(slot ->
                items.replace(slot, new ItemStack(Material.AIR), null)
        );
        return items;
    }

    /**
     * Gets the slot number for the given equipment slot.
     *
     * @param player the holder of the equipment
     * @param slot   the equipment slot
     * @return the slot number
     */
    public static int getSlot(@NotNull Player player, @NotNull EquipmentSlot slot) {
        return switch (slot) {
            case HAND -> player.getInventory().getHeldItemSlot();
            case OFF_HAND -> 45;
            case FEET -> 8;
            case LEGS -> 7;
            case CHEST -> 6;
            case HEAD -> 5;
            default -> -1;
        };
    }

    /**
     * Gets the equipment slot for the given slot number.
     *
     * @param player the holder of the equipment
     * @param slot   the slot number
     * @return the equipment slot
     */
    public static @Nullable EquipmentSlot getEquipmentSlot(@NotNull Player player, int slot) {
        int mainHandSlot = player.getInventory().getHeldItemSlot();
        if (mainHandSlot == slot) return EquipmentSlot.HAND;

        return switch (slot) {
            case 45 -> EquipmentSlot.OFF_HAND;
            case 8 -> EquipmentSlot.FEET;
            case 7 -> EquipmentSlot.LEGS;
            case 6 -> EquipmentSlot.CHEST;
            case 5 -> EquipmentSlot.HEAD;
            default -> null;
        };
    }

    /**
     * Adds an item to the inventory and returns the slot where the item was added and the item that was replaced.
     *
     * @param inv  the inventory to add the item to
     * @param item the item to add
     * @return a pair containing the slot where the item was added and the item that was replaced
     */
    public static @NotNull Pair<@NotNull Integer, @Nullable ItemStack> addItemGetSlot(@NotNull Inventory inv, @NotNull ItemStack item) {
        ItemStack[] currentContents = inv.getContents();
        inv.addItem(item);
        ItemStack[] newContents = inv.getContents();
        for (int i = 0; i < currentContents.length; i++) {
            if (!Objects.equals(currentContents[i], newContents[i])) {
                inv.setItem(i, newContents[i]);
                return Pair.of(i, currentContents[i]);
            }
        }
        return Pair.of(-1, null);
    }

    /**
     * Converts the hotbar slot to the inventory slot.
     *
     * @param slot the hotbar slot
     * @return the inventory slot
     */
    public static int convertHotbarSlotToInvSlot(int slot) {
        return switch (slot) {
            case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> slot + 36;
            default -> -1;
        };
    }

    /**
     * Takes a specified amount of items from the player's inventory
     * This will take the items even if the player doesn't have enough items
     *
     * @param playerInventory The inventory of the player
     * @param items           The list of items to select from
     * @param amount          The amount of items to take
     * @return Whether if the player had enough items, true if the player had enough items
     */
    public static boolean takeItems(PlayerInventory playerInventory, List<ItemStack> items, int amount) {
        for (ItemStack item : items) {
            if (amount <= 0) {
                return true;
            }
            int itemAmount = item.getAmount();

            if (itemAmount <= amount) {
                amount -= itemAmount;
                playerInventory.remove(item);
            } else {
                item.setAmount(itemAmount - amount);
                return true;
            }
        }
        return false;
    }
}
