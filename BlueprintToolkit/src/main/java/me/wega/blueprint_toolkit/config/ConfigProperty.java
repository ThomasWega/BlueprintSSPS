package me.wega.blueprint_toolkit.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * A simple class to ease the use of config values.
 * One instance of this class represents one value in the config.
 * @param <T> The type of the property.
 */
@RequiredArgsConstructor
public class ConfigProperty<T> {
    private final @NotNull FileConfiguration config;
    private final @NotNull String path;

    /**
     * @return The value of the property.
     * @implNote Limitations:
     * <ul>
     *   <li>The value instance must be of the same type as the generic type.</li>
     *   <li>The value must be present in the config.</li>
     *   <li>The value must be non-null.</li>
     *   <li>Doesn't support Float, only Double.</li>
     *   <li>Doesn't support Long, only Integer.</li>
     * </ul>
     * @see FileConfiguration#get(String)
     */
    public T getValue() {
        return (T) config.get(path);
    }
}
