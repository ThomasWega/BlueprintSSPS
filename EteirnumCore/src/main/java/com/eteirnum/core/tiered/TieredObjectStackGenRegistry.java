package com.eteirnum.core.tiered;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Registry for {@link TieredObjectStackGen} instances.
 */
@UtilityClass
public class TieredObjectStackGenRegistry {
    private static final Map<Class<? extends TieredObjectInstance<?>>, TieredObjectStackGen<?>> GENERATORS = Map.of(
    );

    /**
     * Gets the generator for the given class.
     *
     * @param clazz The class to get the generator for.
     * @param <T>   The type of the tiered object instance.
     * @return The generator for the given class or super class or super interface.
     */
    @SuppressWarnings("unchecked")
    public static <T extends TieredObjectInstance<?>> @Nullable TieredObjectStackGen<T> getGenerator(Class<T> clazz) {
        return (TieredObjectStackGen<T>) GENERATORS.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(new TieredObjectStackGen.Default<>());
    }
}
