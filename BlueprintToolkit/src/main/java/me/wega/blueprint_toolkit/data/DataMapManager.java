package me.wega.blueprint_toolkit.data;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A simple manager to ease the use of data maps.
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public abstract class DataMapManager<K, V> {
    private Map<K, V> dataMap;

    public DataMapManager() {
        this.dataMap = new HashMap<>();
    }

    /**
     * Create a new instance of the data map manager with a specific map type.
     * @param mapType The type of map to use.
     */
    @SneakyThrows
    public DataMapManager(@NotNull Class<? extends Map<K, V>> mapType) {
        this();
        this.dataMap = mapType.getConstructor((Class<?>) null).newInstance();
    }

    /**
     * Add a key-value pair to the data map.
     *
     * @param key   Key of type
     * @param value Value of type
     * @return True if the key-value pair was added, false if the key already exists and needed to be overwritten.
     */
    public boolean add(@NotNull K key, @NotNull V value) {
        return dataMap.put(key, value) == null;
    }

    /**
     * Add multiple key-value pairs to the data map.
     * @param data The data to add.
     * @return A map of values that were overwritten.
     */
    public @NotNull Map<K, V> addAll(@NotNull Map<K, V> data) {
        Map<K, V> unadded = new HashMap<>();
        for (Map.Entry<K, V> entry : data.entrySet()) {
            V oldValue = dataMap.put(entry.getKey(), entry.getValue());
            if (oldValue != null)
                unadded.put(entry.getKey(), oldValue);
        }
        return unadded;
    }

    /**
     * Check if the data map contains a specific value.
     * @param value The value to check for.
     * @return True if the value is present in the data map.
     */
    public boolean hasValue(@NotNull V value) {
        return dataMap.containsValue(value);
    }

    /**
     * Check if the data map contains a specific key.
     * @param key The key to check for.
     * @return True if the key is present in the data map.
     */
    public boolean has(@NotNull K key) {
        return dataMap.containsKey(key);
    }

    /**
     * Check if the data map contains a specific key-value pair.
     * @param key The key to check for.
     * @param value The value to check for.
     * @return True if the key-value pair is present in the data map.
     */
    public boolean has(@NotNull K key, @NotNull V value) {
        return dataMap.containsKey(key) && dataMap.get(key).equals(value);
    }

    /**
     * Remove a key-value pair from the data map.
     * @param key The key to remove.
     * @return True if the key-value pair was removed, false if the key did not exist.
     */
    public boolean remove(@NotNull K key) {
        return dataMap.remove(key) != null;
    }

    /**
     * Remove a value from the data map.
     * @param value The value to remove.
     * @return True if the value was removed, false if the value did not exist.
     */
    public boolean removeValue(@NotNull V value) {
        return dataMap.values().remove(value);
    }

    /**
     * Get a value from the data map.
     * @param key The key to get the value for.
     * @return The value for the key, or null if the key does not exist.
     */
    public @Nullable V get(@NotNull K key) {
        return dataMap.get(key);
    }

    /**
     * Get a value from the data map, or add it if it does not exist.
     * @param key The key to get the value for.
     * @param value The value to add if the key does not exist.
     * @return The value for the key, or the added value if the key did not exist.
     */
    @Contract("_, !null -> !null")
    public @Nullable V getOrAdd(@NotNull K key, @Nullable V value) {
        V finalVal = dataMap.get(key);
        if (finalVal == null) {
            dataMap.put(key, value);
            finalVal = value;
        }
        return finalVal;
    }

    /**
     * Get a value from the data map or null if it does not exist.
     * Then remove the value from the data map.
     *
     * @param key The key to get the value for.
     * @return The value for the key, or the default value if the key does not exist.
     */
    public @Nullable V getAndRemove(@NotNull K key) {
        return dataMap.remove(key);
    }

    /**
     * Get a value from the data map, or add a default value if it does not exist.
     * @param key The key to get the value for.
     * @param defaultValue The default value to add if the key does not exist.
     * @return The value for the key, or the default value if the key does not exist.
     */
    @Contract("_, !null -> !null")
    public @Nullable V getOrDefault(@NotNull K key, @Nullable V defaultValue) {
        return dataMap.getOrDefault(key, defaultValue);
    }

    /**
     * Get a value from the data map, or throw an exception if it does not exist.
     * @param key The key to get the value for.
     * @return The value for the key.
     * @throws NoSuchElementException If the key does not exist.
     */
    public @NotNull V getOrThrow(@NotNull K key) {
        V value = dataMap.get(key);
        if (value == null)
            throw new NoSuchElementException("Key not found in data map.");
        return value;
    }

    /**
     * Get a value from the data map, or throw a custom exception if it does not exist.
     * @param key The key to get the value for.
     * @param exception The exception to throw if the key does not exist.
     * @param <X> The type of exception to throw.
     * @return The value for the key.
     * @throws X If the key does not exist.
     */
    public <X extends Throwable> @NotNull V getOrThrow(@NotNull K key, @NotNull Supplier<@NotNull X> exception) throws X {
        V value = dataMap.get(key);
        if (value == null)
            throw exception.get();
        return value;
    }

    /**
     * Get a set of keys from the data map.
     * @return A set of keys.
     */
    public @NotNull Set<K> getKeys() {
        return dataMap.keySet();
    }

    /**
     * Get a list of values from the data map.
     * @return A list of values.
     */
    public @NotNull List<V> getValues() {
        return new ArrayList<>(dataMap.values());
    }

    /**
     * Get the data map.
     * @return A new map containing the data map.
     */
    public @NotNull Map<K, V> getMap() {
        return new HashMap<>(dataMap);
    }

    public void clear() {
        dataMap.clear();
    }

    /**
     * Retrieves keys with duplicate values in the data map.
     *
     * @return A list of duplicate keys.
     */
    public @NotNull Set<K> findDuplicateValues() {
        Set<V> uniqueValues = new HashSet<>();
        return dataMap.entrySet().stream()
                .filter(e -> !uniqueValues.add(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Set the data map to a new map.
     * @param data The new map to set.
     */
    public void set(@NotNull Map<K, V> data) {
        dataMap.clear();
        dataMap.putAll(data);
    }
}
