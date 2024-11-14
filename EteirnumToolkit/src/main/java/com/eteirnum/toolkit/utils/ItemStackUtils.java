package com.eteirnum.toolkit.utils;

import de.tr7zw.nbtapi.NBT;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class ItemStackUtils {

    /**
     * Merges the ItemStacks from the given list to their maximum stack amount
     *
     * @param inputItems ItemStack to stack up
     * @return List of stacked up items
     */
    public static List<ItemStack> merge(@NotNull List<ItemStack> inputItems) {
        List<ItemStack> copiedItems = new ArrayList<>(inputItems);

        return new ArrayList<>(copiedItems.stream()
                .collect(Collectors.toMap(
                        itemStack -> itemStack,
                        Function.identity(),
                        (existing, replacement) -> {
                            ItemStack mergedItem = existing.clone();
                            mergedItem.setAmount(existing.getAmount() + replacement.getAmount());
                            return mergedItem;
                        }
                ))
                .values());
    }

    /**
     * Splits a collection of ItemStacks into individual ItemStacks, each with an amount of 1.
     *
     * @param stacks the collection of ItemStacks to split
     * @return a list of individual ItemStacks, each with an amount of 1
     */
    public @NotNull List<@NotNull ItemStack> split(@NotNull Collection<@Nullable ItemStack> stacks) {
        List<ItemStack> resultList = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            ItemStack clone = stack.clone();
            int amount = clone.getAmount();
            clone.setAmount(1);
            for (int i = 0; i < amount; i++)
                resultList.add(clone.clone());
        }

        return resultList;
    }

    /**
     * Converts an array of ItemStacks to a NBT string
     * @param itemStacks the ItemStack array to convert
     * @return the NBT string
     */
    public static @NotNull String toNBTStringArray(@Nullable ItemStack @NotNull [] itemStacks) {
        return NBT.itemStackArrayToNBT(itemStacks).toString();
    }

    /**
     * Converts an ItemStack to a NBT string
     * @param itemStack the ItemStack to convert
     * @return the NBT string
     * @see #toNBTStringArray(ItemStack[])
     */
    public static @NotNull String toNBTString(@NotNull ItemStack itemStack) {
        return NBT.itemStackToNBT(itemStack).toString();
    }

    /**
     * Converts a NBT string to an array of ItemStacks
     * @param itemString the NBT string to convert
     * @return the ItemStack array
     */
    public static @Nullable ItemStack fromNBTString(@NotNull String itemString) {
        return NBT.itemStackFromNBT(NBT.parseNBT(itemString));
    }

    /**
     * Converts a NBT string to an array of ItemStacks
     * @param itemString the NBT string to convert
     * @return the ItemStack array
     */
    public static @Nullable ItemStack @Nullable [] fromNBTStringArray(@NotNull String itemString) {
        return NBT.itemStackArrayFromNBT(NBT.parseNBT(itemString));
    }
}
