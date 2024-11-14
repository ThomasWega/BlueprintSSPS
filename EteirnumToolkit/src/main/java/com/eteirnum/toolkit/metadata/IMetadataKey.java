package com.eteirnum.toolkit.metadata;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a metadata key.
 * @implSpec Most often this interface is implemented by an enum.
 */
public interface IMetadataKey {
    /**
     * @implSpec Create a static field and then just public getter for it!
     */
    @NotNull JavaPlugin getInstance();

    @NotNull String getKey();

    default String getString(@NotNull Metadatable meta) {
        return get(meta).toString();
    }

    default Integer getInt(@NotNull Metadatable meta) {
        return (Integer) get(meta);
    }

    default Long getLong(@NotNull Metadatable meta) {
        return (Long) get(meta);
    }

    default Double getDouble(@NotNull Metadatable meta) {
        return (Double) get(meta);
    }

    default Boolean getBoolean(@NotNull Metadatable meta) {
        return (Boolean) get(meta);
    }

    default void set(@NotNull Metadatable meta, Object value) {
        meta.setMetadata(getKey(), new FixedMetadataValue(getInstance(), value));
    }

    default void remove(@NotNull Metadatable meta) {
        meta.removeMetadata(getKey(), getInstance());
    }

    default @Nullable Object get(@NotNull Metadatable meta) {
        List<MetadataValue> values = meta.getMetadata(getKey());
        return values.isEmpty() ? null : values.get(0).value();
    }

    default boolean has(@NotNull Metadatable meta) {
        return meta.hasMetadata(getKey());
    }
}