package me.wega.blueprint_core.tiered;

import me.wega.blueprint_core.BlueprintCore;
import me.wega.blueprint_core.tiered.tier.TieredObjectTier;
import me.wega.blueprint_toolkit.builder.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generator for {@link TieredObjectInstance} instances.
 */
public abstract class TieredObjectStackGen<T extends TieredObjectInstance<?>> {

    /**
     * Gets the instance of the tiered object for the given item stack.
     *
     * @param itemStack The item stack to get the instance for.
     * @return The instance of the tiered object for the given item stack or null if the item stack is not a tiered object.
     */
    public abstract @Nullable T getInstance(@NotNull TieredObjectImpl impl, @NotNull String id, int tierNum, @NotNull ItemStack itemStack);

    /**
     * Gets the item stack for the given tiered object instance.
     *
     * @param object The tiered object instance to get the item stack for.
     * @return The item stack for the given tiered object instance.
     */
    public @NotNull ItemStack getItemStack(@NotNull T object) {
        return getItemBuilder(object).build();
    }

    /**
     * Gets the item builder for the given tiered object instance.
     *
     * @param object The tiered object instance to get the item builder for.
     * @return The item builder for the given tiered object instance.
     * @implNote Default implementation already sets the tier, id, and impl persistent data container keys,
     * so on override, make sure to call super.
     */
    protected @NotNull ItemBuilder getItemBuilder(@NotNull T object) {
        TieredObjectTier tier = object.getTier();
        int tierNum = object.getTierNum();

        return new ItemBuilder(tier.getItemStack())
                .unbreakable(true)
                .hideFlags()
                .pdcKey(TieredObjectKey.ID, object.getObject().getId())
                .pdcKey(TieredObjectKey.IMPL, object.getObject().getImpl())
                .pdcKey(TieredObjectKey.TIER, tierNum);
    }

    /**
     * Gets the item stack for the given tiered object instance.
     *
     * @param object The tiered object instance to get the item stack for.
     * @return The item stack for the given tiered object instance.
     */
    @SuppressWarnings("unchecked")
    public static <T extends TieredObjectInstance<?>> @NotNull ItemStack getItemStackFor(T object) {
        TieredObjectStackGen<T> gen = (TieredObjectStackGen<T>) TieredObjectStackGenRegistry.getGenerator(object.getClass());
        return gen.getItemStack(object);
    }

    /**
     * Gets the instance of the tiered object for the given item stack.
     *
     * @param itemStack The item stack to get the instance for.
     * @return The instance of the tiered object for the given item stack or null if the item stack is not a tiered object.
     */
    public static <T extends TieredObjectInstance<?>> @Nullable T getInstanceFor(@NotNull ItemStack itemStack) {
        TieredObjectImpl impl = (TieredObjectImpl) TieredObjectKey.IMPL.get(itemStack.getItemMeta());
        if (impl == null) return null;
        return getInstanceFor(impl, itemStack);
    }

    /**
     * Gets the instance of the tiered object for the given item stack.
     *
     * @param impl      The implementation of the tiered object.
     * @param itemStack The item stack to get the instance for.
     * @return The instance of the tiered object for the given item stack or null if the item stack is not a tiered object.
     */
    @SuppressWarnings("unchecked")
    public static <T extends TieredObjectInstance<?>> @Nullable T getInstanceFor(@NotNull TieredObjectImpl impl, @NotNull ItemStack itemStack) {
        TieredObjectImpl itemImpl = (TieredObjectImpl) TieredObjectKey.IMPL.get(itemStack.getItemMeta());
        if (itemImpl == null) return null;
        if (!impl.equals(itemImpl)) return null;
        TieredObjectStackGen<T> gen = (TieredObjectStackGen<T>) TieredObjectStackGenRegistry.getGenerator(impl.getInstanceClass());
        String id = TieredObjectKey.ID.getString(itemStack.getItemMeta());
        int tier = TieredObjectKey.TIER.getInt(itemStack.getItemMeta());
        return gen.getInstance(impl, id, tier, itemStack);
    }

    /**
     * Gets the instance of the tiered object for the given item stack.
     *
     * @param clazz     The class of the tiered object instance.
     * @param itemStack The item stack to get the instance for.
     * @return The instance of the tiered object for the given item stack or null if the item stack is not a tiered object.
     */
    public static <T extends TieredObjectInstance<?>> @Nullable T getInstanceFor(@NotNull Class<T> clazz, @NotNull ItemStack itemStack) {
        TieredObjectImpl impl = (TieredObjectImpl) TieredObjectKey.IMPL.get(itemStack.getItemMeta());
        if (impl == null) return null;
        if (!impl.getInstanceClass().isAssignableFrom(clazz)) return null;
        return getInstanceFor(impl, itemStack);
    }

    /**
     * Default implementation of {@link TieredObjectStackGen} that does not support instance creation.
     * Is used as a fallback when no other generator is found and sometimes used on instances that do not require instance creation
     * and just need to be displayed
     *
     * @param <T> The type of tiered object instance.
     */
    public static class Default<T extends TieredObjectInstance<?>> extends TieredObjectStackGen<T> {
        @Override
        public @Nullable T getInstance(@NotNull TieredObjectImpl impl, @NotNull String id, int tierNum, @NotNull ItemStack itemStack) {
            BlueprintCore.instance.getLogger().severe("Default TieredObjectStackGen does not support instance creation. If instance creation is required, a custom TieredObjectStackGen must be implemented.");
            return null;
        }
    }
}
