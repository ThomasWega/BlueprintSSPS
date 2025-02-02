package me.wega.blueprint_toolkit.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to initialize static fields of a class
 */
@UtilityClass
@ApiStatus.Experimental
public class InitializeStatic {

    /**
     * Initializes all static fields of the given class and its subclasses
     *
     * @param clazz Class to initialize
     */
    public static void initializeAll(@NotNull Class<?> clazz) {
        initialize(clazz);

        // Initialize static subclasses
        Class<?>[] subclasses = clazz.getDeclaredClasses();
        for (Class<?> subclass : subclasses) {
            initializeAll(subclass);
        }
    }

    /**
     * Initializes all static fields of the given class
     *
     * @param clazz Class to initialize
     * @see #initializeAll(Class)
     */
    @SneakyThrows
    public static void initialize(@NotNull Class<?> clazz) {
        Class.forName(clazz.getName());
    }
}