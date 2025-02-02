package me.wega.blueprint_toolkit.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple manager to ease the use of data sets.
 * @param <T> The type of data.
 */
public abstract class DataManager<T> {
    private final List<T> dataSet = new ArrayList<>();
    private final boolean allowDuplicates;

    public DataManager() {
        this.allowDuplicates = false;
    }

    public DataManager(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public boolean add(@NotNull T data) {
        if (!allowDuplicates && dataSet.contains(data)) return false;
        return dataSet.add(data);
    }

    /**
     * Add multiple data to the set.
     * @param data The data to add.
     * @return A list of duplicates (if disabled otherwise empty) that were not added.
     */
    public @NotNull Collection<T> addAll(@NotNull Collection<T> data) {
        List<T> duplicates = new ArrayList<>();
        for (T t : data) {
            if (!allowDuplicates && dataSet.contains(t)) duplicates.add(t);
            else dataSet.add(t);
        }
        return duplicates;
    }

    public void set(@NotNull Collection<T> data) {
        dataSet.clear();
        dataSet.addAll(data);
    }

    public @Nullable T get(int index) {
        return (dataSet.size() > index && index >= 0)
                ? dataSet.get(index)
                : null;
    }

    public boolean has(@NotNull T data) {
        return dataSet.contains(data);
    }

    public boolean remove(@NotNull T data) {
        return dataSet.remove(data);
    }

    public @NotNull List<T> getSet() {
        return dataSet;
    }

    /**
     * Clear the data set.
     */
    public void clear() {
        dataSet.clear();
    }

    public boolean isEmpty() {
        return dataSet.isEmpty();
    }
}
