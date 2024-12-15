package com.eteirnum.core.tiered;

import com.eteirnum.toolkit.data.DataMapManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A manager for {@link TieredObject} instances.
 */
public class TieredObjectManager extends DataMapManager<TieredObjectImpl, List<TieredObject<?>>> {

    public @Nullable TieredObject<?> get(@NotNull TieredObjectImpl impl, @NotNull String id) {
        List<TieredObject<?>> objects = this.get(impl);
        if (objects == null) return null;
        return objects.stream()
                .filter(object -> object.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public boolean add(@NotNull TieredObjectImpl key, @NotNull TieredObject<?> value) {
        List<TieredObject<?>> objects = this.getOrDefault(key, new ArrayList<>());
        boolean contains = objects.contains(value);
        objects.add(value);
        this.add(key, objects);
        return contains;
    }

    public boolean has(@NotNull TieredObjectImpl impl, @NotNull String id) {
        return this.get(impl, id) != null;
    }
}
